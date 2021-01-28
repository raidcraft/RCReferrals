-- apply changes
create table rcreferrals_referrals (
  id                            varchar(40) not null,
  referred_by_id                varchar(40),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_rcreferrals_referrals primary key (id)
);

create table rcreferrals_players (
  id                            varchar(40) not null,
  name                          varchar(255),
  referral_id                   varchar(40),
  last_ip_address               varchar(255),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint uq_rcreferrals_players_referral_id unique (referral_id),
  constraint pk_rcreferrals_players primary key (id)
);

create index ix_rcreferrals_referrals_referred_by_id on rcreferrals_referrals (referred_by_id);
alter table rcreferrals_referrals add constraint fk_rcreferrals_referrals_referred_by_id foreign key (referred_by_id) references rcreferrals_players (id) on delete restrict on update restrict;

alter table rcreferrals_players add constraint fk_rcreferrals_players_referral_id foreign key (referral_id) references rcreferrals_referrals (id) on delete restrict on update restrict;

