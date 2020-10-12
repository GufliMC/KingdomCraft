-- apply changes
create table kingdoms (
  id                            integer not null,
  name                          varchar(255),
  display                       varchar(255),
  prefix                        varchar(255),
  suffix                        varchar(255),
  invite_only                   int default 0 not null,
  max_members                   integer not null,
  default_rank_id               integer,
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint uq_kingdoms_name unique (name),
  constraint uq_kingdoms_default_rank_id unique (default_rank_id),
  constraint pk_kingdoms primary key (id),
  foreign key (default_rank_id) references ranks (id) on delete set null on update set null
);

create table user_invites (
  id                            integer not null,
  user_id                       varchar(255),
  kingdom_id                    integer,
  sender_id                     varchar(255),
  created_at                    timestamp not null,
  constraint pk_user_invites primary key (id),
  foreign key (user_id) references users (id) on delete cascade on update cascade,
  foreign key (kingdom_id) references kingdoms (id) on delete cascade on update cascade,
  foreign key (sender_id) references users (id) on delete cascade on update cascade
);

create table ranks (
  id                            integer not null,
  name                          varchar(255),
  kingdom_id                    integer,
  display                       varchar(255),
  prefix                        varchar(255),
  suffix                        varchar(255),
  max_members                   integer not null,
  level                         integer not null,
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint uq_ranks_name_kingdom_id unique (name,kingdom_id),
  constraint pk_ranks primary key (id),
  foreign key (kingdom_id) references kingdoms (id) on delete cascade on update cascade
);

create table kingdom_relations (
  id                            integer not null,
  kingdom_id                    integer,
  other_kingdom_id              integer,
  relation                      integer not null,
  is_request                    int default 0 not null,
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint pk_kingdom_relations primary key (id),
  foreign key (kingdom_id) references kingdoms (id) on delete cascade on update cascade,
  foreign key (other_kingdom_id) references kingdoms (id) on delete cascade on update cascade
);

create table users (
  id                            varchar(255) not null,
  name                          varchar(255),
  rank_id                       integer,
  kingdom_id                    integer,
  created_at                    timestamp not null,
  updated_at                    timestamp not null,
  constraint uq_users_name unique (name),
  constraint pk_users primary key (id),
  foreign key (rank_id) references ranks (id) on delete set null on update set null,
  foreign key (kingdom_id) references kingdoms (id) on delete set null on update set null
);

