package de.raidcraft.referrals.listener;

import com.djrapitops.plan.query.QueryService;
import de.raidcraft.referrals.Messages;
import de.raidcraft.referrals.RCReferrals;
import de.raidcraft.referrals.entities.Referral;
import de.raidcraft.referrals.entities.ReferralPlayer;
import de.raidcraft.referrals.util.TimeUtil;
import io.ebean.Transaction;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Log(topic = "RCReferrals")
public class PlayerListener implements Listener {

    private final RCReferrals plugin;
    private final Map<UUID, Instant> onlineSince = new HashMap<>();
    private QueryService queryService;
    private boolean planEnabled = false;

    public PlayerListener(RCReferrals plugin) {
        this.plugin = plugin;
        try {
            queryService = QueryService.getInstance();
            planEnabled = true;
        } catch (IllegalStateException planIsNotEnabled) {
            log.warning("Plan is not enabled. Falling back to simple online time tracking...");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        ReferralPlayer referralPlayer = ReferralPlayer.of(player);

        onlineSince.put(player.getUniqueId(), Instant.now());

        if (referralPlayer.referral() != null) {
            return;
        }

        Instant timeout = referralPlayer.firstJoin().plus(TimeUtil.parseTimeAsSeconds(plugin.getPluginConfig().getReferralTimeout()), ChronoUnit.SECONDS);
        if (Instant.now().isAfter(timeout)) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            referralPlayer.refresh();
            if (player.isOnline() && referralPlayer.referral() == null) {
                Messages.send(player, Messages.referralNotice(timeout));
            }
        }, plugin.getPluginConfig().getLoginMessageDelay());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {

        Instant instant = onlineSince.remove(event.getPlayer().getUniqueId());
        if (instant != null) {
            try (Transaction transaction = plugin.getDatabase().beginTransaction()) {

                ReferralPlayer referralPlayer = ReferralPlayer.of(event.getPlayer());
                referralPlayer.playTime(referralPlayer.playTime() + Instant.now().toEpochMilli() - instant.toEpochMilli()).save();

                Referral referral = referralPlayer.referral();
                if (referral != null && referral.isPlayerReferral() && !referral.claimable() && referral.rewardPending())
                if (planEnabled) {
                    // TODO: wait for new API release
                } else {
                    if (referralPlayer.playTime() > TimeUtil.parseTimeAsMilliseconds(plugin.getPluginConfig().getRequiredPlayTime())) {
                        referral.claimable(true).save();
                    }
                }

                transaction.commit();
            }
        }
    }
}
