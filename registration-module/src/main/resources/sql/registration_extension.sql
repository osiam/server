--
-- Updating hibernate sequence start value
--
SELECT pg_catalog.setval('hibernate_sequence', 10, false);

--
-- Extension data for registration purpose
--

Insert INTO scim_extension VALUES (5, 'urn:scim:schemas:osiam:1.0:Registration');

INSERT INTO scim_extension_field (internal_id, is_required, name, type, extension_internal_id)
	VALUES (6, false, 'activationToken', 'STRING', 5);
INSERT INTO scim_extension_field (internal_id, is_required, name, type, extension_internal_id)
	VALUES (7, false, 'oneTimePassword', 'STRING', 5);
INSERT INTO scim_extension_field (internal_id, is_required, name, type, extension_internal_id)
	VALUES (8, false, 'emailConfirmToken', 'STRING', 5);
INSERT INTO scim_extension_field (internal_id, is_required, name, type, extension_internal_id)
	VALUES (9, false, 'tempMail', 'STRING', 5);