package de.raidcraft.template.commands;

import co.aikar.commands.BaseCommand;
import de.raidcraft.template.RCReferrals;

public class AdminCommands extends BaseCommand {

    private final RCReferrals plugin;

    public AdminCommands(RCReferrals plugin) {
        this.plugin = plugin;
    }
}
