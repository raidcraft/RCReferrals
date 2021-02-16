-- apply changes
create table rcreferrals_promo_codes (
  id                            varchar(40) not null,
  name                          varchar(255),
  description                   varchar(255),
  amount                        integer not null,
  start                         timestamp,
  end                           timestamp,
  enabled                       int default 0 not null,
  rewards                       clob default '[]',
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint uq_rcreferrals_promo_codes_name unique (name),
  constraint pk_rcreferrals_promo_codes primary key (id)
);

create table rcreferrals_redeemed_codes (
  id                            varchar(40) not null,
  player_id                     varchar(40),
  code_id                       varchar(40),
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rcreferrals_redeemed_codes primary key (id),
  foreign key (player_id) references rcreferrals_players (id) on delete restrict on update restrict,
  foreign key (code_id) references rcreferrals_promo_codes (id) on delete restrict on update restrict
);

alter table rcreferrals_referrals add column claimable int default 0 not null;

alter table rcreferrals_players add column play_time integer default 0 not null;

