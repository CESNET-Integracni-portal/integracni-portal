

INSERT INTO organization_unit VALUES (1, 'default', 1000000);


INSERT INTO user_details VALUES (41, '$2a$10$NZveUAqH5vPPoYL76dJ8juH5g83VKHd6XNydVCuH9qGFIclKpU9Dq', 'test4', 1);
INSERT INTO user_details VALUES (45, '$2a$10$BJG6WTsuj3ebxYhrOT9rUecR42o.1oj9.D.Rgcz8dazc/uG9PBdFq', 'test3', 1);
INSERT INTO user_details VALUES (46, '$2a$10$BJG6WTsuj3ebxYhrOT9rUecR42o.1oj9.D.Rgcz8dazc/uG9PBdFq', 'test2', 1);
INSERT INTO user_details VALUES (80, '$2a$10$BJG6WTsuj3ebxYhrOT9rUecR42o.1oj9.D.Rgcz8dazc/uG9PBdFq', 'test1', 1);


INSERT INTO user_role VALUES (42, 'Administrator of externists.', 'externists_admin');
INSERT INTO user_role VALUES (43, 'An externist (can change his own password).', 'externist');
INSERT INTO user_role VALUES (44, 'Administrator of units.', 'units_admin');


INSERT INTO userrole_permissions VALUES (42, 'EDIT_EXTERNISTS');
INSERT INTO userrole_permissions VALUES (43, 'CHANGE_PASSWORD');
INSERT INTO userrole_permissions VALUES (44, 'EDIT_ORGANIZATIONAL_UNITS');


INSERT INTO user_details_user_role VALUES (41, 43);
INSERT INTO user_details_user_role VALUES (46, 43);
INSERT INTO user_details_user_role VALUES (45, 42);
INSERT INTO user_details_user_role VALUES (80, 42);

