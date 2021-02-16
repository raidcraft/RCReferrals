-- apply changes
create table rcreferrals_promo_codes (
  id                            varchar(40) not null,
  name                          varchar(255),
  description                   varchar(255),
  count                         integer not null,
  start                         timestamp,
  end                           timestamp,
  commands                      clob default '[]',
  version                       integer not null,
  when_created                  timestamp not null,
  when_modified                 timestamp not null,
  constraint uq_rcreferrals_promo_codes_name unique (name),
  constraint pk_rcreferrals_promo_codes primary key (id)
);

create table rcreferrals_promo_codes_rcreferrals_redeemed_codes (
  rcreferrals_promo_codes_id    varchar(40) not null,
  rcreferrals_redeemed_codes_id varchar(40) not null,
  constraint pk_rcreferrals_promo_codes_rcreferrals_redeemed_codes primary key (rcreferrals_promo_codes_id,rcreferrals_redeemed_codes_id),
  foreign key (rcreferrals_promo_codes_id) references rcreferrals_promo_codes (id) on delete restrict on update restrict,
  foreign key (rcreferrals_redeemed_codes_id) references rcreferrals_redeemed_codes (id) on delete restrict on update restrict
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

