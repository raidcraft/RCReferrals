package de.raidcraft.referrals;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.google.common.base.Strings;
import de.raidcraft.referrals.commands.AdminCommands;
import de.raidcraft.referrals.commands.PlayerCommands;
import de.raidcraft.referrals.entities.ReferralPlayer;
import io.ebean.Database;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.ebean.Config;
import net.silthus.ebean.EbeanWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.stream.Collectors;

@PluginMain
public class RCReferrals extends JavaPlugin {

    @Getter
    @Accessors(fluent = true)
    private static RCReferrals instance;

    private Database database;
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private PluginConfig pluginConfig;

    private PaperCommandManager commandManager;

    @Getter
    private static boolean testing = false;

    public RCReferrals() {
        instance = this;
    }

    public RCReferrals(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        instance = this;
        testing = true;
    }

    @Override
    public void onEnable() {

        loadConfig();
        setupDatabase();
        setupListener();
        setupCommands();
    }

    public void reload() {

        loadConfig();
    }

    private void loadConfig() {

        getDataFolder().mkdirs();
        pluginConfig = new PluginConfig(new File(getDataFolder(), "config.yml").toPath());
        pluginConfig.loadAndSave();
    }

    private void setupListener() {


    }

    private void setupCommands() {

        this.commandManager = new PaperCommandManager(this);

        commandManager.getCommandCompletions().registerAsyncCompletion("rplayers",
                context -> ReferralPlayer.find.all().stream()
                .map(ReferralPlayer::name)
                .collect(Collectors.toSet()));

        commandManager.getCommandContexts().registerIssuerAwareContext(ReferralPlayer.class, context -> {
            String name = context.popFirstArg();
            if (Strings.isNullOrEmpty(name)) {
                return ReferralPlayer.of(context.getPlayer());
            } else {
                Player player = Bukkit.getPlayerExact(name);
                if (player == null) {
                    return ReferralPlayer.byName(name)
                            .orElseThrow(() -> new InvalidCommandArgument("Es wurde kein Spieler mit dem Namen "
                                    + name + " gefunden!"));
                }
                return ReferralPlayer.of(player);
            }
        });

        commandManager.registerCommand(new AdminCommands(this));
        commandManager.registerCommand(new PlayerCommands(this));
    }

    private void setupDatabase() {

        this.database = new EbeanWrapper(Config.builder(this)
                .entities(
                        // TODO: add your database entities here
                )
                .build()).connect();
    }
}
