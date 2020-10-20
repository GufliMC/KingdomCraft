-- apply changes
create table rank_permission_groups (
  id                            bigint auto_increment not null,
  rank_id                       bigint,
  name                          varchar(255),
  constraint uq_rank_permission_groups_rank_id_name unique (rank_id,name),
  constraint pk_rank_permission_groups primary key (id)
);

create index ix_rank_permission_groups_rank_id on rank_permission_groups (rank_id);
alter table rank_permission_groups add constraint fk_rank_permission_groups_rank_id foreign key (rank_id) references ranks (id) on delete cascade on update cascade;

