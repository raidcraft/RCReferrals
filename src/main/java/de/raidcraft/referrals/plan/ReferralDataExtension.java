package de.raidcraft.referrals.plan;

import com.djrapitops.plan.extension.CallEvents;
import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.annotation.BooleanProvider;
import com.djrapitops.plan.extension.annotation.Conditional;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.annotation.StringProvider;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import de.raidcraft.referrals.entities.ReferralPlayer;

import java.util.Optional;
import java.util.UUID;

@PluginInfo(
        name = "Referrals",
        iconName = "handshake",
        iconFamily = Family.SOLID,
        color = Color.NONE
)
public class ReferralDataExtension implements DataExtension {

    @Override
    public CallEvents[] callExtensionMethodsOn() {
        return new CallEvents[]{
                CallEvents.PLAYER_JOIN,
                CallEvents.PLAYER_LEAVE
        };
    }

    @StringProvider(
            text = "Referred by",
            description = "The name of the player that referred this player.",
            priority = 5,
            iconName = "user-friends",
            iconFamily = Family.SOLID,
            iconColor = Color.NONE,
            playerName = true,
            showInPlayerTable = true
    )
    @Conditional("isReferredByPlayer")
    public String referredByPlayer(UUID playerUUID) {

        Optional<ReferralPlayer> player = ReferralPlayer.byId(playerUUID);
        if (player.isEmpty()) return null;
        ReferralPlayer referralPlayer = player.get();
        if (referralPlayer.referral() == null) return null;
        if (referralPlayer.referral().referredBy() == null) return null;

        return referralPlayer.referral().referredBy().name();
    }

    @StringProvider(
            text = "Referral Type",
            description = "The reason why the player joined the server.",
            priority = 4,
            iconName = "handshake",
            iconFamily = Family.SOLID,
            iconColor = Color.NONE,
            showInPlayerTable = true
    )
    @Conditional("isReferred")
    public String referredReason(UUID playerUUID) {

        Optional<ReferralPlayer> player = ReferralPlayer.byId(playerUUID);
        if (player.isEmpty()) return null;
        ReferralPlayer referralPlayer = player.get();
        if (referralPlayer.referral() == null) return null;
        return referralPlayer.referral().type().name();
    }

    @BooleanProvider(
            text = "Referred by Player?",
            conditionName = "isReferredByPlayer"
    )
    public boolean isReferredByPlayer(UUID playerUUID) {

        Optional<ReferralPlayer> player = ReferralPlayer.byId(playerUUID);
        if (player.isEmpty()) return false;
        return player.get().referral() != null && player.get().referral().isPlayerReferral();
    }

    @BooleanProvider(
            text = "Is referred?",
            conditionName = "isReferred"
    )
    public boolean isReferred(UUID playerUUID) {

        Optional<ReferralPlayer> player = ReferralPlayer.byId(playerUUID);
        if (player.isEmpty()) return false;
        return player.get().referral() != null;
    }
}
