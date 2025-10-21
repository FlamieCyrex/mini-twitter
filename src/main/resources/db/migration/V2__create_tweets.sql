CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE tweets(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    author_id  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content    VARCHAR(280) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT tweet_content_not_blank CHECK (length(btrim(content)) > 0)
);

CREATE INDEX idx_tweets_created_at_desc ON tweets(created_at DESC);
CREATE INDEX idx_tweets_author_id_created_at_desc on tweets(author_id, created_at DESC)