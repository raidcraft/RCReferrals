package de.raidcraft.referrals.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.annotation.*;
import de.raidcraft.referrals.Messages;
import de.raidcraft.referrals.RCReferrals;
import de.raidcraft.referrals.ReferralException;
import de.raidcraft.referrals.entities.PromoCode;
import de.raidcraft.referrals.entities.Referral;
import de.raidcraft.referrals.entities.ReferralPlayer;
import de.raidcraft.referrals.entities.ReferralType;
import de.raidcraft.referrals.util.TimeUtil;
import io.artframework.ART;
import io.artframework.ArtContext;
import io.artframework.ParseException;
import io.ebean.Transaction;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.units.qual.C;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("ref|empfehlungen|empfehlung|referral|referrals|rcreferrals")
public class PlayerCommands extends BaseCommand {

    public static final String REF_COMMAND = "/ref";
    public static final String REF_BY_PLAYER = "/ref by ";
    public static final String REF_BY_TYPE_COMMAND = "/ref type ";
    public static final String REF_CLAIM = "/ref claim";

    private final RCReferrals plugin;

    public PlayerCommands(RCReferrals plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("Sage uns wie du uns gefunden hast und erhalte eine Belohnung!")
    public void referal() {

        if (ReferralPlayer.of(getCurrentCommandIssuer().getIssuer()).referral() != null) {
            getCurrentCommandIssuer().sendMessage(ChatColor.RED + "Vielen Dank für deinen Übermut, aber du hast uns ja bereits gesagt wie du auf uns gestoßen bist.");
            return;
        }

        Messages.send(getCurrentCommandIssuer(), Messages.referralChoice());
    }

    @Subcommand("claim")
    @CommandAlias("refclaim")
    @Description("Hole deine Belohnung für die Empfehlung von anderen Spielern ab.")
    public void claim() {

        Player player = getCurrentCommandIssuer().getIssuer();
        ReferralPlayer referralPlayer = ReferralPlayer.of(player);

        List<Referral> pendingRewards = referralPlayer.referrals()
                .stream()
                .filter(Referral::rewardPending)
                .filter(Referral::claimable)
                .collect(Collectors.toList());

        if (pendingRewards.isEmpty()) {
            throw new ConditionFailedException("Du hast aktuell keine ausstehenden Belohnungen. Werbe andere Spieler und hole dann deine Belohnung ab.");
        }

        if (plugin.getRewardListener() != null) {
            ArtContext playerRewards = plugin.getRewardListener().getPlayerRewards();
            try (Transaction transaction = plugin.getDatabase().beginTransaction()) {
                for (Referral reward : pendingRewards) {
                    playerRewards.execute(player);
                    reward.rewardPending(false).save();
                }
                transaction.commit();
            }
            Messages.send(player, Messages.rewardsClaimed());
        } else {
            getCurrentCommandIssuer().sendMessage(ChatColor.RED + "Tut uns leid bei der Verteilung der Belohnung ist etwas schief gelaufen. Bitte probiere es später nochmal.");
        }
    }

    @CommandAlias("refby|empfohlen|empfohlenvon|referredby|referred")
    @Subcommand("by|von")
    @CommandCompletion("@rplayers")
    public void refered(ReferralPlayer referredBy) {

        Player player = getCurrentCommandIssuer().getIssuer();
        ReferralPlayer referrer = ReferralPlayer.of(player);
        if (referrer.equals(referredBy)) {
            throw new ConditionFailedException("Du kannst dich nicht selbst empfehlen.");
        }

        checkConditions(referrer);

        try {
            Referral.create(referrer, referredBy);

            getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "Vielen Dank! Du hast angegeben dass "
                    + ChatColor.YELLOW + referredBy.name()
                    + ChatColor.GREEN + " dich geworben hat.");
        } catch (ReferralException e) {
            getCurrentCommandIssuer().sendMessage(ChatColor.RED + "Die Empfehlung ist fehlgeschlagen: " + e.getMessage());
        }
    }

