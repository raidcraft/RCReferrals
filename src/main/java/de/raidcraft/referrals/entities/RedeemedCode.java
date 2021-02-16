package de.raidcraft.referrals.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.ebean.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@Accessors(fluent = true)
@Table(name = "rcreferrals_redeemed_codes")
public class RedeemedCode extends BaseEntity {

    @ManyToOne
    private ReferralPlayer player;
    @ManyToOne
    private PromoCode code;
}
