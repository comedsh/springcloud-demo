drop table if exists users;
create table users (
  username varchar(25) primary key,
  password varchar(100),
  enabled boolean
);

drop table if exists authorities;
create table authorities (
  username varchar(25),
  authority varchar(100)
);

drop table if exists oauth_client_details;
create table oauth_client_details (
  client_id VARCHAR(150) PRIMARY KEY,
  resource_ids VARCHAR(50),
  client_secret VARCHAR(150),
  scope VARCHAR(100),
  authorized_grant_types VARCHAR(50),
  web_server_redirect_uri VARCHAR(100),
  authorities VARCHAR(100),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additional_information VARCHAR(100),
  autoapprove VARCHAR(100)
);

drop table if exists oauth_client_token;
create table oauth_client_token (
  token_id VARCHAR(256),
  token BLOB,
  authentication_id VARCHAR(256),
  user_name VARCHAR(25),
  client_id VARCHAR(150)
);

drop table if exists oauth_access_token;
create table oauth_access_token (
  token_id VARCHAR(256),
  token BLOB,
  authentication_id VARCHAR(256),
  user_name VARCHAR(25),
  client_id VARCHAR(150),
  authentication BLOB,
  refresh_token VARCHAR(256)
);

drop table if exists oauth_refresh_token;
create table oauth_refresh_token (
  token_id VARCHAR(256),
  token BLOB,
  authentication BLOB
);

drop table if exists oauth_code;
create table oauth_code (
  code VARCHAR(256), 
  authentication VARCHAR(256)
);

