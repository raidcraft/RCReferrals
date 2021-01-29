-- apply changes
create table rcreferrals_referrals (
  id                            varchar(40) not null,
  player_id                     varchar(40),
  referred_by_id                varchar(40),
  reason                        varchar(255),
  type_id                       varchar(40),
  reward_pending                int default 0 not null,
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint uq_rcreferrals_referrals_player_id unique (player_id),
  constraint pk_rcreferrals_referrals primary key (id),
  foreign key (player_id) references rcreferrals_players (id) on delete restrict on update restrict,
  foreign key (referred_by_id) references rcreferrals_players (id) on delete restrict on update restrict,
  foreign key (type_id) references rcreferrals_types (id) on delete restrict on update restrict
);

create table rcreferrals_players (
  id                            varchar(40) not null,
  name                          varchar(255),
  last_ip_address               varchar(255),
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  first_join                    timestamp not null,
  constraint pk_rcreferrals_players primary key (id)
);

create table rcreferrals_types (
  id                            varchar(40) not null,
  identifier                    varchar(255),
  name                          varchar(255),
  description                   varchar(255),
  text                          varchar(255),
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  deleted                       int default 0 not null,
  constraint pk_rcreferrals_types primary key (id)
);

