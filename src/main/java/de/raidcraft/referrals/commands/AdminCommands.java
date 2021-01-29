package de.raidcraft.referrals.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import de.raidcraft.referrals.RCReferrals;
import org.bukkit.ChatColor;

@CommandAlias("refadmin")
@CommandPermission("rcreferrals.admin")
public class AdminCommands extends BaseCommand {

    private final RCReferrals plugin;

    public AdminCommands(RCReferrals plugin) {
        this.plugin = plugin;
    }

    @Subcommand("reload")
    @CommandPermission("rcreferrals.admin.reload")
    public void reload() {

        plugin.reload();
        getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "RCReferrals wurde neugeladen.");
    }
}
