// Full CRUD end-to-end runner for all modules.
// Create -> Read(list) -> Read(byId) -> Update -> verify -> Delete -> verify(cleanup)
const fs = require('fs');
const path = require('path');
const http = require('http');

const BASE = 'http://localhost:8080/smart_restaurant/api/v1';
const { marker, modules } = JSON.parse(fs.readFileSync(path.join(__dirname, 'crud_payloads.json'), 'utf8'));

function request(method, urlPath, body, token) {
  return new Promise((resolve) => {
    const data = body ? JSON.stringify(body) : null;
    const url = new URL(BASE + urlPath);
    const headers = { 'Content-Type': 'application/json;charset=utf-8' };
    if (data) headers['Content-Length'] = Buffer.byteLength(data);
    if (token) headers['Authorization'] = 'Bearer ' + token;
    const req = http.request({ hostname: url.hostname, port: url.port, path: url.pathname + url.search, method, headers }, (res) => {
      let raw = '';
      res.on('data', (c) => (raw += c));
      res.on('end', () => {
        let json = null;
        try { json = JSON.parse(raw); } catch { json = { code: -1, message: 'non-json: ' + raw.slice(0, 120), httpStatus: res.statusCode }; }
        resolve({ httpStatus: res.statusCode, body: json });
      });
    });
    req.on('error', (e) => resolve({ httpStatus: 0, body: { code: -1, message: 'network: ' + e.message } }));
    if (data) req.write(data);
    req.end();
  });
}

(async () => {
  // 1. login
  const login = await request('POST', '/auth/login', { username: '13800000001', password: '123456' });
  if (!login.body.data || !login.body.data.token) {
    console.log('LOGIN FAILED, abort:', login.body);
    process.exit(1);
  }
  const token = login.body.data.token;
  console.log('Login OK\n');

  const results = [];
  for (const m of modules) {
    const r = { module: m.path, create: '-', readList: '-', readById: '-', update: '-', delete: '-', id: null, note: '' };
    try {
      // CREATE
      const created = await request('POST', '/' + m.path, m.payload, token);
      r.create = created.body.code === 0 ? 'PASS' : 'FAIL';
      if (created.body.code !== 0) { r.note = 'create: ' + created.body.message; results.push(r); console.log(`[FAIL] ${m.path.padEnd(22)} create -> ${created.body.code} ${created.body.message}`); continue; }

      // READ LIST - find the created row by marker field
      let found = null;
      for (let page = 1; page <= 5 && !found; page++) {
        const list = await request('GET', `/${m.path}?page=${page}&size=100`, null, token);
        if (list.body.code !== 0) { r.readList = 'FAIL'; r.note = 'list: ' + list.body.message; break; }
        const arr = (list.body.data && list.body.data.list) || [];
        if (m.markerField) {
          found = arr.find((x) => x[m.markerField] && String(x[m.markerField]).includes(marker));
        } else if (m.findField) {
          found = arr.find((x) => String(x[m.findField]) === String(m.findValue));
        } else {
          // no text marker: fall back to first row
          found = arr[0];
        }
        if (found) break;
      }
      r.readList = found ? 'PASS' : 'FAIL';
      if (!found) { r.note = 'created row not found in list'; results.push(r); console.log(`[FAIL] ${m.path.padEnd(22)} create OK but not found in list`); continue; }
      r.id = found.id;

      // READ BY ID
      const one = await request('GET', `/${m.path}/${found.id}`, null, token);
      r.readById = (one.body.code === 0 && one.body.data && String(one.body.data.id) === String(found.id)) ? 'PASS' : 'FAIL';

      // UPDATE: text tables change the marker field; junction tables flip the sentinel key
      if (m.markerField) {
        const updPayload = { [m.markerField]: (m.payload[m.markerField] || marker) + '-UPD' };
        const upd = await request('PUT', `/${m.path}/${found.id}`, updPayload, token);
        if (upd.body.code === 0) {
          const verify = await request('GET', `/${m.path}/${found.id}`, null, token);
          const changed = verify.body.data && String(verify.body.data[m.markerField] || '').includes('-UPD');
          r.update = changed ? 'PASS' : 'FAIL';
          if (!changed) r.note = 'update not persisted';
        } else { r.update = 'FAIL'; r.note = 'update: ' + upd.body.message; }
      } else if (m.findField) {
        const SENT2 = 7777;
        const upd = await request('PUT', `/${m.path}/${found.id}`, { [m.findField]: SENT2 }, token);
        if (upd.body.code === 0) {
          const verify = await request('GET', `/${m.path}/${found.id}`, null, token);
          const changed = verify.body.data && String(verify.body.data[m.findField]) === String(SENT2);
          r.update = changed ? 'PASS' : 'FAIL';
          if (!changed) r.note = 'junction update not persisted';
        } else { r.update = 'FAIL'; r.note = 'junction update: ' + upd.body.message; }
      } else {
        r.update = 'SKIP(no text field)';
      }

      // DELETE
      const del = await request('DELETE', `/${m.path}/${found.id}`, null, token);
      if (del.body.code === 0) {
        const after = await request('GET', `/${m.path}/${found.id}`, null, token);
        // after delete row should be gone (data null) for both hard & soft delete
        r.delete = (after.body.code === 0 && (after.body.data === null || after.body.data === undefined)) ? 'PASS' : 'FAIL';
        if (r.delete === 'FAIL') r.note = (r.note ? r.note + '; ' : '') + 'row still readable after delete';
      } else { r.delete = 'FAIL'; r.note = 'delete: ' + del.body.message; }
    } catch (e) {
      r.note = 'exception: ' + e.message;
    }
    results.push(r);
    const ok = (x) => x === 'PASS';
    const allOk = ok(r.create) && ok(r.readList) && ok(r.readById) && (ok(r.update) || r.update.startsWith('SKIP')) && ok(r.delete);
    console.log(`[${allOk ? 'PASS' : 'WARN'}] ${m.path.padEnd(22)} C:${r.create} Rl:${r.readList} Rg:${r.readById} U:${r.update} D:${r.delete}${r.note ? '  << ' + r.note : ''}`);
  }

  // Summary
  const summary = results.reduce((a, r) => {
    ['create','readList','readById','update','delete'].forEach((k) => {
      if (r[k] === 'PASS') a.pass++;
      else if (r[k] === 'FAIL') a.fail++;
      else a.skip++;
    });
    return a;
  }, { pass: 0, fail: 0, skip: 0 });
  const moduleFail = results.filter((r) => r.create === 'FAIL' || r.readList === 'FAIL' || r.readById === 'FAIL' || r.update === 'FAIL' || r.delete === 'FAIL').map((r) => r.module);
  console.log('\n===== CRUD MATRIX SUMMARY =====');
  console.log(`Modules: ${results.length}, operation checks: PASS ${summary.pass} / FAIL ${summary.fail} / SKIP ${summary.skip}`);
  console.log('Modules with any FAIL:', moduleFail.length ? moduleFail.join(', ') : 'NONE');
  fs.writeFileSync(path.join(__dirname, 'crud_report.json'), JSON.stringify({ marker, summary, results }, null, 2));
})();
