-- ALTER TABLE users
--     RENAME COLUMN is_private TO is_confirmed
--     ADD COLUMN IF NOT EXISTS is_private boolean DEFAULT false;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS is_confirmed boolean DEFAULT false;