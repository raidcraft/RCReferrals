package de.raidcraft.referrals;

import co.aikar.commands.CommandIssuer;
import de.raidcraft.referrals.commands.PlayerCommands;
import de.raidcraft.referrals.entities.ReferralPlayer;
import de.raidcraft.referrals.entities.ReferralType;
import de.raidcraft.referrals.util.TimeUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;

import static de.raidcraft.referrals.Messages.Colors.*;
import static de.raidcraft.referrals.commands.PlayerCommands.REF_BY_TYPE_COMMAND;
import static de.raidcraft.referrals.commands.PlayerCommands.REF_COMMAND;
import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.ClickEvent.suggestCommand;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

public final class Messages {

    public static final class Colors {

        public static final TextColor BASE = YELLOW;
        public static final TextColor TEXT = BASE;
        public static final TextColor ACCENT = GOLD;
        public static final TextColor DARK_ACCENT = DARK_AQUA;
        public static final TextColor HIGHLIGHT = AQUA;
        public static final TextColor DARK_HIGHLIGHT = DARK_AQUA;
        public static final TextColor ERROR = RED;
        public static final TextColor ERROR_ACCENT = DARK_RED;
        public static final TextColor SUCCESS = GREEN;
        public static final TextColor SUCCESS_ACCENT = DARK_GREEN;
        public static final TextColor WARNING = GOLD;
        public static final TextColor NOTE = GRAY;
    }

    public static void send(UUID playerId, Component message) {

        if (RCReferrals.isTesting()) return;
        BukkitAudiences.create(RCReferrals.instance())
                .player(playerId)
                .sendMessage(message);
    }

    public static void send(UUID playerId, Consumer<TextComponent.Builder> message) {

        TextComponent.Builder builder = text();
        message.accept(builder);
        send(playerId, builder.build());
    }

    public static void send(Object commandIssuer, Component message) {

        if (commandIssuer instanceof ReferralPlayer) {
            send(((ReferralPlayer) commandIssuer).id(), message);
        } else if (commandIssuer instanceof Player) {
            sendPlayer((Player) commandIssuer, message);
        } else if (commandIssuer instanceof ConsoleCommandSender) {
            sendConsole((ConsoleCommandSender) commandIssuer, message);
        } else if (commandIssuer instanceof RemoteConsoleCommandSender) {
            sendRemote((RemoteConsoleCommandSender) commandIssuer, message);
        } else if (commandIssuer instanceof CommandIssuer) {
            send((Object) ((CommandIssuer) commandIssuer).getIssuer(), message);
        }
    }

    public static void send(UUID target, Title title) {

        if (RCReferrals.isTesting()) return;
        BukkitAudiences.create(RCReferrals.instance())
                .player(target)
                .showTitle(title);
    }

    public static void sendPlayer(Player player, Component message) {
        send(player.getUniqueId(), message);
    }

    public static void sendConsole(ConsoleCommandSender sender, Component message) {

        sender.sendMessage(PlainComponentSerializer.plain().serialize(message));
    }

    public static void sendRemote(RemoteConsoleCommandSender sender, Component message) {

        sender.sendMessage(PlainComponentSerializer.plain().serialize(message));
    }

    public static Component referralChoice() {

        TextComponent.Builder builder = text().append(text("Woher kennst du uns? Eine kurze Antwort und du bekommst eine ", TEXT))
                .append(text("Belohnung:", SUCCESS, BOLD))
                .append(newline())
                .append(text().append(text("  - ", TEXT)
                        .append(text("von einem ", NOTE))
                        .append(text("FREUND", ACCENT)))
                        .hoverEvent(text("Klicke um den Namen deines Freundes anzugeben und ihr erhaltet beide eine ", NOTE)
                                .append(text("Belohnung!", SUCCESS, BOLD)))
                        .clickEvent(suggestCommand(PlayerCommands.REF_BY_PLAYER))
                );

        for (ReferralType type : ReferralType.activeTypes()) {
            builder.append(newline()).append(text().append(text("  - ", TEXT))
                    .append(text(type.text(), NOTE))
                    .append(text(type.name().toUpperCase(), ACCENT))
                    .hoverEvent(text(type.description(), NOTE))
                    .clickEvent(runCommand(REF_BY_TYPE_COMMAND + type.identifier()))
            );
        }

        return builder.build();
    }

    public static Component referralNotice(Instant timeout) {

        return text().append(text("Sage uns noch bis ", TEXT))
                .append(text(TimeUtil.formatDateTime(timeout, "HH:mm"), HIGHLIGHT))
                .append(text(" woher du uns kennst und erhalte eine ", TEXT))
                .append(text("Belohnung!", SUCCESS, BOLD)
                        .hoverEvent(text("Was für eine Belohnung?", NOTE)
                                .append(newline())
                                .append(text("Sage uns wie du uns gefunden hast und finde es heraus :)", ACCENT))
                        )
                )
                .append(newline())
                .append(text("Klicke dafür ", NOTE))
                .append(text("hier", ACCENT)
                        .clickEvent(runCommand(REF_COMMAND))
                        .hoverEvent(text("Klicke um den Befehl ", NOTE).append(text(REF_COMMAND, ACCENT)).append(text(" auszuführen.", NOTE)))
                ).append(text(" oder gebe ", NOTE))
                .append(text(REF_COMMAND, ACCENT)
                        .clickEvent(runCommand(REF_COMMAND))
                        .hoverEvent(text("Klicke um den Befehl ", NOTE).append(text(REF_COMMAND, ACCENT)).append(text(" auszuführen.", NOTE)))
                ).append(text(" ein.", NOTE))
                .build();
    }
}
