#!/usr/bin/env sh
set -e

ES_URL="http://elasticsearch:9200"

# Ждём, пока кластер запустится
until curl -s "$ES_URL" >/dev/null; do
  echo "⏳ Ожидание Elasticsearch..."
  sleep 1
done
echo "✅ Elasticsearch доступен, создаём индексы…"

# Перебираем все нужные комбинации
for gender in female male; do
  for i in 1 2 3 4 5; do
    INDEX="vectorized_users_${gender}${i}"
    echo "— создаём индекс $INDEX"
    RESPONSE=$(curl -s -X PUT "$ES_URL/$INDEX" \
         -H 'Content-Type: application/json' \
         -d @/scripts/mapping.json)

    echo "$RESPONSE" | grep -q '"acknowledged":true' \
      && echo "   ✔ $INDEX создан" \
      || { echo "   ⚠ Ошибка при создании $INDEX:"; echo "$RESPONSE"; }
  done
done

echo "🏁 Готово."
