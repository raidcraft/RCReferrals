package de.raidcraft.template.entities;

import de.raidcraft.template.ReferralException;
import de.raidcraft.template.events.PlayerReferredByPlayerEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.ebean.BaseEntity;
import org.bukkit.Bukkit;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Accessors(fluent = true)
@Table(name = "rcreferrals_referrals")
public class Referral extends BaseEntity {

    /**
     * Creates a new referral firing an referral event and giving rewards.
     *
     * @param referral the user that was referred and is new to the server
     * @param referredBy the user that referred the new user
     * @return the created referral
     */
    public static Referral create(@NonNull ReferralPlayer referral, @NonNull ReferralPlayer referredBy) throws ReferralException {

        if (referral.referral() != null) {
            throw new ReferralException("Der Spieler wurde bereits empfohlen.");
        }

        PlayerReferredByPlayerEvent event = new PlayerReferredByPlayerEvent(referral, referredBy);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            throw new ReferralException("Die Empfehlung wurde durch ein Plugin verhindert.");
        }

        Referral ref = new Referral(referral, referredBy);
        ref.save();

        return ref;
    }

    @OneToOne(mappedBy = "referral")
    private ReferralPlayer player;
    @ManyToOne
    private ReferralPlayer referredBy;

    public Referral(ReferralPlayer player, ReferralPlayer referredBy) {
        this.player = player;
        this.referredBy = referredBy;
    }
}
