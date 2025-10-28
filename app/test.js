// Simple self-test: starts server on ephemeral port and validates response
const http = require('http');
const { createServer } = require('./index');

const server = createServer();

server.listen(0, '127.0.0.1', () => {
  const address = server.address();
  const port = typeof address === 'object' && address ? address.port : 0;

  http
    .get({ hostname: '127.0.0.1', port, path: '/', agent: false }, (res) => {
      let data = '';
      res.on('data', (chunk) => (data += chunk));
      res.on('end', () => {
        const ok = res.statusCode === 200 && data.includes('Hello, World!');
        console.log(
          `Test ${ok ? 'PASSED' : 'FAILED'}: status=${res.statusCode} body="${data.trim()}"`
        );
        server.close(() => process.exit(ok ? 0 : 1));
      });
    })
    .on('error', (err) => {
      console.error('Request error:', err);
      server.close(() => process.exit(1));
    });
});
