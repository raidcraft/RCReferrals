package de.raidcraft.referrals.listener;

import de.raidcraft.referrals.Messages;
import de.raidcraft.referrals.RCReferrals;
import de.raidcraft.referrals.art.ReferralTrigger;
import de.raidcraft.referrals.entities.Referral;
import de.raidcraft.referrals.entities.ReferralPlayer;
import de.raidcraft.referrals.events.PlayerReferredEvent;
import io.artframework.ArtContext;
import io.artframework.ParseException;
import io.artframework.Scope;
import io.artframework.TriggerExecution;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.stream.Collectors;

@Log(topic = "RCAchievements")
public class RewardListener implements Listener {

    private final RCReferrals plugin;
    private final Scope scope;
    // the rewards a new player gets when he answers how he came to the server
    private ArtContext newPlayerRewards;
    // the rewards players get that referred other players
    @Getter
    private ArtContext playerRewards;

    public RewardListener(RCReferrals plugin, Scope scope) {

        this.plugin = plugin;
        this.scope = scope;
    }

    public void load() {

        try {
            newPlayerRewards = scope.load(plugin.getPluginConfig().getNewPlayerRewards());
        } catch (ParseException e) {
            log.severe("failed to load new_player_rewards: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            playerRewards = scope.load(plugin.getPluginConfig().getPlayerRewards());
        } catch (ParseException e) {
            log.severe("failed to load player_rewards: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        List<Referral> pendingRewards = ReferralPlayer.of(event.getPlayer())
                .referrals().stream()
                .filter(Referral::rewardPending)
                .collect(Collectors.toList());

        if (pendingRewards.size() > 0) {
            Bukkit.getScheduler().runTaskLater(plugin,
                    () -> Messages.send(event.getPlayer(), Messages.rewardsPending()),
                    plugin.getPluginConfig().getLoginMessageDelay()
            );
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerReferred(PlayerReferredEvent event) {

        Player player = Bukkit.getPlayer(event.getReferral().player().id());
        if (player == null) return;

        if (newPlayerRewards != null) {
            newPlayerRewards.execute(player);
            Messages.send(player, Messages.newPlayerRewarded());
        }

        if (playerRewards != null && event.getReferral().isPlayerReferral()) {
            Messages.send(event.getReferral().referredBy().id(), Messages.rewardsPending());
        }

        TriggerExecution<ReferralTrigger> trigger = scope.trigger(ReferralTrigger.class)
                .with(event.getReferral())
                .with(player);
        if (event.getReferral().isPlayerReferral()) {
            trigger.with(event.getReferral().referredBy().offlinePlayer());
        }
        trigger.execute();
    }
}
