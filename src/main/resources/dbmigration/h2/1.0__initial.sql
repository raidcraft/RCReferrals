-- apply changes
create table rcreferrals_referrals (
  id                            uuid not null,
  referred_by_id                uuid,
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint pk_rcreferrals_referrals primary key (id)
);

create table rcreferrals_players (
  id                            uuid not null,
  name                          varchar(255),
  referral_id                   uuid,
  last_ip_address               varchar(255),
  version                       bigint not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint uq_rcreferrals_players_referral_id unique (referral_id),
  constraint pk_rcreferrals_players primary key (id)
);

create index ix_rcreferrals_referrals_referred_by_id on rcreferrals_referrals (referred_by_id);
alter table rcreferrals_referrals add constraint fk_rcreferrals_referrals_referred_by_id foreign key (referred_by_id) references rcreferrals_players (id) on delete restrict on update restrict;

alter table rcreferrals_players add constraint fk_rcreferrals_players_referral_id foreign key (referral_id) references rcreferrals_referrals (id) on delete restrict on update restrict;

