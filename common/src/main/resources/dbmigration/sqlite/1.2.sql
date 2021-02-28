-- apply changes
alter table users add column admin_mode_enabled int default 0 not null;
alter table users add column social_spy_enabled int default 0 not null;

