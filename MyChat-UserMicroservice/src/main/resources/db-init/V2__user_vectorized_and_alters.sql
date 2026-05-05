-- Создаём (или пересоздаём) таблицу или VIEW user_vectorized
CREATE TABLE IF NOT EXISTS users_vectorized (
                                                user_tag VARCHAR(50) NOT NULL REFERENCES user_profiles(tag) ON DELETE CASCADE,
                                                gender TEXT,
                                                vector TEXT NOT NULL
);


ALTER TABLE user_temporal_data
    ADD COLUMN IF NOT EXISTS gender TEXT;
ALTER TABLE user_temporal_data
    ADD COLUMN IF NOT EXISTS vector TEXT;
