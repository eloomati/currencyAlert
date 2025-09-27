-- Dodaj role
INSERT INTO roles (id, name)
VALUES (1, 'ROLE_USER'),
       (2, 'ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;

-- Dodaj użytkownika
INSERT INTO users (id, username, email, password_hash)
VALUES ('11111111-1111-1111-1111-111111111111', 'testuser', 'test@example.com', 'hashedpassword')
ON CONFLICT (username) DO NOTHING;

-- Dodaj przykładowe kursy walut
INSERT INTO exchange_rate_history (id, base, symbol, rate, as_of)
VALUES ('22222222-2222-2222-2222-222222222201', 'USD', 'PLN', 4.10, '2025-09-26T12:00:00Z'),
       ('22222222-2222-2222-2222-222222222202', 'USD', 'PLN', 4.15, '2025-09-27T12:00:00Z'),
       ('22222222-2222-2222-2222-222222222203', 'USD', 'EUR', 0.92, '2025-09-27T12:00:00Z'),
       ('33333333-3333-3333-3333-333333333301', 'EUR', 'USD', 1.10, '2025-09-26T12:00:00Z'),
       ('33333333-3333-3333-3333-333333333302', 'EUR', 'USD', 2.20, '2025-09-27T12:00:00Z')
ON CONFLICT DO NOTHING;