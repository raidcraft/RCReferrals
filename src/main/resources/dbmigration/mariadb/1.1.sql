-- apply changes
create table rcreferrals_promo_codes (
  id                            varchar(40) not null,
  name                          varchar(255),
  description                   varchar(255),
  amount                        integer not null,
  start                         datetime(6),
  end                           datetime(6),
  enabled                       tinyint(1) default 0 not null,
  rewards                       json default '[]',
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint uq_rcreferrals_promo_codes_name unique (name),
  constraint pk_rcreferrals_promo_codes primary key (id)
);

create table rcreferrals_redeemed_codes (
  id                            varchar(40) not null,
  player_id                     varchar(40),
  code_id                       varchar(40),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_rcreferrals_redeemed_codes primary key (id)
);

alter table rcreferrals_referrals add column claimable tinyint(1) default 0 not null;

alter table rcreferrals_players add column play_time bigint default 0 not null;

create index ix_rcreferrals_redeemed_codes_player_id on rcreferrals_redeemed_codes (player_id);
alter table rcreferrals_redeemed_codes add constraint fk_rcreferrals_redeemed_codes_player_id foreign key (player_id) references rcreferrals_players (id) on delete restrict on update restrict;

create index ix_rcreferrals_redeemed_codes_code_id on rcreferrals_redeemed_codes (code_id);
alter table rcreferrals_redeemed_codes add constraint fk_rcreferrals_redeemed_codes_code_id foreign key (code_id) references rcreferrals_promo_codes (id) on delete restrict on update restrict;

