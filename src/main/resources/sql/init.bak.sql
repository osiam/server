CREATE TABLE database_scheme_version (
  version DOUBLE PRECISION NOT NULL
);


CREATE SEQUENCE hibernate_sequence
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

CREATE TABLE scim_address (
  id                               BIGINT NOT NULL,
  country                          CHARACTER VARYING(255),
  formatted                        CHARACTER VARYING(255),
  locality                         CHARACTER VARYING(255),
  postalcode                       CHARACTER VARYING(255),
  postgresql_does_not_like_primary BOOLEAN,
  region                           CHARACTER VARYING(255),
  streetaddress                    CHARACTER VARYING(255),
  type                             CHARACTER VARYING(255),
  user_internal_id                 BIGINT
);

CREATE TABLE scim_certificate (
  value            CHARACTER VARYING(255) NOT NULL,
  user_internal_id BIGINT
);

CREATE TABLE scim_email (
  value                            CHARACTER VARYING(255) NOT NULL,
  postgresql_does_not_like_primary BOOLEAN,
  type                             CHARACTER VARYING(255),
  user_internal_id                 BIGINT                 NOT NULL
);

CREATE TABLE scim_enterprise (
  id             BIGINT NOT NULL,
  costcenter     CHARACTER VARYING(255),
  department     CHARACTER VARYING(255),
  division       CHARACTER VARYING(255),
  employeenumber CHARACTER VARYING(255),
  organization   CHARACTER VARYING(255),
  manager_id     BIGINT
);

CREATE TABLE scim_entitlements (
  value CHARACTER VARYING(255) NOT NULL
);

CREATE TABLE scim_group (
  internal_id BIGINT                 NOT NULL,
  displayname CHARACTER VARYING(255) NOT NULL UNIQUE,
  externalid  CHARACTER VARYING(255),
  id          UUID                   NOT NULL UNIQUE,
  additional  CHARACTER VARYING(255)
);

CREATE TABLE scim_group_internalidskeleton (
  scim_group_internal_id BIGINT NOT NULL,
  members_internal_id    BIGINT NOT NULL
);

CREATE TABLE scim_im (
  value            CHARACTER VARYING(255) NOT NULL,
  type             CHARACTER VARYING(255),
  user_internal_id BIGINT
);

CREATE TABLE scim_manager (
  id          BIGINT NOT NULL,
  displayname CHARACTER VARYING(255),
  managerid   BYTEA
);

CREATE TABLE scim_meta (
  id           BIGINT NOT NULL,
  created      TIMESTAMP WITHOUT TIME ZONE,
  lastmodified TIMESTAMP WITHOUT TIME ZONE,
  location     CHARACTER VARYING(255),
  version      CHARACTER VARYING(255)
);

CREATE TABLE scim_name (
  id              BIGINT NOT NULL,
  familyname      CHARACTER VARYING(255),
  formatted       CHARACTER VARYING(255),
  givenname       CHARACTER VARYING(255),
  honorificprefix CHARACTER VARYING(255),
  honorificsuffix CHARACTER VARYING(255),
  middlename      CHARACTER VARYING(255)
);

CREATE TABLE scim_phonenumber (
  value            CHARACTER VARYING(255) NOT NULL,
  type             CHARACTER VARYING(255),
  user_internal_id BIGINT
);

CREATE TABLE scim_photo (
  value            CHARACTER VARYING(255) NOT NULL,
  type             CHARACTER VARYING(255),
  user_internal_id BIGINT
);

CREATE TABLE scim_roles (
  value CHARACTER VARYING(255) NOT NULL
);

CREATE TABLE scim_user (
  internal_id       BIGINT                 NOT NULL,
  displayname       CHARACTER VARYING(255),
  externalid        CHARACTER VARYING(255),
  id                UUID                   NOT NULL,
  active            BOOLEAN,
  locale            CHARACTER VARYING(255),
  nickname          CHARACTER VARYING(255),
  password          CHARACTER VARYING(255) NOT NULL,
  preferredlanguage CHARACTER VARYING(255),
  profileurl        CHARACTER VARYING(255),
  timezone          CHARACTER VARYING(255),
  title             CHARACTER VARYING(255),
  username          CHARACTER VARYING(255) NOT NULL UNIQUE,
  usertype          CHARACTER VARYING(255),
  name_id           BIGINT
);

