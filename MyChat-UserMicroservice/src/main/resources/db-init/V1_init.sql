CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Базовые таблицы
CREATE TABLE IF NOT EXISTS user_profiles (
                                             id SERIAL PRIMARY KEY,
                                             tag VARCHAR(50) NOT NULL UNIQUE,
                                             name VARCHAR(100) NOT NULL,
                                             surname VARCHAR(100) NOT NULL,
                                             email VARCHAR(100) NOT NULL UNIQUE,
                                             phone_number VARCHAR(20),
                                             weight INT NOT NULL,
                                             height DOUBLE PRECISION NOT NULL,
                                             hobby VARCHAR(250),
                                             profession VARCHAR(250),
                                             earnings BIGINT,
                                             age INT NOT NULL,
                                             gender VARCHAR(10) CHECK (gender IN ('MALE','FEMALE')) NOT NULL,
                                             about_me TEXT,
                                             city VARCHAR(100) NOT NULL,
                                             country VARCHAR(100) NOT NULL,
                                             personality_extraversion DOUBLE PRECISION,
                                             personality_openness DOUBLE PRECISION,
                                             personality_conscientiousness DOUBLE PRECISION,
                                             life_value_family DOUBLE PRECISION,
                                             life_value_career DOUBLE PRECISION,
                                             activity_level DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS user_locations (
                                              id SERIAL PRIMARY KEY,
                                              user_tag VARCHAR(50) NOT NULL REFERENCES user_profiles(tag) ON DELETE CASCADE,
                                              city VARCHAR(100),
                                              country VARCHAR(100),
                                              location GEOMETRY(Point,4326)
);

CREATE TABLE IF NOT EXISTS user_pictures (
                                             id SERIAL PRIMARY KEY,
                                             user_tag VARCHAR(50) NOT NULL REFERENCES user_profiles(tag) ON DELETE CASCADE,
                                             picture_url VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_preferences (
                                                id SERIAL PRIMARY KEY,
                                                preferences_hobby VARCHAR(255),
                                                preferences_professions VARCHAR(255),
                                                min_earnings BIGINT,
                                                min_bmi DOUBLE PRECISION,
                                                max_bmi DOUBLE PRECISION,
                                                min_height DOUBLE PRECISION,
                                                max_height DOUBLE PRECISION,
                                                min_age DOUBLE PRECISION,
                                                max_age DOUBLE PRECISION,
                                                distance INT,
                                                rating INT DEFAULT 0,
                                                user_tag VARCHAR(50) NOT NULL REFERENCES user_profiles(tag) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS jwt_access_token (
                                                id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
                                                access_token TEXT NOT NULL,
                                                expiration_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS jwt_refresh_token (
                                                 id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
                                                 refresh_token TEXT NOT NULL,
                                                 expiration_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recommendation_statistics (
                                                         gender VARCHAR(10) PRIMARY KEY,
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
                                                         avg_activity_level DOUBLE PRECISION,
                                                         number BIGINT
);

CREATE TABLE IF NOT EXISTS user_temporal_data (
                                                  id BIGSERIAL PRIMARY KEY,
                                                  user_tag VARCHAR(50) UNIQUE,
                                                  temporary_table INT,
                                                  offset_users INT
);

CREATE TABLE IF NOT EXISTS likes (
                                     user_tag TEXT,
                                     liked_user_tag TEXT,
                                     expiration_date TIMESTAMP,
                                     PRIMARY KEY(user_tag, liked_user_tag)
);

ALTER TABLE user_profiles
    ADD COLUMN IF NOT EXISTS beauty INT DEFAULT 0;
