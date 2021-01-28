package de.raidcraft.referrals.listener;

import de.raidcraft.referrals.Messages;
import de.raidcraft.referrals.RCReferrals;
import de.raidcraft.referrals.entities.ReferralPlayer;
import de.raidcraft.referrals.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static net.kyori.adventure.text.Component.text;

public class PlayerListener implements Listener {

    private final RCReferrals plugin;

    public PlayerListener(RCReferrals plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        ReferralPlayer referralPlayer = ReferralPlayer.of(player);

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
}
