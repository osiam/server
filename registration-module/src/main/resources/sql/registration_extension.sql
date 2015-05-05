--
-- Extension data for registration purpose
--

INSERT INTO scim_extension VALUES (5, 'urn:scim:schemas:osiam:1.0:Registration');

INSERT INTO scim_extension_field (internal_id, is_required, name, type, extension_internal_id)
VALUES (6, FALSE, 'activationToken', 'STRING', 5);
INSERT INTO scim_extension_field (internal_id, is_required, name, type, extension_internal_id)
VALUES (7, FALSE, 'oneTimePassword', 'STRING', 5);
INSERT INTO scim_extension_field (internal_id, is_required, name, type, extension_internal_id)
VALUES (8, FALSE, 'emailConfirmToken', 'STRING', 5);
INSERT INTO scim_extension_field (internal_id, is_required, name, type, extension_internal_id)
VALUES (9, FALSE, 'tempMail', 'STRING', 5);
