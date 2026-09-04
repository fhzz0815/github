// Generate per-module create payloads by parsing CREATE TABLE constraints.
// Required insert fields = NOT NULL && no DEFAULT && not auto_increment.
const fs = require('fs');
const path = require('path');

const sql = fs.readFileSync(path.join(__dirname, 'database', 'smart_restaurant.sql'), 'utf8');

function parseTables(sql) {
  const tables = [];
  const re = /CREATE TABLE\s+`(\w+)`\s*\(([\s\S]*?)\)\s*(?:ENGINE|;)/g;
  let m;
  while ((m = re.exec(sql)) !== null) {
    const tableName = m[1];
    const body = m[2];
    const cols = [];
    for (const line of body.split('\n')) {
      const t = line.trim().replace(/,$/, '');
      if (!t) continue;
      // Type may contain a comma, e.g. DECIMAL(10,2) -> capture it inside the parens.
      const cm = t.match(/^`(\w+)`\s+([A-Za-z]+(?:\s+UNSIGNED)?(?:\s*\([^)]*\))?)\s*([\s\S]*)$/);
      if (!cm) continue; // KEY/PRIMARY etc
      const name = cm[1];
      const typePart = cm[2];
      const rest = cm[3] || '';
      const rawType = typePart.replace(/\(.*\)/, '').trim().toLowerCase();
      const isNotNull = /NOT NULL/i.test(rest);
      const hasDefault = /DEFAULT\s+/i.test(rest);
      const isAutoInc = /AUTO_INCREMENT/i.test(rest);
      const onUpdate = /ON UPDATE/i.test(rest);
      const cmt = (rest.match(/COMMENT\s+'([^']*)'/i) || [])[1] || '';
      cols.push({ name, rawType, isNotNull, hasDefault, isAutoInc, onUpdate, comment: cmt });
    }
    tables.push({ name: tableName, cols });
  }
  return tables;
}

function toCamel(s) { return s.replace(/_([a-z])/g, (_, c) => c.toUpperCase()); }
function toPascal(s) { const c = toCamel(s); return c.charAt(0).toUpperCase() + c.slice(1); }

// Authoritative table -> API path map read from controller source
const CTRL_DIR = path.join(__dirname, 'sec-backend', 'src', 'main', 'java', 'com', 'iwe3', 'sec', 'controller');
function controllerPath(tableName) {
  const ctrlFile = path.join(CTRL_DIR, toPascal(tableName) + 'Controller.java');
  if (fs.existsSync(ctrlFile)) {
    const src = fs.readFileSync(ctrlFile, 'utf8');
    const mm = src.match(/@RequestMapping\("\/api\/v1\/([^"]+)"\)/);
    if (mm) return mm[1];
  }
  return null;
}

const tables = parseTables(sql);
const marker = 'CRUD' + Date.now().toString().slice(-6);

