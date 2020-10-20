-- apply changes
create table rank_permission_groups (
  id                            integer not null,
  rank_id                       integer,
  name                          varchar(255),
  constraint uq_rank_permission_groups_rank_id_name unique (rank_id,name),
  constraint pk_rank_permission_groups primary key (id),
  foreign key (rank_id) references ranks (id) on delete cascade on update cascade
);

