CREATE TABLE recommendation_statistics (
                                 gender VARCHAR(10) PRIMARY KEY, -- 'Male' или 'Female'
                                 avg_height DOUBLE PRECISION,
                                 avg_age INT,
                                 avg_weight DOUBLE PRECISION,
                                 avg_earnings BIGINT,
                                 avg_beauty DOUBLE PRECISION,
                                 avg_personality_extraversion DOUBLE PRECISION,
                                 avg_personality_openness DOUBLE PRECISION,
                                 avg_personality_conscientiousness DOUBLE PRECISION,
                                 avg_life_value_family DOUBLE PRECISION,
                                 avg_life_value_career DOUBLE PRECISION,
                                 avg_activity_level DOUBLE PRECISION
);
ALTER TABLE recommendation_statistics ADD COLUMN number BIGINT;

CREATE TABLE user_temporal_data(
    id BIGSERIAL PRIMARY KEY,
    user_tag VARCHAR(50) UNIQUE ,
    temporary_table INT,
    offset_users INT
);

DROP TABLE user_temporal_data;

ALTER TABLE user_temporal_data ADD COLUMN gender TEXT;
ALTER TABLE user_temporal_data ADD COLUMN vector TEXT;


WITH vector_parts AS (
    SELECT
        user_tag,
        i AS part_index,
        (string_to_array(vector, ','))[i]::numeric AS part_value
    FROM users_vectorized,
         generate_subscripts(string_to_array(vector, ','), 1) AS i
)
SELECT
    part_index,
    max(part_value) - min(part_value) AS diff
FROM vector_parts
GROUP BY part_index
ORDER BY part_index;
