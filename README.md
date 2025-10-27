# Node.js Hello World (Dockerized)

A minimal Node.js HTTP server that responds with `Hello, World!`, plus a Dockerfile to containerize it.

## Run locally

```bash
npm test   # quick self-check; should print PASSED
npm start  # starts the server on port 3000
```

Visit: http://localhost:3000

Set a custom port:

```bash
PORT=8080 npm start
```

## Build and run with Docker

```bash
# Build image (from repo root)
docker build -t tp3-devops-hello .

# Run container (maps host 3000 -> container 3000)
docker run --rm -p 3000:3000 --name hello tp3-devops-hello
```

Then open http://localhost:3000.

## Project structure

- `index.js` — HTTP server using Node's built-in `http` module
- `test.js` — self-test that starts the server on an ephemeral port and validates the response
- `package.json` — start and test scripts
- `Dockerfile` — container build instructions
- `.dockerignore` — excludes dev files from Docker context

## Notes

- No external dependencies required.
- Node 18+ recommended (uses CommonJS; no ESM config needed).
