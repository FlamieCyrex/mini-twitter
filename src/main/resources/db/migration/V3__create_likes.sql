CREATE TABLE likes(
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    tweet_id UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_likes_user FOREIGN KEY (user_id)
                  REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fl_likes_tweet FOREIGN KEY (tweet_id)
                  REFERENCES tweets(id) ON DELETE CASCADE ,
    CONSTRAINT unique_like UNIQUE (user_id, tweet_id)

);