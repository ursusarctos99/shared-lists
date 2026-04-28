CREATE TABLE shared_list (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id    UUID        NOT NULL,
    title       TEXT        NOT NULL,
    description TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by  UUID        NOT NULL
);

CREATE TABLE list_entry (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    list_id    UUID        NOT NULL REFERENCES shared_list(id) ON DELETE CASCADE,
    title      TEXT        NOT NULL,
    url        TEXT,
    creator_id UUID        NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by UUID        NOT NULL
);

CREATE TABLE list_share (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    list_id    UUID        NOT NULL REFERENCES shared_list(id) ON DELETE CASCADE,
    user_id    UUID        NOT NULL,
    role       TEXT        NOT NULL CHECK (role IN ('EDITOR', 'VIEWER')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (list_id, user_id)
);

CREATE TABLE list_invite (
    id             UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    list_id        UUID        NOT NULL REFERENCES shared_list(id) ON DELETE CASCADE,
    invited_by      UUID        NOT NULL,
    invited_by_name TEXT        NOT NULL,
    invitee_email   TEXT        NOT NULL,
    role            TEXT        NOT NULL CHECK (role IN ('EDITOR', 'VIEWER')),
    status         TEXT        NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED')),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    expires_at     TIMESTAMPTZ NOT NULL DEFAULT now() + INTERVAL '7 days',
    accepted_at    TIMESTAMPTZ
);

CREATE INDEX ON list_entry (list_id);
CREATE INDEX ON list_share (list_id);
CREATE INDEX ON list_share (user_id);
CREATE INDEX ON list_invite (list_id);
CREATE INDEX ON list_invite (invitee_email);