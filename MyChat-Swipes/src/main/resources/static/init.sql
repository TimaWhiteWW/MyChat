CREATE TABLE IF NOT EXISTS likes(
    user_tag TEXT,
    liked_user_tag TEXT,
    expiration_date timestamp,
    primary key (user_tag, liked_user_tag)
);

ALTER TABLE user_profiles ALTER COLUMN beauty TYPE DOUBLE PRECISION USING beauty::DOUBLE PRECISION;