    @CommandAlias("reftype")
    @Subcommand("type")
    @CommandCompletion("@types")
    public void type(ReferralType type) {

        Player player = getCurrentCommandIssuer().getIssuer();
        ReferralPlayer referrer = ReferralPlayer.of(player);

        checkConditions(referrer);

        try {
            Referral.create(referrer, type);

            getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "Vielen Dank! Du hast angegeben dass du durch "
                    + ChatColor.YELLOW + type.name()
                    + ChatColor.GREEN + " auf uns gestoßen bist.");
        } catch (ReferralException e) {
            getCurrentCommandIssuer().sendMessage(ChatColor.RED + "Die Empfehlung ist fehlgeschlagen: " + e.getMessage());
        }
    }

    @Subcommand("code")
    @CommandAlias("code")
    @CommandPermission("rcreferrals.code.claim")
    public void code(PromoCode code) {

        if (!getCurrentCommandIssuer().isPlayer()) {
            return;
        }

        ReferralPlayer player = ReferralPlayer.of(getCurrentCommandIssuer().getIssuer());
        if (code.hasCode(player)) {
            throw new ConditionFailedException("Du hast diesen Code bereits eingelöst.");
        }

        if (!code.enabled()) {
            throw new ConditionFailedException("Du kannst diesen Code nicht einlösen.");
        }

        if (Instant.now().isBefore(code.start())) {
            throw new ConditionFailedException("Du kannst diesen Code erst ab dem " + TimeUtil.formatDateTime(code.start()) + " einlösen.");
        }

        if (Instant.now().isAfter(code.end())) {
            throw new ConditionFailedException("Der Code ist nicht mehr gültig und kann nicht eingelöst werden.");
        }

        if (code.amount() > 0 && code.redeemedCodes().size() > code.amount()) {
            throw new ConditionFailedException("Der Code wurde bereits zu oft von anderen Spielern eingelöst und kann nicht mehr verwendet werden.");
        }

        if (Bukkit.getPluginManager().getPlugin("art-framework") != null) {
            try {
                ART.load(code.id().toString(), code.rewards())
                        .execute(getCurrentCommandIssuer().getIssuer());
                player.addCode(code);
            } catch (ParseException e) {
                plugin.getLogger().warning("cannot load rewards of code " + code.name());
                e.printStackTrace();
                getCurrentCommandIssuer().sendMessage(ChatColor.RED + "Beim Einlösen des Codes ist ein Fehler aufgetreten. Bitte probiere es später erneut.");
                return;
            }
        } else {
            Plugin placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
            List<String> rewards = code.rewards();
            if (placeholderAPI != null) {
                rewards = PlaceholderAPI.setPlaceholders(getCurrentCommandIssuer().getIssuer(), rewards);
            }
            for (String reward : rewards) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), reward);
            }
        }
        getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "Herzlichen Glückwunsch! " + ChatColor.YELLOW + "Du hast den Code " + ChatColor.BOLD + ChatColor.AQUA + code.name()
                + ChatColor.RESET + ChatColor.GREEN + " eingelöst" + ChatColor.YELLOW + ".");

    }

    private void checkConditions(ReferralPlayer referrer) {

        if (referrer.referral() != null) {
            throw new ConditionFailedException("Vielen Dank, aber du hast bereits angegeben wie du uns gefunden hast.");
        }

        long referralTimeout = TimeUtil.parseTimeAsSeconds(plugin.getPluginConfig().getReferralTimeout());
        if (referralTimeout > 0) {
            if (Instant.now().isAfter(referrer.firstJoin().plus(referralTimeout, ChronoUnit.SECONDS))) {
                throw new ConditionFailedException("Die Zeit ist leider abgelaufen in der du angeben konntest woher du uns kennst.");
            }
        }
    }
}
