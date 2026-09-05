// Redis 协议探测：PING / AUTH / INFO
const net = require('net')
const s = net.connect(6379, '127.0.0.1')
let buf = ''
const send = (cmd) => s.write(cmd + '\r\n')
s.on('connect', () => {
  send('PING')
  send('AUTH Flzx3qc_14yhl9t')
  setTimeout(() => send('PING'), 300)
  setTimeout(() => send('INFO server'), 500)
})
s.on('data', (d) => {
  buf += d.toString()
})
s.on('error', (e) => console.log('连接错误:', e.message))
setTimeout(() => {
  console.log('--- 响应 ---')
  console.log(buf.split('\r\n').slice(0, 25).join('\n'))
  s.end()
  process.exit(0)
}, 1200)
