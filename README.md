<div align="center">

# TP3 DevOps – Node.js + Docker + Kubernetes + Helm + Jenkins

Minimal Node.js “Hello, World!” service containerized with Docker and deployable to Kubernetes via raw manifests or Helm. Jenkins pipelines are included for CI/CD.

</div>

## Contents

- Overview and prerequisites
- Project layout
- Local development (Node.js)
- Docker (build/run) and Docker Hub push
- Deploy to Kubernetes (raw manifests)
- Deploy with Helm
- Jenkins pipelines (credentials and variables)
- Troubleshooting (Windows/Git Bash tips, ports, Docker Hub tags)
- License

## Overview and prerequisites

This project provides a tiny HTTP server written in Node.js that responds with "Hello, World!" and includes:

- A production-ready Dockerfile (non-root user, Node 20-alpine base)
- Kubernetes manifests and a Helm chart
- Jenkins pipelines for both raw manifests and Helm deployments

Prerequisites:

- Node.js 18+ (for local run)
- Docker Desktop
- kubectl and a Kubernetes cluster (e.g., minikube)
- Helm (v3)
- Jenkins (optional) with Docker and Kubernetes access

## Project layout

This repo separates application code, infrastructure, and pipelines.

Top-level folders:

- `app/` — Node.js app and tests
- `infra/docker/` — Dockerfile and Docker context rules
- `infra/k8s/base/` — Raw Kubernetes manifests
- `infra/helm/mon-app/` — Helm chart
- `ci/jenkins/` — Jenkins pipeline definitions

Key files:

- `package.json` — scripts point to `app/index.js` and `app/test.js`
- `infra/docker/Dockerfile` — container build entry
- `infra/k8s/base/deployment.yaml` — containerPort 3000
- `infra/k8s/base/service.yaml` — NodePort 30080 -> targetPort 3000
- `infra/helm/mon-app/values.yaml` — `containerPort: 3000`

## Local development (Node.js)

From the repo root:

```bash
npm test   # quick self-check; should print PASSED
npm start  # starts the server on http://localhost:3000
```

Use another port:

```bash
PORT=8080 npm start
```

## Docker

Build the image (from repo root; note Dockerfile location):

```bash
docker build -f infra/docker/Dockerfile -t tp3-devops-hello .
```

Run the container:

```bash
docker run --rm -p 3000:3000 --name hello tp3-devops-hello
# open http://localhost:3000
```

### Push to Docker Hub

Replace `DOCKERHUB_USER` with your username (lowercase):

```bash
docker login
docker tag tp3-devops-hello:latest DOCKERHUB_USER/tp3-devops-hello:latest
docker push DOCKERHUB_USER/tp3-devops-hello:latest

# optional version tag
docker tag tp3-devops-hello:latest DOCKERHUB_USER/tp3-devops-hello:1.0.0
docker push DOCKERHUB_USER/tp3-devops-hello:1.0.0
```

Verify pushed tags at: https://hub.docker.com/r/DOCKERHUB_USER/tp3-devops-hello/tags

## Deploy to Kubernetes (raw manifests)

Apply manifests (NodePort service on 30080):

```bash
kubectl apply -f infra/k8s/base/deployment.yaml
kubectl apply -f infra/k8s/base/service.yaml

kubectl get pods
kubectl get svc mon-app-svc
```

Get the URL (NodePort):

```bash
NODE_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[0].address}')
echo "http://$NODE_IP:30080"
```

## Deploy with Helm

Install/upgrade release `mon-app` using the included chart:

```bash
helm upgrade --install mon-app infra/helm/mon-app \
	--set image.repository=DOCKERHUB_USER/tp3-devops-hello \
	--set image.tag=latest

helm status mon-app
```

## Jenkins pipelines

Pipelines are under `ci/jenkins/`.

- `pipeline-no-helm.groovy`

  - Optional param: `BUILD_AND_PUSH` to build and push Docker image
  - Applies raw manifests: `infra/k8s/base/*.yaml`

- `pipeline-helm.groovy`
  - Optional param: `BUILD_AND_PUSH` to build and push Docker image
  - Deploys with Helm from `infra/helm/mon-app`

Required Jenkins credentials/inputs:

- `dockerhub-creds` — Docker Hub username/password
- `kubeconfig-dev` — File credential containing kubeconfig

Both pipelines build using:

```
docker build -f infra/docker/Dockerfile -t $DOCKER_IMAGE .
```

## Troubleshooting

- Port 3000 is already in use

  - Change host port: `docker run -p 8080:3000 tp3-devops-hello`
  - Or free the port (Windows/PowerShell):
    ```powershell
    netstat -ano | findstr :3000
    taskkill /PID <PID> /F
    ```

- Docker Hub: `manifest unknown`

  - Ensure you pushed the tag you’re trying to pull:
    ```bash
    docker push DOCKERHUB_USER/tp3-devops-hello:latest
    # or push a version you built
    docker push DOCKERHUB_USER/tp3-devops-hello:1.0.0
    ```

- Git Bash path conversion (when mounting Docker socket)
  - Disable path conversion:
    ```bash
    MSYS_NO_PATHCONV=1 docker run ... -v //var/run/docker.sock:/var/run/docker.sock ...
    ```

## License

MIT
