-- ========================================================
-- V1: Create full schema for Library Management System
-- ========================================================

CREATE TABLE roles (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(200)
);

CREATE TABLE permissions (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(200),
    resource    VARCHAR(50),
    action      VARCHAR(20)
);

CREATE TABLE role_permissions (
    role_id       BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE publishers (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(200) NOT NULL UNIQUE,
    address TEXT,
    email   VARCHAR(150),
    phone   VARCHAR(30),
    website VARCHAR(300)
);

CREATE TABLE authors (
    id         BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    biography  TEXT,
    birth_date DATE,
    nationality VARCHAR(60),
    photo_url  VARCHAR(500)
);
CREATE INDEX idx_authors_first_name ON authors(first_name);
CREATE INDEX idx_authors_last_name  ON authors(last_name);

CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(300),
    slug        VARCHAR(100),
    parent_id   BIGINT REFERENCES categories(id)
);

CREATE TABLE books (
    id               BIGSERIAL PRIMARY KEY,
    isbn             VARCHAR(20)  NOT NULL UNIQUE,
    title            VARCHAR(500) NOT NULL,
    subtitle         VARCHAR(300),
    language         VARCHAR(10)  DEFAULT 'en',
    edition          VARCHAR(50),
    publication_year INTEGER,
    total_pages      INTEGER,
    description      TEXT,
    cover_image_url  VARCHAR(500),
    status           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    publisher_id     BIGINT REFERENCES publishers(id),
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP
);
CREATE INDEX idx_books_title    ON books(title);
CREATE INDEX idx_books_language ON books(language);

CREATE TABLE book_authors (
    book_id   BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES authors(id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, author_id)
);

CREATE TABLE book_categories (
    book_id     BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, category_id)
);

CREATE TABLE shelves (
    id               BIGSERIAL PRIMARY KEY,
    shelf_code       VARCHAR(20)  NOT NULL UNIQUE,
    location_floor   VARCHAR(20),
    location_section VARCHAR(50),
    capacity         INTEGER,
    description      TEXT,
    category_id      BIGINT REFERENCES categories(id)
);

CREATE TABLE book_copies (
    id               BIGSERIAL PRIMARY KEY,
    barcode          VARCHAR(50)  NOT NULL UNIQUE,
    book_id          BIGINT       NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    shelf_id         BIGINT       REFERENCES shelves(id),
    condition        VARCHAR(20)  NOT NULL DEFAULT 'GOOD',
    status           VARCHAR(20)  NOT NULL DEFAULT 'AVAILABLE',
    acquisition_date DATE,
    price            DECIMAL(10,2)
);
CREATE INDEX idx_book_copies_book_status ON book_copies(book_id, status);
CREATE INDEX idx_book_copies_barcode     ON book_copies(barcode);

CREATE TABLE members (
    id                BIGSERIAL PRIMARY KEY,
    member_code       VARCHAR(20)  NOT NULL UNIQUE,
    email             VARCHAR(150) NOT NULL UNIQUE,
    first_name        VARCHAR(100) NOT NULL,
    last_name         VARCHAR(100) NOT NULL,
    password_hash     VARCHAR(255) NOT NULL,
    phone             VARCHAR(20),
    address           TEXT,
    date_of_birth     DATE,
    membership_type   VARCHAR(20)  NOT NULL DEFAULT 'STANDARD',
    membership_expiry DATE,
    status            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    role_id           BIGINT REFERENCES roles(id),
    created_at        TIMESTAMP
);

CREATE TABLE staff (
    id            BIGSERIAL PRIMARY KEY,
    employee_code VARCHAR(20)  NOT NULL UNIQUE,
    email         VARCHAR(150) NOT NULL UNIQUE,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone         VARCHAR(20),
    designation   VARCHAR(100),
    role_id       BIGINT REFERENCES roles(id),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE loans (
    id            BIGSERIAL PRIMARY KEY,
    member_id     BIGINT    NOT NULL REFERENCES members(id),
    book_copy_id  BIGINT    NOT NULL REFERENCES book_copies(id),
    issued_by     BIGINT    REFERENCES staff(id),
    returned_to   BIGINT    REFERENCES staff(id),
    issue_date    TIMESTAMP NOT NULL,
    due_date      DATE      NOT NULL,
    return_date   TIMESTAMP,
    status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    renewal_count INTEGER     NOT NULL DEFAULT 0,
    notes         TEXT
);
CREATE INDEX idx_loans_member_status ON loans(member_id, status);
CREATE INDEX idx_loans_due_date      ON loans(due_date);

CREATE TABLE reservations (
    id               BIGSERIAL PRIMARY KEY,
    member_id        BIGINT    NOT NULL REFERENCES members(id),
    book_id          BIGINT    NOT NULL REFERENCES books(id),
    reservation_date TIMESTAMP NOT NULL DEFAULT NOW(),
    expiry_date      TIMESTAMP,
    queue_position   INTEGER,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notified_at      TIMESTAMP
);

CREATE TABLE fines (
    id          BIGSERIAL PRIMARY KEY,
    member_id   BIGINT       NOT NULL REFERENCES members(id),
    loan_id     BIGINT       REFERENCES loans(id),
    fine_type   VARCHAR(20)  NOT NULL,
    amount      DECIMAL(10,2) NOT NULL,
    paid_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    issued_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    paid_at     TIMESTAMP,
    waived_by   BIGINT REFERENCES staff(id)
);

CREATE TABLE reviews (
    id          BIGSERIAL PRIMARY KEY,
    member_id   BIGINT     NOT NULL REFERENCES members(id),
    book_id     BIGINT     NOT NULL REFERENCES books(id),
    rating      SMALLINT   NOT NULL CHECK (rating BETWEEN 1 AND 5),
    review_text TEXT,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reviewed_at TIMESTAMP  NOT NULL DEFAULT NOW(),
    UNIQUE (member_id, book_id)
);
CREATE INDEX idx_reviews_member_book ON reviews(member_id, book_id);

CREATE TABLE notifications (
    id           BIGSERIAL PRIMARY KEY,
    recipient_id BIGINT      NOT NULL REFERENCES members(id),
    type         VARCHAR(30) NOT NULL,
    channel      VARCHAR(20) NOT NULL,
    subject      VARCHAR(300),
    body         TEXT,
    is_read      BOOLEAN     NOT NULL DEFAULT FALSE,
    sent_at      TIMESTAMP,
    created_at   TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE TABLE fine_policies (
    id               BIGSERIAL PRIMARY KEY,
    membership_type  VARCHAR(20)   NOT NULL UNIQUE,
    daily_rate       DECIMAL(6,2)  NOT NULL,
    max_fine         DECIMAL(8,2)  NOT NULL,
    grace_period_days SMALLINT     NOT NULL DEFAULT 0,
    max_loan_days    SMALLINT      NOT NULL DEFAULT 14,
    max_renewals     SMALLINT      NOT NULL DEFAULT 2,
    max_active_loans SMALLINT      NOT NULL DEFAULT 5
);
