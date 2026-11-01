-- =============================================================================
-- V1__initial_schema.sql - Database Migration for Neon PostgreSQL
-- Description: Creates admins and inquiries tables with domain constraints and indexes.
-- =============================================================================

-- 1. Admins Table
CREATE TABLE IF NOT EXISTS admins (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Inquiries Table
CREATE TABLE IF NOT EXISTS inquiries (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    user_type VARCHAR(20) NOT NULL,
    category VARCHAR(30) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'New',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Restrict allowed values per specification
    CONSTRAINT chk_user_type CHECK (user_type IN ('Parent', 'Student', 'School')),
    CONSTRAINT chk_category CHECK (category IN ('Scholarship', 'Concert', 'Admission', 'General Query')),
    CONSTRAINT chk_status CHECK (status IN ('New', 'Contacted', 'Resolved'))
);

-- 3. Performance Indexes
CREATE INDEX IF NOT EXISTS idx_admins_email ON admins(email);
CREATE INDEX IF NOT EXISTS idx_inquiries_category ON inquiries(category);
CREATE INDEX IF NOT EXISTS idx_inquiries_user_type ON inquiries(user_type);
CREATE INDEX IF NOT EXISTS idx_inquiries_status ON inquiries(status);
CREATE INDEX IF NOT EXISTS idx_inquiries_created_at ON inquiries(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_inquiries_name ON inquiries(name);
CREATE INDEX IF NOT EXISTS idx_inquiries_phone ON inquiries(phone);
CREATE INDEX IF NOT EXISTS idx_inquiries_status_created_at ON inquiries(status, created_at DESC);
