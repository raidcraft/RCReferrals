package de.raidcraft.referrals.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.annotation.*;
import de.raidcraft.referrals.Messages;
import de.raidcraft.referrals.PluginConfig;
import de.raidcraft.referrals.RCReferrals;
import de.raidcraft.referrals.ReferralException;
import de.raidcraft.referrals.entities.Referral;
import de.raidcraft.referrals.entities.ReferralPlayer;
import de.raidcraft.referrals.entities.ReferralType;
import de.raidcraft.referrals.util.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@CommandAlias("ref|empfehlungen|empfehlung|referral|referrals|rcreferrals")
public class PlayerCommands extends BaseCommand {

    public static final String REF_COMMAND = "/ref";
    public static final String REF_BY_PLAYER = "/ref by ";
    public static final String REF_BY_TYPE_COMMAND = "/ref type ";

    private final RCReferrals plugin;

    public PlayerCommands(RCReferrals plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("Sage uns wie du uns gefunden hast und erhalte eine Belohnung!")
    public void referal() {

        Messages.send(getCurrentCommandIssuer(), Messages.referralChoice());
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
