# 1️⃣ Build-Phase mit allen Dev-Tools
FROM node:23-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

# Kopiere Source inkl. TypeScript-Konfig
COPY . .

# Baue die App (kompiliert auch next.config.ts automatisch)
RUN npm run build

# 2️⃣ Production-Phase
FROM node:23-alpine

WORKDIR /app

# Nur notwendige Files übernehmen
COPY --from=build /app/.next ./.next
COPY --from=build /app/public ./public
COPY --from=build /app/package.json ./package.json
COPY --from=build /app/node_modules ./node_modules

# ❌ next.config.ts wird NICHT benötigt zur Laufzeit

# Optional: Falls du Umgebungsvariablen brauchst
# COPY --from=build /app/.env ./.env

EXPOSE 8091

CMD ["npm", "run", "start"]
