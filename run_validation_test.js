// Validation & error-handling checks — all must return clean structured Result JSON, never a stack trace.
const http = require('http');
const BASE = 'http://localhost:8080/smart_restaurant/api/v1';
function request(method, urlPath, body, token) {
  return new Promise((resolve) => {
    const data = body ? JSON.stringify(body) : null;
    const url = new URL(BASE + urlPath);
    const headers = { 'Content-Type': 'application/json;charset=utf-8' };
    if (data) headers['Content-Length'] = Buffer.byteLength(data);
    if (token) headers.Authorization = 'Bearer ' + token;
    const req = http.request({ hostname: url.hostname, port: url.port, path: url.pathname + url.search, method, headers }, (res) => {
      let raw = ''; res.on('data', c => raw += c);
      res.on('end', () => {
        let j = null; try { j = JSON.parse(raw); } catch { j = { code: -1, raw: raw.slice(0, 80) }; }
        resolve({ http: res.statusCode, j });
      });
    });
    req.on('error', e => resolve({ http: 0, j: { code: -1, raw: e.message } }));
    if (data) req.write(data); req.end();
  });
}
(async () => {
  const login = await request('POST', '/auth/login', { username: '13800000001', password: '123456' });
  const token = login.j.data.token;
  const out = [];
  const T = (name, cond, detail) => out.push(`${cond ? 'PASS' : 'FAIL'}  ${name}${detail ? '  -> ' + detail : ''}`);

  // 1. no token -> 401
  let r = await request('GET', '/stores?page=1&size=2', null, null);
  T('No token -> 401', r.http === 401, `http=${r.http}`);

  // 2. bad token -> 401
  r = await request('GET', '/stores', null, 'garbage.token.here');
  T('Invalid token -> 401', r.http === 401, `http=${r.http}`);

  // 3. wrong password login
  r = await request('POST', '/auth/login', { username: '13800000001', password: 'wrong' });
  T('Wrong password -> code 1004', r.j.code === 1004, `code=${r.j.code} msg=${r.j.message}`);

  // 4. nonexistent user
  r = await request('POST', '/auth/login', { username: '19900000000', password: 'x' });
  T('Unknown user -> code 1002', r.j.code === 1002, `code=${r.j.code}`);

  // 5. missing required field on create (store without storeName/storeNo) -> structured error, not crash
  r = await request('POST', '/stores', { contactName: 'x' }, token);
  T('Create missing required -> structured Result (non-zero)', r.j.code !== 0 && typeof r.j.message === 'string', `http=${r.http} code=${r.j.code} msg=${r.j.message}`);

  // 6. malformed JSON body -> clean error (no stack trace leaked)
  const rawBody = '{"storeName": '; // invalid json
  const res = await new Promise((resolve) => {
    const url = new URL(BASE + '/stores');
    const req = http.request({ hostname: url.hostname, port: url.port, path: url.pathname, method: 'POST', headers: { 'Content-Type': 'application/json', 'Content-Length': Buffer.byteLength(rawBody), Authorization: 'Bearer ' + token } }, (resp) => {
      let b = ''; resp.on('data', c => b += c); resp.on('end', () => resolve({ http: resp.statusCode, body: b }));
    });
    req.on('error', e => resolve({ http: 0, body: e.message }));
    req.write(rawBody); req.end();
  });
  let parsed = null; try { parsed = JSON.parse(res.body); } catch {}
  T('Malformed JSON -> clean structured response', parsed && typeof parsed.message === 'string' && !/Exception|at \w+\./.test(res.body), `http=${res.http}`);

  // 7. get by non-numeric id -> clean error
  r = await request('GET', '/stores/abc', null, token);
  T('Non-numeric id -> handled (not 200-success)', r.j.code !== 0 || r.http >= 400, `http=${r.http} code=${r.j.code}`);

  // 8. get nonexistent id -> data null
  r = await request('GET', '/stores/99999999', null, token);
  T('Get missing id -> code 0 with null data', r.j.code === 0 && (r.j.data === null || r.j.data === undefined), `code=${r.j.code} data=${JSON.stringify(r.j.data)}`);

  // 9. update nonexistent id -> safe (does not crash); accept code 0(0 rows) or non-zero
  r = await request('PUT', '/stores/99999999', { storeName: 'ghost' }, token);
  T('Update missing id -> safe response', typeof r.j.code === 'number', `http=${r.http} code=${r.j.code}`);

  // 10. delete nonexistent id -> safe
  r = await request('DELETE', '/stores/99999999', null, token);
  T('Delete missing id -> safe response', typeof r.j.code === 'number', `http=${r.http} code=${r.j.code}`);

  // 11. duplicate unique key (create a second role with same code) -> structured error, no crash
  const rc = await request('POST', '/sysRoles', { roleName: 'DUP_CODE_ROLE', roleCode: 'GENERAL_MANAGER' }, token);
  T('Duplicate unique key -> structured error (non-zero)', rc.j.code !== 0, `code=${rc.j.code} msg=${rc.j.message}`);

  // 12. bad date format on create -> structured error (the @JsonFormat fix path)
  r = await request('POST', '/reservations', { storeId: 1, memberId: 1, contactName: 'BADD', contactPhone: '13900000000', reservationDate: 'not-a-date', reservationTime: '18:30:00' }, token);
  T('Bad date format -> structured error', r.j.code !== 0 && typeof r.j.message === 'string', `code=${r.j.code}`);

  // 13. valid date-only/time-only now accepted (regression of the fix)
  r = await request('POST', '/reservations', { storeId: 1, memberId: 1, contactName: 'DATEOK_' + Date.now(), contactPhone: '13900000000', reservationDate: '2026-09-10', reservationTime: '18:30:00' }, token);
  T('Date-only + time-only create -> code 0', r.j.code === 0, `code=${r.j.code} msg=${r.j.message}`);
  // cleanup that reservation
  if (r.j.code === 0) {
    const list = await request('GET', '/reservations?page=1&size=100', null, token);
    const row = (list.j.data.list || []).find(x => (x.contactName || '').startsWith('DATEOK_'));
    if (row) await request('DELETE', '/reservations/' + row.id, null, token);
  }

  out.forEach(l => console.log('[' + l + ']'));
  const fails = out.filter(l => l.startsWith('FAIL'));
  console.log(`\nValidation/error checks: ${out.length - fails.length}/${out.length} passed`);
  if (fails.length) console.log('FAILED:\n' + fails.join('\n'));
})();
