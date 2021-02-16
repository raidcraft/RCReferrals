package de.raidcraft.referrals.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import de.raidcraft.referrals.Messages;
import de.raidcraft.referrals.RCReferrals;
import de.raidcraft.referrals.entities.PromoCode;
import de.raidcraft.referrals.util.TimeUtil;
import io.ebean.Model;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;

import java.time.Instant;

import static de.raidcraft.referrals.Messages.Colors.*;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

@CommandAlias("refadmin")
@CommandPermission("rcreferrals.admin")
public class AdminCommands extends BaseCommand {

    private final RCReferrals plugin;

    public AdminCommands(RCReferrals plugin) {
        this.plugin = plugin;
    }

    @Subcommand("code")
    @CommandPermission("rcreferrals.admin.code")
    public class CodeCommands extends BaseCommand {

        @Subcommand("info")
        @CommandCompletion("@codes")
        public void info(PromoCode code) {

            TextComponent.Builder builder = text().append(text(code.name(), TEXT)).append(newline())
                    .append(text(code.description(), NOTE, ITALIC))
                    .append(newline())
                    .append(text("Menge: ", TEXT))
                    .append(text(code.amount(), HIGHLIGHT))
                    .append(newline())
                    .append(text("Start: ", TEXT))
                    .append(text(TimeUtil.formatDateTime(code.start()), HIGHLIGHT))
                    .append(newline())
                    .append(text("Ende: ", TEXT))
                    .append(text(TimeUtil.formatDateTime(code.end()), HIGHLIGHT))
                    .append(newline())
                    .append(text("Aktiv: ", TEXT))
                    .append(text(code.enabled() ? "JA" : "NEIN", code.enabled() ? SUCCESS : ERROR))
                    .append(newline())
                    .append(text("Belohnungen: ", TEXT))
                    .append(text(code.rewards().size(), HIGHLIGHT))
                    .append(newline());

            for (int i = 0; i < code.rewards().size(); i++) {
                builder.append(text(i, HIGHLIGHT)).append(text(": ", TEXT))
                        .append(text(code.rewards().get(i), NOTE))
                        .append(newline());
            }

            Messages.send(getCurrentCommandIssuer(), builder.build());
        }

        @Subcommand("create")
        @CommandCompletion("* *")
        @CommandPermission("rcreferrals.admin.code.create")
        public void code(String name, @Optional String description) {

            if (PromoCode.find(name).isPresent()) {
                throw new InvalidCommandArgument("Den Promo Code gibt es bereits: " + name);
            }

            PromoCode code = PromoCode.getOrCreate(name.toUpperCase())
                    .description(description);

            getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "Der Promo Code "
                    + ChatColor.GOLD + code.name().toUpperCase()
                    + ChatColor.GREEN + " wurde erstellt.");
            getCurrentCommandIssuer().sendMessage(ChatColor.GRAY + "Nutze " + ChatColor.GOLD
                    + "/refadmin set code start|end|enabled|description|amount "
                    + ChatColor.GRAY + " um den Promo Code zu bearbeiten."
            );
        }

        @Subcommand("set")
        @CommandPermission("rcreferrals.admin.code.set")
        public class SetCode extends BaseCommand {

            @Subcommand("start")
            @CommandCompletion("@codes 30.03.2021-21:00")
            public void setStart(PromoCode code, Instant instant) {

                code.start(instant).save();
                getCurrentCommandIssuer().sendMessage("Der Startpunkt des Codes " + code.name()
                        + " wurde auf " + TimeUtil.formatDateTime(instant) + " gesetzt.");
            }

            @Subcommand("end")
            @CommandCompletion("@codes 30.03.2021-21:00")
            public void setEnd(PromoCode code, Instant instant) {

                code.end(instant).save();
                getCurrentCommandIssuer().sendMessage("Der Endzeitpunkt des Codes " + code.name()
                        + " wurde auf " + TimeUtil.formatDateTime(instant) + " gesetzt.");
            }

            @Subcommand("description")
            @CommandCompletion("@codes *")
            public void setDesc(PromoCode code, String description) {

                code.description(description).save();
                getCurrentCommandIssuer().sendMessage("Die Beschreibung des Codes " + code.name()
                        + " wurde auf " + description + " gesetzt.");
            }

            @Subcommand("enabled")
            @CommandCompletion("@codes true|false")
            public void setEnabled(PromoCode code, boolean enabled) {

                code.enabled(enabled).save();
                if (enabled) {
                    getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "Der Code " + code.name() + " wurde aktiviert.");
                } else {
                    getCurrentCommandIssuer().sendMessage(ChatColor.RED + "Der Code " + code.name() + " wurde deaktiviert.");
                }
            }

            @Subcommand("amount")
            @CommandCompletion("@codes *")
            public void setAmount(PromoCode code, int amount) {

                code.amount(amount).save();
                getCurrentCommandIssuer().sendMessage("Die Menge des Codes " + code.name()
                        + " wurde auf " + amount + " gesetzt.");
            }
        }

        @Subcommand("rewards")
        @CommandPermission("rcreferrals.admin.code.rewards")
        public class CodeRewards {

            @Subcommand("add")
            @CommandCompletion("@codes *")
            public void add(PromoCode code, String reward) {

                code.rewards().add(reward);
                code.save();
                getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "Die Belohnung wurde zum Code " + code.name() + " hinzugefügt.");
            }

            @Subcommand("remove")
            @CommandCompletion("@codes *")
            public void remove(PromoCode code, int index) {

                if (code.rewards().size() < index) {
                    throw new InvalidCommandArgument("Es gibt keine Belohnung mit der ID " + index);
                }
                String remove = code.rewards().remove(index);
                code.save();
                getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "Die Belohnung \"" + remove
                        + "\" wurde vom Code " + code.name() + " entfernt.");
            }

            @Subcommand("clear")
            @CommandCompletion("@codes")
            public void clear(PromoCode code) {

                code.rewards().clear();
                code.save();
                getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "Die Belohnungen wurden vom Code " + code.name() + " gelöscht.");
            }
        }

        @Subcommand("clear")
        @CommandPermission("rcreferrals.admin.code.clear")
        @CommandCompletion("@codes confirm")
        public void clear(PromoCode code, @Optional String confirm) {

            if (!"confirm".equalsIgnoreCase(confirm)) {
                throw new InvalidCommandArgument("Dieser Befehl löscht alle bisherigen claims des Codes "
                        + code.name() + ". Bestätige mit /refadmin code clear confirm.");
            }

            code.redeemedCodes().forEach(Model::delete);
            getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "Alle Claims des Codes " + code.name() + " wurden gelöscht.");
        }
    }

    @Subcommand("reload")
    @CommandPermission("rcreferrals.admin.reload")
    public void reload() {

        plugin.reload();
        getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "RCReferrals wurde neugeladen.");
    }
}
