#!/usr/bin/env bash
set -e
echo "开始清理旧进程..."
PIDS=$(ps -ef | grep 'sec-backend' | grep java | grep -v grep | awk '{print $2}' || true)
if [ -n "$PIDS" ]; then
  echo "停止旧进程: $PIDS"
  kill $PIDS || true
  sleep 5
  kill -9 $PIDS 2>/dev/null || true
fi
echo "清理完成"