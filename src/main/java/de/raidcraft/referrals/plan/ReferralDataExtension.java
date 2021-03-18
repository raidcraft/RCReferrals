package de.raidcraft.referrals.plan;

import com.djrapitops.plan.extension.CallEvents;
import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.annotation.*;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.djrapitops.plan.extension.icon.Icon;
import com.djrapitops.plan.extension.table.Table;
import de.raidcraft.referrals.entities.ReferralPlayer;
import de.raidcraft.referrals.entities.ReferralType;

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
                CallEvents.PLAYER_LEAVE,
                CallEvents.SERVER_EXTENSION_REGISTER
        };
    }

    @TableProvider()
    public Table referralStats() {

        Table.Factory factory = Table.builder()
                .columnOne("Referral Type", Icon.called("handshake").of(Color.BLUE_GREY).of(Family.SOLID).build())
                .columnTwo("Count", Icon.called("chart-line").of(Color.BLUE_GREY).of(Family.SOLID).build());

        ReferralType.all().stream()
                .sorted((o1, o2) -> Integer.compare(o2.referrals().size(), o1.referrals().size()))
                .forEach(referralType -> factory.addRow(referralType.name(), referralType.referrals().size()));

        return factory.build();
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

    @NumberProvider(
            text = "Referral Count",
            description = "The number of players this player referred.",
            priority = 4,
            iconName = "user-friends",
            iconFamily = Family.SOLID,
            iconColor = Color.NONE,
            showInPlayerTable = true
    )
    public int referralCount(UUID playerUUID) {

        Optional<ReferralPlayer> player = ReferralPlayer.byId(playerUUID);
        if (player.isEmpty()) return 0;
        ReferralPlayer referralPlayer = player.get();

        return referralPlayer.referrals().size();
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
            text = "Referred by Player",
            conditionName = "isReferredByPlayer"
    )
    public boolean isReferredByPlayer(UUID playerUUID) {

        Optional<ReferralPlayer> player = ReferralPlayer.byId(playerUUID);
        if (player.isEmpty()) return false;
        return player.get().referral() != null && player.get().referral().isPlayerReferral();
    }

    @BooleanProvider(
            text = "Is referred",
            conditionName = "isReferred"
    )
    public boolean isReferred(UUID playerUUID) {

        Optional<ReferralPlayer> player = ReferralPlayer.byId(playerUUID);
        if (player.isEmpty()) return false;
        return player.get().referral() != null;
    }
}
