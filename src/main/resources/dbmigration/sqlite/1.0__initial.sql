-- apply changes
create table rcreferrals_referrals (
  id                            varchar(40) not null,
  referred_by_id                varchar(40),
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rcreferrals_referrals primary key (id),
  foreign key (referred_by_id) references rcreferrals_players (id) on delete restrict on update restrict
);

create table rcreferrals_players (
  id                            varchar(40) not null,
  name                          varchar(255),
  referral_id                   varchar(40),
  last_ip_address               varchar(255),
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint uq_rcreferrals_players_referral_id unique (referral_id),
  constraint pk_rcreferrals_players primary key (id),
  foreign key (referral_id) references rcreferrals_referrals (id) on delete restrict on update restrict
);

