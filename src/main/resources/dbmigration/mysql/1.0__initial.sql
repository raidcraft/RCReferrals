-- apply changes
create table rcreferrals_referrals (
  id                            varchar(40) not null,
  player_id                     varchar(40),
  referred_by_id                varchar(40),
  reason                        varchar(255),
  type_id                       varchar(40),
  reward_pending                tinyint(1) default 0 not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint uq_rcreferrals_referrals_player_id unique (player_id),
  constraint pk_rcreferrals_referrals primary key (id)
);

create table rcreferrals_players (
  id                            varchar(40) not null,
  name                          varchar(255),
  last_ip_address               varchar(255),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  first_join                    datetime(6) not null,
  constraint pk_rcreferrals_players primary key (id)
);

create table rcreferrals_types (
  id                            varchar(40) not null,
  identifier                    varchar(255),
  name                          varchar(255),
  description                   varchar(255),
  text                          varchar(255),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  deleted                       tinyint(1) default 0 not null,
  constraint pk_rcreferrals_types primary key (id)
);

alter table rcreferrals_referrals add constraint fk_rcreferrals_referrals_player_id foreign key (player_id) references rcreferrals_players (id) on delete restrict on update restrict;

create index ix_rcreferrals_referrals_referred_by_id on rcreferrals_referrals (referred_by_id);
alter table rcreferrals_referrals add constraint fk_rcreferrals_referrals_referred_by_id foreign key (referred_by_id) references rcreferrals_players (id) on delete restrict on update restrict;

create index ix_rcreferrals_referrals_type_id on rcreferrals_referrals (type_id);
alter table rcreferrals_referrals add constraint fk_rcreferrals_referrals_type_id foreign key (type_id) references rcreferrals_types (id) on delete restrict on update restrict;

