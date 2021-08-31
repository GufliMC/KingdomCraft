-- apply changes
create table user_attributes (
  id                            integer auto_increment not null,
  user_id                       varchar(255),
  name                          varchar(255),
  value                         varchar(255),
  constraint uq_user_attributes_user_id_name unique (user_id,name),
  constraint pk_user_attributes primary key (id)
);

create index ix_user_attributes_user_id on user_attributes (user_id);
alter table user_attributes add constraint fk_user_attributes_user_id foreign key (user_id) references users (id) on delete cascade on update restrict;

