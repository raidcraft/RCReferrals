-- apply changes
create table rcreferrals_promo_codes (
  id                            uuid not null,
  name                          varchar(255),
  description                   varchar(255),
  count                         integer not null,
  start                         timestamptz,
  end                           timestamptz,
  commands                      json default '[]',
  version                       bigint not null,
  when_created                  timestamptz not null,
  when_modified                 timestamptz not null,
  constraint uq_rcreferrals_promo_codes_name unique (name),
  constraint pk_rcreferrals_promo_codes primary key (id)
);

create table rcreferrals_promo_codes_rcreferrals_redeemed_codes (
  rcreferrals_promo_codes_id    uuid not null,
  rcreferrals_redeemed_codes_id uuid not null,
  constraint pk_rcreferrals_promo_codes_rcreferrals_redeemed_codes primary key (rcreferrals_promo_codes_id,rcreferrals_redeemed_codes_id)
);

create table rcreferrals_redeemed_codes (
  id                            uuid not null,
  player_id                     uuid,
  code_id                       uuid,
  version                       bigint not null,
  when_created                  timestamptz not null,
  when_modified                 timestamptz not null,
  constraint pk_rcreferrals_redeemed_codes primary key (id)
);

create index ix_rcreferrals_promo_codes_rcreferrals_redeemed_codes_rcr_1 on rcreferrals_promo_codes_rcreferrals_redeemed_codes (rcreferrals_promo_codes_id);
alter table rcreferrals_promo_codes_rcreferrals_redeemed_codes add constraint fk_rcreferrals_promo_codes_rcreferrals_redeemed_codes_rcr_1 foreign key (rcreferrals_promo_codes_id) references rcreferrals_promo_codes (id) on delete restrict on update restrict;

create index ix_rcreferrals_promo_codes_rcreferrals_redeemed_codes_rcr_2 on rcreferrals_promo_codes_rcreferrals_redeemed_codes (rcreferrals_redeemed_codes_id);
alter table rcreferrals_promo_codes_rcreferrals_redeemed_codes add constraint fk_rcreferrals_promo_codes_rcreferrals_redeemed_codes_rcr_2 foreign key (rcreferrals_redeemed_codes_id) references rcreferrals_redeemed_codes (id) on delete restrict on update restrict;

create index ix_rcreferrals_redeemed_codes_player_id on rcreferrals_redeemed_codes (player_id);
alter table rcreferrals_redeemed_codes add constraint fk_rcreferrals_redeemed_codes_player_id foreign key (player_id) references rcreferrals_players (id) on delete restrict on update restrict;

create index ix_rcreferrals_redeemed_codes_code_id on rcreferrals_redeemed_codes (code_id);
alter table rcreferrals_redeemed_codes add constraint fk_rcreferrals_redeemed_codes_code_id foreign key (code_id) references rcreferrals_promo_codes (id) on delete restrict on update restrict;

