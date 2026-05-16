-- Existing invite rows cannot be migrated (email→id lookup impossible in SQL alone)
DELETE FROM list_invite;

ALTER TABLE list_invite
    ADD COLUMN invitee_id UUID NOT NULL REFERENCES app_user(id);

ALTER TABLE list_invite
    DROP COLUMN invited_by_name,
    DROP COLUMN invitee_email;

DROP INDEX IF EXISTS list_invite_invitee_email_idx;
CREATE INDEX ON list_invite (invitee_id);