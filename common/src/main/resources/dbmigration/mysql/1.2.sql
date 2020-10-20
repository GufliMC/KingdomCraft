-- apply changes
create table user_chatchannels (
  id                            bigint auto_increment not null,
  user_id                       varchar(255),
  channel                       varchar(255),
  enabled                       tinyint(1) default 0 not null,
  constraint uq_user_chatchannels_user_id_channel unique (user_id,channel),
  constraint pk_user_chatchannels primary key (id)
);

create index ix_user_chatchannels_user_id on user_chatchannels (user_id);
alter table user_chatchannels add constraint fk_user_chatchannels_user_id foreign key (user_id) references users (id) on delete cascade on update cascade;

