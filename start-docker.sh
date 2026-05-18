#!/usr/bin/env sh
set -eu

if [ ! -f .env ]; then
  cp .env.example .env
fi

while IFS= read -r line; do
  case "$line" in
    ""|\#*) continue ;;
  esac
  key="${line%%=*}"
  if ! grep -q "^${key}=" .env; then
    printf '%s\n' "$line" >> .env
  fi
done < .env.example

docker compose up --build -d
docker compose ps
