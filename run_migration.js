// 临时执行 SQL 迁移脚本
const mysql = require('mysql2/promise');

(async () => {
  const conn = await mysql.createConnection({
    host: '192.168.153.138', port: 3307, user: 'root', password: 'Flzx3qc_14yhl9t',
    database: 'smart_restaurant', charset: 'utf8mb4_general_ci'
  });

  // 1. 添加 level 列
  try {
    await conn.query('ALTER TABLE `sys_role` ADD COLUMN `level` INT NOT NULL DEFAULT 10 COMMENT "角色等级" AFTER `role_code`');
    console.log('ALTER OK: level column added');
  } catch (e) {
    if (e.code === 'ER_DUP_FIELDNAME') console.log('SKIP: level column already exists');
    else throw e;
  }

  // 2. 回填数据
  const updates = [
    "UPDATE sys_role SET level = 99 WHERE role_code = 'GENERAL_MANAGER'",
    "UPDATE sys_role SET level = 50 WHERE role_code = 'STORE_MANAGER'",
    "UPDATE sys_role SET level = 10 WHERE role_code IN ('WAITER','CASHIER','KITCHEN')"
  ];
  for (const u of updates) {
    const [r] = await conn.query(u);
    console.log('UPDATE OK:', u.substring(0, 50), 'affected:', r.affectedRows);
  }

  // 3. 验证
  const [rows] = await conn.query('SELECT id, role_name, role_code, level FROM sys_role ORDER BY id');
  console.log('\nsys_role 验证结果:');
  console.table(rows);

  await conn.end();
})();

