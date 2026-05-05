CREATE EXTENSION IF NOT EXISTS postgis;


CREATE TABLE IF NOT EXISTS user_profiles (
                                             id SERIAL PRIMARY KEY,                         -- Уникальный идентификатор пользователя
                                             tag VARCHAR(50) NOT NULL UNIQUE,               -- Имя пользователя
                                             name VARCHAR(100) NOT NULL,                    -- Имя
                                             surname VARCHAR(100) NOT NULL,                 -- Фамилия
                                             email VARCHAR(100) NOT NULL UNIQUE,            -- Электронная почта
                                             phone_number VARCHAR(20),                      -- Номер телефона (необязательный)
                                             weight INT NOT NULL,                           -- Вес пользователя
                                             height DOUBLE PRECISION NOT NULL,              -- Рост пользователя
                                             hobby VARCHAR(250),                             -- Хобби пользователя (enum в Java)
                                             profession VARCHAR(250),                        -- Профессия пользователя (enum в Java)
                                             earnings BIGINT,                               -- Заработок пользователя
                                             age INT NOT NULL,                              -- Возраст пользователя
                                             gender VARCHAR(10) CHECK (gender IN ('MALE', 'FEMALE')) NOT NULL,

                                             about_me TEXT,                                 -- Описание профиля
                                             city VARCHAR(100) NOT NULL,                    -- Город проживания
                                             country VARCHAR(100) NOT NULL,                 -- Страна
    -- Дополнительные параметры для векторизации профиля:
                                             personality_extraversion DOUBLE PRECISION,     -- Экстраверсия (0-10)
                                             personality_openness DOUBLE PRECISION,         -- Открытость новому опыту (0-10)
                                             personality_conscientiousness DOUBLE PRECISION,  -- Добросовестность (0-10)
                                             life_value_family DOUBLE PRECISION,            -- Важность семьи (0-10)
                                             life_value_career DOUBLE PRECISION,            -- Важность карьеры (0-10)
                                             activity_level DOUBLE PRECISION                -- Уровень активности (0-10)
);


CREATE TABLE IF NOT EXISTS user_locations (
                                              id SERIAL PRIMARY KEY,
                                              user_tag VARCHAR(50) NOT NULL,                    -- Используем tag вместо user_id
                                              city VARCHAR(100),
                                              country VARCHAR(100),
                                              location GEOMETRY(Point, 4326),
                                              FOREIGN KEY (user_tag) REFERENCES user_profiles(tag) ON DELETE CASCADE  -- Внешний ключ ссылается на tag
);

CREATE TABLE IF NOT EXISTS user_pictures (
                                                id SERIAL PRIMARY KEY,
                                                user_tag VARCHAR(50) NOT NULL,                    -- Используем tag вместо user_id
                                                picture_url VARCHAR(255) NOT NULL,
                                                FOREIGN KEY (user_tag) REFERENCES user_profiles(tag) ON DELETE CASCADE  -- Внешний ключ ссылается на tag
);

CREATE TABLE user_preferences (
                             id SERIAL PRIMARY KEY,
                             preferences_hobby VARCHAR(255),  -- Строка для хобби
                             preferences_professions VARCHAR(255),  -- Строка для профессий
                             min_earnings BIGINT,
                             min_bmi FLOAT8,
                             max_bmi FLOAT8,
                             min_height FLOAT8,
                             max_height FLOAT8,
                             min_age FLOAT8,
                             max_age FLOAT8,
                             distance INT,
                             rating INT DEFAULT 0,
                             user_tag VARCHAR(50) NOT NULL,                    -- Используем tag вместо id
                             FOREIGN KEY (user_tag) REFERENCES user_profiles(tag) ON DELETE CASCADE  -- Внешний ключ ссылается на tag
);


create table jwt_access_token
(
    id              uuid default uuid_generate_v4() not null
        constraint jwt_token_pkey
            primary key,
    access_token    text                            not null,
    expiration_date timestamp
);

alter table jwt_access_token
    owner to postgres;


create table jwt_refresh_token
(
    id              uuid default uuid_generate_v4() not null
        primary key,
    refresh_token   text                            not null,
    expiration_date timestamp
);

alter table jwt_refresh_token
    owner to postgres;

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

CREATE TABLE IF NOT EXISTS likes(
                                    user_tag TEXT,
                                    liked_user_tag TEXT,
                                    expiration_date timestamp,
                                    primary key (user_tag, liked_user_tag)
);

ALTER TABLE user_profiles ALTER COLUMN beauty TYPE DOUBLE PRECISION USING beauty::DOUBLE PRECISION;


ALTER TABLE user_profiles ADD COLUMN beauty INT;
ALTER TABLE user_profiles ALTER COLUMN beauty SET DEFAULT 0;