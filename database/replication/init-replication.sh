#!/usr/bin/env bash
set -e
echo "===== 初始化 MySQL 主从复制 ====="

MASTER_HOST=${MASTER_HOST:-mysql-master}
SLAVE1_HOST=${SLAVE1_HOST:-mysql-slave1}
SLAVE2_HOST=${SLAVE2_HOST:-mysql-slave2}
ROOT_PASS=${ROOT_PASS:-Flzx3qc_14yhl9t}

docker exec sr-mysql-master mysql -uroot -p"$ROOT_PASS" -e "
  CREATE USER IF NOT EXISTS 'repl'@'%' IDENTIFIED BY 'Repl_123456';
  GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';
  FLUSH PRIVILEGES;
"

for SLAVE in sr-mysql-slave1 sr-mysql-slave2; do
  docker exec $SLAVE mysql -uroot -p"$ROOT_PASS" -e "
    CHANGE MASTER TO
      MASTER_HOST='$MASTER_HOST',
      MASTER_PORT=3306,
      MASTER_USER='repl',
      MASTER_PASSWORD='Repl_123456',
      MASTER_AUTO_POSITION=1;
    START SLAVE;
  "
  STATUS=$(docker exec $SLAVE mysql -uroot -p"$ROOT_PASS" -e "SHOW SLAVE STATUS\G" 2>/dev/null)
  IO_RUNNING=$(echo "$STATUS" | grep "Slave_IO_Running:" | awk '{print $2}')
  SQL_RUNNING=$(echo "$STATUS" | grep "Slave_SQL_Running:" | awk '{print $2}')
  echo "   $SLAVE: IO=$IO_RUNNING, SQL=$SQL_RUNNING"
done
echo "===== 主从复制初始化完成 ====="