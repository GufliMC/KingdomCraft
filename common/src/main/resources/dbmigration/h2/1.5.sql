-- apply changes
alter table user_attributes drop constraint if exists fk_user_attributes_user_id;
alter table user_attributes add constraint fk_user_attributes_user_id foreign key (user_id) references users (id) on delete cascade on update cascade;
alter table user_chatchannels drop constraint if exists fk_user_chatchannels_user_id;
alter table user_chatchannels add constraint fk_user_chatchannels_user_id foreign key (user_id) references users (id) on delete cascade on update cascade;