CREATE TABLE scim_user_additional (
  id         BIGINT NOT NULL,
  additional CHARACTER VARYING(255)
);

CREATE TABLE scim_user_scim_address (
  scim_user_internal_id BIGINT NOT NULL,
  addresses_id          BIGINT NOT NULL
);

CREATE TABLE scim_user_scim_entitlements (
  scim_user_internal_id BIGINT                 NOT NULL,
  entitlements_value    CHARACTER VARYING(255) NOT NULL
);

CREATE TABLE scim_user_scim_group (
  scim_user_internal_id BIGINT NOT NULL,
  groups_internal_id    BIGINT NOT NULL
);

CREATE TABLE scim_user_scim_roles (
  scim_user_internal_id BIGINT                 NOT NULL,
  roles_value           CHARACTER VARYING(255) NOT NULL
);

INSERT INTO database_scheme_version VALUES (0.02);

SELECT
  pg_catalog.setval('hibernate_sequence', 3, TRUE);

INSERT INTO scim_group VALUES (3, 'testGroup2', NULL, '2a820312-67b3-4275-963d-b235c6525207', NULL);

INSERT INTO scim_group_internalidskeleton VALUES (3, 1);

INSERT INTO scim_roles VALUES ('USER');
INSERT INTO scim_user VALUES (1, NULL, 'marissa', 'cef9452e-00a9-4cec-a086-d171374ffbef', NULL, NULL, NULL, 'cbae73fac0893291c4792ef19d158a589402288b35cb18fb8406e951b9d95f6b8b06a3526ffebe96ae0d91c04ae615a7fe2af362763db386ccbf3b55c29ae800', NULL, NULL, NULL, NULL, 'marissa', NULL, NULL);
INSERT INTO scim_user_scim_roles VALUES (1, 'USER');
-- Constraints
ALTER TABLE ONLY database_scheme_version
ADD CONSTRAINT database_scheme_version_pkey PRIMARY KEY (version);

ALTER TABLE ONLY scim_address
ADD CONSTRAINT scim_address_pkey PRIMARY KEY (id);

ALTER TABLE ONLY scim_certificate
ADD CONSTRAINT scim_certificate_pkey PRIMARY KEY (value);

ALTER TABLE ONLY scim_email
ADD CONSTRAINT scim_email_pkey PRIMARY KEY (value);

ALTER TABLE ONLY scim_enterprise
ADD CONSTRAINT scim_enterprise_pkey PRIMARY KEY (id);

ALTER TABLE ONLY scim_entitlements
ADD CONSTRAINT scim_entitlements_pkey PRIMARY KEY (value);

ALTER TABLE ONLY scim_group_internalidskeleton
ADD CONSTRAINT scim_group_internalidskeleton_pkey PRIMARY KEY (scim_group_internal_id, members_internal_id);

ALTER TABLE ONLY scim_group
ADD CONSTRAINT scim_group_pkey PRIMARY KEY (internal_id);

ALTER TABLE ONLY scim_im
ADD CONSTRAINT scim_im_pkey PRIMARY KEY (value);

ALTER TABLE ONLY scim_manager
ADD CONSTRAINT scim_manager_pkey PRIMARY KEY (id);

ALTER TABLE ONLY scim_meta
ADD CONSTRAINT scim_meta_pkey PRIMARY KEY (id);

ALTER TABLE ONLY scim_name
ADD CONSTRAINT scim_name_pkey PRIMARY KEY (id);

ALTER TABLE ONLY scim_phonenumber
ADD CONSTRAINT scim_phonenumber_pkey PRIMARY KEY (value);

ALTER TABLE ONLY scim_photo
ADD CONSTRAINT scim_photo_pkey PRIMARY KEY (value);

ALTER TABLE ONLY scim_roles
ADD CONSTRAINT scim_roles_pkey PRIMARY KEY (value);

