-- apply changes
alter table users add column admin_mode_enabled tinyint(1) default 0 not null;
alter table users add column social_spy_enabled tinyint(1) default 0 not null;

