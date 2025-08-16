-- Initial data for Authentication Server Database
-- This file populates the database with default roles and admin user

-- Insert default roles
INSERT INTO roles (name, description) VALUES
    ('ADMIN', 'Full access to all system features, can manage users and roles'),
    ('MANAGER', 'Can manage products, inventory, and orders, but cannot manage users'),
    ('STAFF', 'Can view and create orders, limited product management'),
    ('VIEWER', 'Read-only access to products and inventory')
ON CONFLICT (name) DO NOTHING;

-- Insert default admin user
-- Password is 'admin123' (BCrypt encoded)
INSERT INTO users (username, email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('admin', 'admin@inventory.com', '$2a$10$TQp.B8ZjK7FgOKv3DXpE5.DH9G4W1xJ0M.qkYLlV.F4S8BcJ5b5jS', 'System', 'Administrator', true, true, true, true)
ON CONFLICT (username) DO NOTHING;

-- Insert demo manager user
-- Password is 'manager123' (BCrypt encoded)
INSERT INTO users (username, email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('manager', 'manager@inventory.com', '$2a$10$AsdK4QFG3wY9Jk8H.dK0lOHc8vB7N.qLm2RtE6Ws3X9fH2cJ8dKpO', 'Inventory', 'Manager', true, true, true, true)
ON CONFLICT (username) DO NOTHING;

-- Insert demo staff user
-- Password is 'staff123' (BCrypt encoded)
INSERT INTO users (username, email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('staff', 'staff@inventory.com', '$2a$10$VwXk3Lm9R.H6tY8G2nP4xODf7eA5B.pQr9StU1Zv4W7cI0mK5yNhG', 'Inventory', 'Staff', true, true, true, true)
ON CONFLICT (username) DO NOTHING;

-- Insert demo viewer user
-- Password is 'viewer123' (BCrypt encoded)
INSERT INTO users (username, email, password, first_name, last_name, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('viewer', 'viewer@inventory.com', '$2a$10$CbH9M1pL.Y3rT8K6nQ2wEjFd4vA7N.sRu0PvX5Zw9I2cJ7mO8fKhL', 'Inventory', 'Viewer', true, true, true, true)
ON CONFLICT (username) DO NOTHING;

-- Assign roles to users
-- Admin user gets ADMIN role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Manager user gets MANAGER role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'manager' AND r.name = 'MANAGER'
ON CONFLICT DO NOTHING;

-- Staff user gets STAFF role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'staff' AND r.name = 'STAFF'
ON CONFLICT DO NOTHING;

-- Viewer user gets VIEWER role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'viewer' AND r.name = 'VIEWER'
ON CONFLICT DO NOTHING;
