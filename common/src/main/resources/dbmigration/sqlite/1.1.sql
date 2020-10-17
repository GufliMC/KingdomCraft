-- apply changes
create table kingdom_attributes (
  id                            integer not null,
  kingdom_id                    integer,
  name                          varchar(255),
  value                         varchar(255),
  constraint uq_kingdom_attributes_kingdom_id_name unique (kingdom_id,name),
  constraint pk_kingdom_attributes primary key (id),
  foreign key (kingdom_id) references kingdoms (id) on delete cascade on update cascade
);

create table rank_attributes (
  id                            integer not null,
  rank_id                       integer,
  name                          varchar(255),
  value                         varchar(255),
  constraint uq_rank_attributes_rank_id_name unique (rank_id,name),
  constraint pk_rank_attributes primary key (id),
  foreign key (rank_id) references ranks (id) on delete cascade on update cascade
);

