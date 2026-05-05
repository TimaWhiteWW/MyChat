-- Аналитический SELECT логичнее выполнять вне init-скриптов,
-- но если нужно — он будет работать, так как users_vectorized уже создана:
WITH vector_parts AS (
    SELECT
        user_tag,
        i AS part_index,
        (string_to_array(vector,','))[i]::numeric AS part_value
    FROM users_vectorized,
         generate_subscripts(string_to_array(vector,','),1) AS i
)
SELECT
    part_index,
    max(part_value) - min(part_value) AS diff
FROM vector_parts
GROUP BY part_index
ORDER BY part_index;
