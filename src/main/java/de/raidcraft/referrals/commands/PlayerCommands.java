package de.raidcraft.referrals.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import de.raidcraft.referrals.RCReferrals;
import de.raidcraft.referrals.ReferralException;
import de.raidcraft.referrals.entities.Referral;
import de.raidcraft.referrals.entities.ReferralPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;

@CommandAlias("ref|empfehlungen|empfehlung|referral|referrals|rcreferrals")
public class PlayerCommands extends BaseCommand {

    private final RCReferrals plugin;

    public PlayerCommands(RCReferrals plugin) {
        this.plugin = plugin;
    }

    @Default
    @CommandAlias("refby|empfohlen|empfohlenvon|referredby|referred")
    @Subcommand("by|von")
    @CommandCompletion("@rplayers")
    public void refered(ReferralPlayer referredBy) {

        Player player = getCurrentCommandIssuer().getIssuer();
        ReferralPlayer referrer = ReferralPlayer.of(player);
        if (referrer.equals(referredBy)) {
            throw new ConditionFailedException("Du kannst dich nicht selbst empfehlen.");
        }

        if (referrer.referral() != null) {
            throw new ConditionFailedException("Du hast bereits einen Spieler der dich empfohlen hat angegeben.");
        }

        try {
            InetSocketAddress address = player.getAddress();
            if (address != null) {
                referrer.lastIpAddress(address.toString()).save();
            }
        } catch (Exception ignored) {
        }

        try {
            Referral.create(referrer, referredBy);

            getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "Du hast angegeben dass "
                    + ChatColor.YELLOW + referredBy.name()
                    + ChatColor.GREEN + " dich geworben hat.");
        } catch (ReferralException e) {
            getCurrentCommandIssuer().sendMessage(ChatColor.RED + "Die Empfehlung ist fehlgeschlagen: " + e.getMessage());
        }
    }
}
