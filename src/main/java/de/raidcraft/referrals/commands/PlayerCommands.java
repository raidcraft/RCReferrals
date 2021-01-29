package de.raidcraft.referrals.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import de.raidcraft.referrals.Messages;
import de.raidcraft.referrals.RCReferrals;
import de.raidcraft.referrals.ReferralException;
import de.raidcraft.referrals.entities.Referral;
import de.raidcraft.referrals.entities.ReferralPlayer;
import de.raidcraft.referrals.entities.ReferralType;
import de.raidcraft.referrals.util.TimeUtil;
import io.artframework.ArtContext;
import io.ebean.Transaction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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

        Messages.send(getCurrentCommandIssuer(), Messages.referralChoice());
    }

    @Subcommand("claim")
    @CommandAlias("refclaim")
    @Description("Hole deine Belohnung für die Empfehlung von anderen Spielern ab.")
    public void claim() {

        Player player = getCurrentCommandIssuer().getIssuer();
        ReferralPlayer referralPlayer = ReferralPlayer.of(player);

        List<Referral> pendingRewards = referralPlayer.referrals()
                .stream().filter(Referral::rewardPending)
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
