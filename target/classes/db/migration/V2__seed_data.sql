-- ========================================================
-- V2: Seed data — roles, permissions, fine policies
-- ========================================================

-- Roles
INSERT INTO roles (name, description) VALUES
    ('ROLE_ADMIN',     'System administrator with full access'),
    ('ROLE_LIBRARIAN', 'Library staff with operational access'),
    ('ROLE_MEMBER',    'Library member with borrowing access');

-- Permissions
INSERT INTO permissions (name, resource, action, description) VALUES
    ('BOOK_CREATE',       'BOOK',       'CREATE', 'Create books'),
    ('BOOK_READ',         'BOOK',       'READ',   'Read books'),
    ('BOOK_UPDATE',       'BOOK',       'UPDATE', 'Update books'),
    ('BOOK_DELETE',       'BOOK',       'DELETE', 'Delete books'),
    ('MEMBER_CREATE',     'MEMBER',     'CREATE', 'Create members'),
    ('MEMBER_READ',       'MEMBER',     'READ',   'Read members'),
    ('MEMBER_UPDATE',     'MEMBER',     'UPDATE', 'Update members'),
    ('MEMBER_DELETE',     'MEMBER',     'DELETE', 'Delete members'),
    ('LOAN_CREATE',       'LOAN',       'CREATE', 'Issue loans'),
    ('LOAN_READ',         'LOAN',       'READ',   'Read loans'),
    ('LOAN_UPDATE',       'LOAN',       'UPDATE', 'Update loans'),
    ('FINE_READ',         'FINE',       'READ',   'Read fines'),
    ('FINE_WAIVE',        'FINE',       'UPDATE', 'Waive fines'),
    ('REPORT_READ',       'REPORT',     'READ',   'View reports'),
    ('RESERVATION_CREATE','RESERVATION','CREATE', 'Place reservations'),
    ('RESERVATION_READ',  'RESERVATION','READ',   'Read reservations');

-- Assign all permissions to ROLE_ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r CROSS JOIN permissions p WHERE r.name = 'ROLE_ADMIN';

-- Assign librarian permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.name IN (
    'BOOK_CREATE','BOOK_READ','BOOK_UPDATE',
    'MEMBER_CREATE','MEMBER_READ','MEMBER_UPDATE',
    'LOAN_CREATE','LOAN_READ','LOAN_UPDATE',
    'FINE_READ','FINE_WAIVE','REPORT_READ',
    'RESERVATION_READ'
) WHERE r.name = 'ROLE_LIBRARIAN';

-- Assign member permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r JOIN permissions p ON p.name IN (
    'BOOK_READ','LOAN_READ','FINE_READ','RESERVATION_CREATE','RESERVATION_READ'
) WHERE r.name = 'ROLE_MEMBER';

-- Fine policies
INSERT INTO fine_policies (membership_type, daily_rate, max_fine, grace_period_days, max_loan_days, max_renewals, max_active_loans) VALUES
    ('STANDARD', 5.00,  500.00, 1,  14, 2, 5),
    ('PREMIUM',  2.00,  200.00, 3,  21, 3, 10),
    ('STUDENT',  1.00,  100.00, 2,  14, 2, 5);

-- Default admin staff account (password: Admin@123)
INSERT INTO staff (employee_code, email, first_name, last_name, password_hash, designation, role_id, is_active)
VALUES (
    'EMP-0001',
    'admin@library.com',
    'System',
    'Admin',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LedYSHRGbTYpQJVRi',
    'Administrator',
    (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'),
    TRUE
);
