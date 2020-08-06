-- apply changes
create table kingdom (
  id                            integer not null,
  name                          varchar(255) not null,
  display                       varchar(255),
  prefix                        varchar(255),
  suffix                        varchar(255),
  spawn                         varchar(255),
  invite_only                   integer default 0 not null,
  default_rank_id               integer,
  created_at                    timestamp not null,
  constraint uq_kingdom_name unique (name),
  constraint uq_kingdom_default_rank_id unique (default_rank_id),
  constraint pk_kingdom primary key (id),
  foreign key (default_rank_id) references rank (id) on delete restrict on update restrict
);

create table kingdom_invite (
  kingdom_id                    integer,
  sender_id                     varchar(255),
  target_id                     varchar(255),
  created_at                    timestamp not null,
  constraint uq_kingdom_invite_kingdom_id unique (kingdom_id),
  constraint uq_kingdom_invite_sender_id unique (sender_id),
  constraint uq_kingdom_invite_target_id unique (target_id),
  foreign key (kingdom_id) references kingdom (id) on delete restrict on update restrict,
  foreign key (sender_id) references player (id) on delete restrict on update restrict,
  foreign key (target_id) references player (id) on delete restrict on update restrict
);

create table kingdom_relation (
  kingdom_id                    integer,
  target_id                     integer,
  relation                      integer not null,
  created_at                    timestamp not null,
  constraint ck_kingdom_relation_relation check ( relation in (0,1,2)),
  constraint uq_kingdom_relation_kingdom_id unique (kingdom_id),
  constraint uq_kingdom_relation_target_id unique (target_id),
  constraint uq_kingdom_relation_kingdom_id_target_id unique (kingdom_id,target_id),
  foreign key (kingdom_id) references kingdom (id) on delete restrict on update restrict,
  foreign key (target_id) references kingdom (id) on delete restrict on update restrict
);

create table player (
  id                            varchar(255) not null,
  name                          varchar(255) not null,
  kingdom_id                    integer,
  rank_id                       integer,
  created_at                    timestamp not null,
  constraint uq_player_name unique (name),
  constraint pk_player primary key (id),
  foreign key (kingdom_id) references kingdom (id) on delete restrict on update restrict,
  foreign key (rank_id) references rank (id) on delete restrict on update restrict
);

create table rank (
  id                            integer not null,
  kingdom_id                    integer,
  name                          varchar(255) not null,
  display                       varchar(255),
  prefix                        varchar(255),
  suffix                        varchar(255),
  created_at                    timestamp not null,
  constraint uq_rank_kingdom_id_name unique (kingdom_id,name),
  constraint pk_rank primary key (id),
  foreign key (kingdom_id) references kingdom (id) on delete restrict on update restrict
);

