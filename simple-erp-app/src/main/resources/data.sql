INSERT INTO simpleerp.role (id, name)
SELECT nextval('simpleerp.role_seq'), 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT * FROM simpleerp.role WHERE name='ROLE_ADMIN');

INSERT INTO simpleerp.user (id, username, password, creation_date, is_deleted)
SELECT nextval('simpleerp.user_seq'), 'admin', '$2a$12$jlKUD04hlIt3/tjABhch7OfDO1Qf5G6aSnUlQq/FqpQF.7mZgCJrq', now(), false
WHERE NOT EXISTS (SELECT * FROM simpleerp.user WHERE username='admin');

INSERT INTO simpleerp.user_roles (user_id, role_id)
SELECT (SELECT id FROM simpleerp.user WHERE username='admin'), (SELECT id FROM simpleerp.role WHERE name='ROLE_ADMIN')
WHERE NOT EXISTS (SELECT * FROM simpleerp.user_roles
WHERE user_id=(SELECT id FROM simpleerp.user WHERE username='admin') AND role_id=(SELECT id FROM simpleerp.role WHERE name='ROLE_ADMIN'));

INSERT INTO simpleerp.role (id, name)
SELECT nextval('simpleerp.role_seq'), 'ROLE_WAREHOUSE'
WHERE NOT EXISTS (SELECT * FROM simpleerp.role WHERE name='ROLE_WAREHOUSE');

INSERT INTO simpleerp.role (id, name)
SELECT nextval('simpleerp.role_seq'), 'ROLE_PRODUCTION'
WHERE NOT EXISTS (SELECT * FROM simpleerp.role WHERE name='ROLE_PRODUCTION');

INSERT INTO simpleerp.role (id, name)
SELECT nextval('simpleerp.role_seq'), 'ROLE_TRADE'
WHERE NOT EXISTS (SELECT * FROM simpleerp.role WHERE name='ROLE_TRADE');

INSERT INTO simpleerp.task (id, name, user_id, role_id)
SELECT nextval('simpleerp.task_seq'), 'TASK_PURCHASE', (SELECT id FROM simpleerp.user WHERE username='admin'), (SELECT id FROM simpleerp.role WHERE name='ROLE_TRADE')
WHERE NOT EXISTS (SELECT * FROM simpleerp.task WHERE name='TASK_PURCHASE');

INSERT INTO simpleerp.task (id, name, user_id, role_id)
SELECT nextval('simpleerp.task_seq'), 'TASK_SALE', (SELECT id FROM simpleerp.user WHERE username='admin'), (SELECT id FROM simpleerp.role WHERE name='ROLE_TRADE')
WHERE NOT EXISTS (SELECT * FROM simpleerp.task WHERE name='TASK_SALE');

INSERT INTO simpleerp.task (id, name, user_id, role_id)
SELECT nextval('simpleerp.task_seq'), 'TASK_PRODUCTION', (SELECT id FROM simpleerp.user WHERE username='admin'), (SELECT id FROM simpleerp.role WHERE name='ROLE_PRODUCTION')
WHERE NOT EXISTS (SELECT * FROM simpleerp.task WHERE name='TASK_PRODUCTION');

INSERT INTO simpleerp.task (id, name, user_id, role_id)
SELECT nextval('simpleerp.task_seq'), 'TASK_INTERNAL_RELEASE', (SELECT id FROM simpleerp.user WHERE username='admin'), (SELECT id FROM simpleerp.role WHERE name='ROLE_WAREHOUSE')
WHERE NOT EXISTS (SELECT * FROM simpleerp.task WHERE name='TASK_INTERNAL_RELEASE');

INSERT INTO simpleerp.task (id, name, user_id, role_id)
SELECT nextval('simpleerp.task_seq'), 'TASK_INTERNAL_ACCEPTANCE', (SELECT id FROM simpleerp.user WHERE username='admin'), (SELECT id FROM simpleerp.role WHERE name='ROLE_WAREHOUSE')
WHERE NOT EXISTS (SELECT * FROM simpleerp.task WHERE name='TASK_INTERNAL_ACCEPTANCE');

INSERT INTO simpleerp.task (id, name, user_id, role_id)
SELECT nextval('simpleerp.task_seq'), 'TASK_EXTERNAL_RELEASE', (SELECT id FROM simpleerp.user WHERE username='admin'), (SELECT id FROM simpleerp.role WHERE name='ROLE_WAREHOUSE')
WHERE NOT EXISTS (SELECT * FROM simpleerp.task WHERE name='TASK_EXTERNAL_RELEASE');

INSERT INTO simpleerp.task (id, name, user_id, role_id)
SELECT nextval('simpleerp.task_seq'), 'TASK_EXTERNAL_ACCEPTANCE', (SELECT id FROM simpleerp.user WHERE username='admin'), (SELECT id FROM simpleerp.role WHERE name='ROLE_WAREHOUSE')
WHERE NOT EXISTS (SELECT * FROM simpleerp.task WHERE name='TASK_EXTERNAL_ACCEPTANCE');

INSERT INTO simpleerp.forecasting_properties (id, code, name, value, creation_date, is_valid)
SELECT nextval('simpleerp.forecasting_properties_seq'), 'CATEGORY_SEQUENCE_NUMBER', 'Sequence number for products category', '1', now(), true
    WHERE NOT EXISTS (SELECT * FROM simpleerp.forecasting_properties WHERE code='CATEGORY_SEQUENCE_NUMBER');

INSERT INTO simpleerp.forecasting_properties (id, code, name, value, creation_date, is_valid)
SELECT nextval('simpleerp.forecasting_properties_seq'), 'FORECASTING_ACTIVE', 'Flag is forecasting in active mode', 'NO', now(), true
    WHERE NOT EXISTS (SELECT * FROM simpleerp.forecasting_properties WHERE code='FORECASTING_ACTIVE');