ALTER TABLE ONLY scim_user
ADD CONSTRAINT scim_user_pkey PRIMARY KEY (internal_id);

ALTER TABLE ONLY scim_user_scim_address
ADD CONSTRAINT scim_user_scim_address_pkey PRIMARY KEY (scim_user_internal_id, addresses_id);

ALTER TABLE ONLY scim_user_scim_entitlements
ADD CONSTRAINT scim_user_scim_entitlements_pkey PRIMARY KEY (scim_user_internal_id, entitlements_value);

ALTER TABLE ONLY scim_user_scim_group
ADD CONSTRAINT scim_user_scim_group_pkey PRIMARY KEY (scim_user_internal_id, groups_internal_id);

ALTER TABLE ONLY scim_user_scim_roles
ADD CONSTRAINT scim_user_scim_roles_pkey PRIMARY KEY (scim_user_internal_id, roles_value);

ALTER TABLE ONLY scim_user_additional
ADD CONSTRAINT fk20575a504c8bba87 FOREIGN KEY (id) REFERENCES scim_user (internal_id);

ALTER TABLE ONLY scim_user_scim_entitlements
ADD CONSTRAINT fk2d322588abdb6640 FOREIGN KEY (scim_user_internal_id) REFERENCES scim_user (internal_id);

ALTER TABLE ONLY scim_user_scim_entitlements
ADD CONSTRAINT fk2d322588ef67251f FOREIGN KEY (entitlements_value) REFERENCES scim_entitlements (value);

ALTER TABLE ONLY scim_user_scim_address
ADD CONSTRAINT fk340ed212353ef531 FOREIGN KEY (addresses_id) REFERENCES scim_address (id);

ALTER TABLE ONLY scim_user_scim_address
ADD CONSTRAINT fk340ed212abdb6640 FOREIGN KEY (scim_user_internal_id) REFERENCES scim_user (internal_id);

ALTER TABLE ONLY scim_user
ADD CONSTRAINT fk38b265b627b5137b FOREIGN KEY (name_id) REFERENCES scim_name (id);

ALTER TABLE ONLY scim_user_scim_group
ADD CONSTRAINT fk704b1c1d17c2116 FOREIGN KEY (groups_internal_id) REFERENCES scim_group (internal_id);

ALTER TABLE ONLY scim_user_scim_group
ADD CONSTRAINT fk704b1c1dabdb6640 FOREIGN KEY (scim_user_internal_id) REFERENCES scim_user (internal_id);


ALTER TABLE ONLY scim_user_scim_roles
ADD CONSTRAINT fk70e4b45babdb6640 FOREIGN KEY (scim_user_internal_id) REFERENCES scim_user (internal_id);


ALTER TABLE ONLY scim_user_scim_roles
ADD CONSTRAINT fk70e4b45be638e451 FOREIGN KEY (roles_value) REFERENCES scim_roles (value);


ALTER TABLE ONLY scim_im
ADD CONSTRAINT fk725705cf738674d5 FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


ALTER TABLE ONLY scim_group_internalidskeleton
ADD CONSTRAINT fk739135706d23d136 FOREIGN KEY (scim_group_internal_id) REFERENCES scim_group (internal_id);


ALTER TABLE ONLY scim_certificate
ADD CONSTRAINT fk956dd94c738674d5 FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


ALTER TABLE ONLY scim_address
ADD CONSTRAINT fka4a85629738674d5 FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


ALTER TABLE ONLY scim_phonenumber
ADD CONSTRAINT fkd9f3520c738674d5 FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


ALTER TABLE ONLY scim_email
ADD CONSTRAINT fkdcb60f11738674d5 FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


ALTER TABLE ONLY scim_photo
ADD CONSTRAINT fkdd4f01a7738674d5 FOREIGN KEY (user_internal_id) REFERENCES scim_user (internal_id);


ALTER TABLE ONLY scim_enterprise
ADD CONSTRAINT fke1bc510cae52e63f FOREIGN KEY (manager_id) REFERENCES scim_manager (id);