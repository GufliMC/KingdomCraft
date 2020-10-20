-- apply changes
create table user_chatchannels (
  id                            integer not null,
  user_id                       varchar(255),
  channel                       varchar(255),
  enabled                       int default 0 not null,
  constraint uq_user_chatchannels_user_id_channel unique (user_id,channel),
  constraint pk_user_chatchannels primary key (id),
  foreign key (user_id) references users (id) on delete cascade on update cascade
);

