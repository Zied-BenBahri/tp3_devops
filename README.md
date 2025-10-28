## Project layout

This repo is organized to separate application code, infrastructure, and CI pipelines.

Top-level folders:

- `app/` — Node.js app and tests
- `infra/docker/` — Dockerfile and Docker context rules
- `infra/k8s/base/` — Raw Kubernetes manifests
- `infra/helm/mon-app/` — Helm chart
- `ci/jenkins/` — Jenkins pipeline definitions

Quick commands:

- Run the app locally: `npm start` (from repo root; starts `app/index.js`)
- Test locally: `npm test`
- Build Docker: `docker build -f infra/docker/Dockerfile -t tp3-devops-hello .`
- Helm deploy path used by CI: `infra/helm/mon-app`

See `app/README.md` for app-specific details and Docker run instructions.
