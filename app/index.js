// Minimal Node.js HTTP Hello World server (no external deps)
const http = require('http');

const PORT = process.env.PORT || 3000;

function handler(req, res) {
  res.statusCode = 200;
  res.setHeader('Content-Type', 'text/plain; charset=utf-8');
  res.end('Hello, World!\n');
}

function createServer() {
  return http.createServer(handler);
}

// If run directly: start the server
if (require.main === module) {
  createServer().listen(PORT, () => {
    console.log(`Server listening on http://127.0.0.1:${PORT}`);
  });
}

module.exports = { createServer };
