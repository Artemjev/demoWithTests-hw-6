ALTER TABLE users
    ADD COLUMN IF NOT EXISTS is_private boolean DEFAULT false;
