-- 테스트 계정
insert into user_account (user_id, user_password, role_types, nickname, email, memo, created_at, created_by, modified_at, modified_by) values
('joo', '{noop}asdf1234', 'ADMIN', 'joo', 'joo@mail.com', 'I am Joo.', now(), 'joo', now(), 'joo'),
('mark', '{noop}asdf1234', 'MANAGER', 'Mark', 'mark@mail.com', 'I am Mark.', now(), 'joo', now(), 'joo'),
('susan', '{noop}asdf1234', 'MANAGER,DEVELOPER', 'Susan', 'Susan@mail.com', 'I am Susan.', now(), 'joo', now(), 'joo'),
('jim', '{noop}asdf1234', 'USER', 'Jim', 'jim@mail.com', 'I am Jim.', now(), 'joo', now(), 'joo')
;