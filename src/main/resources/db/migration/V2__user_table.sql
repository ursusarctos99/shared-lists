CREATE TABLE app_user (
    id           UUID        PRIMARY KEY,
    email        TEXT        NOT NULL UNIQUE,
    username     TEXT        NOT NULL,
    display_name TEXT        NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

ALTER TABLE shared_list ADD CONSTRAINT fk_shared_list_owner      FOREIGN KEY (owner_id)   REFERENCES app_user(id);
ALTER TABLE shared_list ADD CONSTRAINT fk_shared_list_updated_by FOREIGN KEY (updated_by) REFERENCES app_user(id);
ALTER TABLE list_entry  ADD CONSTRAINT fk_list_entry_creator     FOREIGN KEY (creator_id) REFERENCES app_user(id);
ALTER TABLE list_entry  ADD CONSTRAINT fk_list_entry_updated_by  FOREIGN KEY (updated_by) REFERENCES app_user(id);
ALTER TABLE list_share  ADD CONSTRAINT fk_list_share_user        FOREIGN KEY (user_id)    REFERENCES app_user(id);
ALTER TABLE list_invite ADD CONSTRAINT fk_list_invite_invited_by FOREIGN KEY (invited_by) REFERENCES app_user(id);
