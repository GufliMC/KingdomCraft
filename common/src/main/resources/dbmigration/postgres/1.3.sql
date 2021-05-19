-- apply changes
alter table users add column joined_kingdom_at timestamptz;
alter table users add column last_online_at timestamptz;

