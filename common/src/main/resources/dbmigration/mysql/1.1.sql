-- apply changes
create table kingdom_attributes (
  id                            bigint auto_increment not null,
  kingdom_id                    bigint,
  name                          varchar(255),
  value                         varchar(255),
  constraint uq_kingdom_attributes_kingdom_id_name unique (kingdom_id,name),
  constraint pk_kingdom_attributes primary key (id)
);

create table rank_attributes (
  id                            bigint auto_increment not null,
  rank_id                       bigint,
  name                          varchar(255),
  value                         varchar(255),
  constraint uq_rank_attributes_rank_id_name unique (rank_id,name),
  constraint pk_rank_attributes primary key (id)
);

create index ix_kingdom_attributes_kingdom_id on kingdom_attributes (kingdom_id);
alter table kingdom_attributes add constraint fk_kingdom_attributes_kingdom_id foreign key (kingdom_id) references kingdoms (id) on delete cascade on update cascade;

create index ix_rank_attributes_rank_id on rank_attributes (rank_id);
alter table rank_attributes add constraint fk_rank_attributes_rank_id foreign key (rank_id) references ranks (id) on delete cascade on update cascade;

