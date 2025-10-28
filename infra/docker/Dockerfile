# syntax=docker/dockerfile:1
FROM node:20-alpine

# Create non-root user
RUN addgroup -S nodejs && adduser -S nodeuser -G nodejs

WORKDIR /app

# Copy package files first (improves layer caching)
COPY package*.json ./

# No runtime deps, but if a lockfile exists, install for reproducibility
RUN if [ -f package-lock.json ]; then npm ci --omit=dev; fi

# Copy application source (moved under app/)
COPY app ./app

ENV NODE_ENV=production
EXPOSE 3000
USER nodeuser
CMD ["node", "app/index.js"]
