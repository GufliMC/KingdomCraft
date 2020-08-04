-- apply changes
alter table kingdom_relation drop constraint if exists ck_kingdom_relation_relation;
alter table kingdom_relation add constraint ck_kingdom_relation_relation check ( relation in (0,1,2,3));
alter table kingdom_relation add column kingdom_id integer;
alter table kingdom_relation add column target_id integer;

