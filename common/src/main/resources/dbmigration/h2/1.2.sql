-- apply changes
alter table users add column admin_mode_enabled boolean default false not null;
alter table users add column social_spy_enabled boolean default false not null;

