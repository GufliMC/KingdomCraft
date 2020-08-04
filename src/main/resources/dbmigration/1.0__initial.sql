-- apply changes
create table kingdom (
  id                            integer not null,
  name                          varchar(255),
  display                       varchar(255),
  prefix                        varchar(255),
  suffix                        varchar(255),
  spawn                         varchar(255),
  invite_only                   int default 0 not null,
  default_rank_id               integer,
  created_at                    timestamp not null,
  constraint uq_kingdom_default_rank_id unique (default_rank_id),
  constraint pk_kingdom primary key (id),
  foreign key (default_rank_id) references rank (id) on delete restrict on update restrict
);

create table kingdom_relation (
  kingdom1_id                   integer not null,
  kingdom2_id                   integer not null,
  relation                      integer,
  created_at                    timestamp not null,
  constraint ck_kingdom_relation_relation check ( relation in (0,1,2))
);

create table player (
  id                            varchar(255) not null,
  name                          varchar(255),
  kingdom_id                    integer,
  rank_id                       integer,
  created_at                    timestamp not null,
  constraint pk_player primary key (id),
  foreign key (kingdom_id) references kingdom (id) on delete restrict on update restrict,
  foreign key (rank_id) references rank (id) on delete restrict on update restrict
);

create table rank (
  id                            integer not null,
  kingdom_id                    integer,
  name                          varchar(255),
  display                       varchar(255),
  prefix                        varchar(255),
  suffix                        varchar(255),
  created_at                    timestamp not null,
  constraint pk_rank primary key (id),
  foreign key (kingdom_id) references kingdom (id) on delete restrict on update restrict
);

