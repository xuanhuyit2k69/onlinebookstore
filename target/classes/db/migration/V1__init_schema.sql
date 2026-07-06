-- OLMS initial schema (PostgreSQL 16)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE document (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    isbn            VARCHAR(20)  NOT NULL UNIQUE,
    title           VARCHAR(500) NOT NULL,
    author          VARCHAR(255) NOT NULL,
    publisher       VARCHAR(255),
    pub_year        INT,
    genre           VARCHAR(100),
    title_normalized  VARCHAR(500),
    author_normalized VARCHAR(255)
);

CREATE TABLE member (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    member_code  VARCHAR(50)  NOT NULL UNIQUE,
    full_name    VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    phone        VARCHAR(20)  UNIQUE,
    member_type  VARCHAR(20),
    expiry_date  DATE,
    is_active    BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE app_user (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username        VARCHAR(100) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    role            VARCHAR(30)  NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    failed_attempts INT NOT NULL DEFAULT 0,
    locked_until    TIMESTAMPTZ,
    member_id       UUID UNIQUE REFERENCES member(id)
);

CREATE TABLE copy (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    document_id     UUID NOT NULL REFERENCES document(id),
    barcode         VARCHAR(50) NOT NULL UNIQUE,
    shelf_location  VARCHAR(100),
    status          VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE'
);

CREATE TABLE loan_record (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    member_id    UUID NOT NULL REFERENCES member(id),
    copy_id      UUID NOT NULL REFERENCES copy(id),
    loan_date    DATE NOT NULL,
    due_date     DATE NOT NULL,
    return_date  DATE,
    status       VARCHAR(30) NOT NULL DEFAULT 'CHO_XAC_NHAN',
    fine_amount  BIGINT DEFAULT 0,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE fine_invoice (
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    loan_id    UUID UNIQUE REFERENCES loan_record(id),
    amount     NUMERIC(12,2) NOT NULL,
    status     VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    paid_at    TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE reservation (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    member_id    UUID NOT NULL REFERENCES member(id),
    document_id  UUID NOT NULL REFERENCES document(id),
    reserved_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    status       VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    queue_order  INT NOT NULL DEFAULT 0
);

CREATE TABLE refresh_token (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID NOT NULL REFERENCES app_user(id),
    token_hash  VARCHAR(255) NOT NULL UNIQUE,
    expires_at  TIMESTAMPTZ NOT NULL,
    revoked     BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_document_title_norm ON document(title_normalized);
CREATE INDEX idx_document_author_norm ON document(author_normalized);
CREATE INDEX idx_loan_status_created ON loan_record(status, created_at);
CREATE INDEX idx_copy_document_status ON copy(document_id, status);
