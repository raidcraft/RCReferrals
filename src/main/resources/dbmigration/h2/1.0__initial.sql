-- apply changes
create table rcreferrals_referrals (
  id                            uuid not null,
  player_id                     uuid,
  referred_by_id                uuid,
  reason                        varchar(255),
  type_id                       uuid,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint uq_rcreferrals_referrals_player_id unique (player_id),
  constraint pk_rcreferrals_referrals primary key (id)
);

create table rcreferrals_players (
  id                            uuid not null,
  name                          varchar(255),
  last_ip_address               varchar(255),
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  first_join                    timestamp not null,
  constraint pk_rcreferrals_players primary key (id)
);

create table rcreferrals_types (
  id                            uuid not null,
  identifier                    varchar(255),
  name                          varchar(255),
  description                   varchar(255),
  text                          varchar(255),
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  deleted                       boolean default false not null,
  constraint pk_rcreferrals_types primary key (id)
);

alter table rcreferrals_referrals add constraint fk_rcreferrals_referrals_player_id foreign key (player_id) references rcreferrals_players (id) on delete restrict on update restrict;

create index ix_rcreferrals_referrals_referred_by_id on rcreferrals_referrals (referred_by_id);
alter table rcreferrals_referrals add constraint fk_rcreferrals_referrals_referred_by_id foreign key (referred_by_id) references rcreferrals_players (id) on delete restrict on update restrict;

create index ix_rcreferrals_referrals_type_id on rcreferrals_referrals (type_id);
alter table rcreferrals_referrals add constraint fk_rcreferrals_referrals_type_id foreign key (type_id) references rcreferrals_types (id) on delete restrict on update restrict;

