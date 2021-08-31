-- apply changes
create table user_attributes (
  id                            integer not null,
  user_id                       varchar(255),
  name                          varchar(255),
  value                         varchar(255),
  constraint uq_user_attributes_user_id_name unique (user_id,name),
  constraint pk_user_attributes primary key (id),
  foreign key (user_id) references users (id) on delete cascade on update restrict
);

