INSERT INTO simpleerp.role (id, name)
SELECT nextval('simpleerp.role_seq'), 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT * FROM simpleerp.role WHERE name='ROLE_ADMIN');

INSERT INTO simpleerp.user (id, username, password, creation_date)
SELECT nextval('simpleerp.user_seq'), 'admin', '$2a$12$jlKUD04hlIt3/tjABhch7OfDO1Qf5G6aSnUlQq/FqpQF.7mZgCJrq', now()
WHERE NOT EXISTS (SELECT * FROM simpleerp.user WHERE username='admin');

INSERT INTO simpleerp.user_roles (user_id, role_id)
SELECT (SELECT id FROM simpleerp.user WHERE username='admin'), (SELECT id FROM simpleerp.role WHERE name='ROLE_ADMIN')
WHERE NOT EXISTS (SELECT * FROM simpleerp.user_roles
WHERE user_id=(SELECT id FROM simpleerp.user WHERE username='admin') AND role_id=(SELECT id FROM simpleerp.role WHERE name='ROLE_ADMIN'));