// Generate a safe test value per column
function testValue(col, tableName) {
  const n = col.name.toLowerCase();
  const t = col.rawType;
  const isNum = /int|decimal|double|float|tinyint|bigint/.test(t);
  const isDate = /datetime|timestamp|date|time/.test(t);

  // foreign-key-like id columns -> use existing id 1
  if (n.endsWith('_id') || n === 'store_id' || n === 'member_id') {
    if (n === 'id') return undefined;
    return 1;
  }
  if (isDate) {
    if (n === 'create_time' || n === 'update_time') return undefined; // DB defaults
    if (t === 'date') return '2026-09-10';                       // DATE only
    if (t === 'time') return '18:30:00';                         // TIME only
    return '2026-09-10 18:30:00';                                // datetime/timestamp
  }
  if (t === 'tinyint') {
    if (n.includes('status') || n.includes('state')) return 1;
    if (n.startsWith('is_') || n.includes('_enabled') || n.includes('deleted')) return 0;
    if (n.includes('type') || n.includes('mode') || n.includes('channel') || n.includes('source') || n.includes('pay')) return 1;
    return 1;
  }
  if (isNum) {
    if (n.includes('amount') || n.includes('price') || n.includes('fee') || n.includes('balance') || n.includes('money')) return 1.00;
    if (n.includes('count') || n.includes('quantity') || n.includes('num') || n.includes('qty') || n.includes('stock') || n.includes('person')) return 1;
    if (n.includes('sort') || n.includes('order')) return 1;
    if (n.includes('rate') || n.includes('discount') || n.includes('radius') || n.includes('longitude') || n.includes('latitude')) return 1.0;
    return 1;
  }
  // phone-like columns get a phone number (check BEFORE generic name rule so contact_name keeps marker)
  if (n.includes('phone') || n.includes('mobile') || n.includes('tel')) return '13900000000';
  // varchar/text -> unique marker where it looks like a name/no/code
  if (n.includes('name') || n.includes('no') || n === 'username' || n.includes('code') || n.includes('title') || n.includes('content') || n.includes('remark') || n.includes('reason') || n.includes('description') || n.includes('address') || n.includes('pay_no') || n.includes('order_no') || n.includes('refund_no') || n.includes('check_no') || n.includes('coupon')) {
    return marker + n.slice(0,3);
  }
  if (n.includes('password')) return '123456';
  if (n.includes('email')) return 'crud@test.com';
  if (n.includes('time')) return '2026-09-04 12:00:00';
  return marker;
}

// Unique-key / junction tables: use a sentinel FK so inserts don't collide with
// seed data (DB has NO enforced foreign keys, verified). Rows are located via findField.
const SENT = 8888;
const SENTINELS = {
  sys_role_permission: { overrides: { roleId: SENT, permissionId: SENT }, findField: 'permissionId' },
  dish_stock:           { overrides: { dishId: SENT },                              findField: 'dishId' },
  ingredient_stock:     { overrides: { ingredientId: SENT },                        findField: 'ingredientId' },
  member_coupon:        { overrides: { couponId: SENT },                            findField: 'couponId' },
  store_payment_setting:{ overrides: { storeId: SENT },                             findField: 'storeId' },
  dish_ingredient_rel:  { overrides: { ingredientId: SENT },                        findField: 'ingredientId' },
  staff_schedule:       { overrides: { staffId: SENT } }, // avoids uk_staff_date collision; located via remark marker
};

const modules = tables.map(tbl => {
  const path2 = controllerPath(tbl.name);
  const required = tbl.cols.filter(c =>
    c.isNotNull && !c.hasDefault && !c.isAutoInc && !c.onUpdate &&
    c.name !== 'create_time' && c.name !== 'update_time'
  );
  const payload = {};
  required.forEach(c => {
    const v = testValue(c, tbl.name);
    if (v !== undefined) payload[toCamel(c.name)] = v;
  });
  let findField = null;
  const sp = SENTINELS[tbl.name];
  if (sp) { Object.assign(payload, sp.overrides); findField = sp.findField; }
  // Ensure a recognizable marker field for list lookup; add a name-ish field if present
  const nameCol = tbl.cols.find(c => /name|title|content|remark|reason|no/.test(c.name.toLowerCase()) && !c.name.endsWith('_id'));
  let markerField = null;
  if (nameCol) {
    markerField = toCamel(nameCol.name);
    if (!(markerField in payload)) payload[markerField] = marker + 'M';
  }
  return { table: tbl.name, path: path2, payload, markerField, findField, findValue: SENT, marker };
});

fs.writeFileSync(path.join(__dirname, 'crud_payloads.json'), JSON.stringify({ marker, modules }, null, 2));
console.log('Generated payloads for', modules.length, 'modules; marker =', marker);
modules.forEach(m => console.log(`  ${m.path.padEnd(24)} required fields: ${Object.keys(m.payload).join(', ')}`));
