package de.raidcraft.referrals;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.google.common.base.Strings;
import de.raidcraft.referrals.art.ReferralCountRequirement;
import de.raidcraft.referrals.art.ReferralTrigger;
import de.raidcraft.referrals.commands.AdminCommands;
import de.raidcraft.referrals.commands.PlayerCommands;
import de.raidcraft.referrals.entities.PromoCode;
import de.raidcraft.referrals.entities.RedeemedCode;
import de.raidcraft.referrals.entities.Referral;
import de.raidcraft.referrals.entities.ReferralPlayer;
import de.raidcraft.referrals.entities.ReferralType;
import de.raidcraft.referrals.listener.PlayerListener;
import de.raidcraft.referrals.listener.RewardListener;
import io.artframework.Scope;
import io.artframework.annotations.ArtModule;
import io.artframework.annotations.OnEnable;
import io.artframework.annotations.OnLoad;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;

@PluginMain
@ArtModule(value = "rcreferrals", description = "Adds requirements and trigger for player referrals.")
public class RCReferrals extends JavaPlugin {

    @Getter
    @Accessors(fluent = true)
    private static RCReferrals instance;

    @Getter
    private Database database;
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private PluginConfig pluginConfig;

    @Getter
    private ReferralManager referralManager;
    private PaperCommandManager commandManager;
    private PlayerListener playerListener;
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private RewardListener rewardListener;

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
        setupReferralManager();
        setupListener();
        setupCommands();
    }

    @OnLoad
    public void onLoad(Scope scope) {

        scope.register()
                .requirements().add(ReferralCountRequirement.class)
                .trigger().add(ReferralTrigger.class);
    }

    @OnEnable
    public void onEnable(Scope scope) {

        rewardListener = new RewardListener(this, scope);
        rewardListener.load();
        getServer().getPluginManager().registerEvents(rewardListener, this);
    }

    public void reload() {

        loadConfig();
        referralManager.load();
        if (rewardListener != null) {
            rewardListener.load();
        }
    }

    private void loadConfig() {

        getDataFolder().mkdirs();
        pluginConfig = new PluginConfig(new File(getDataFolder(), "config.yml").toPath());
        pluginConfig.loadAndSave();
    }

    private void setupListener() {

        playerListener = new PlayerListener(this);
        getServer().getPluginManager().registerEvents(playerListener, this);
    }

    private void setupReferralManager() {

        referralManager = new ReferralManager(this);
        referralManager.load();
    }

    private void setupCommands() {

        this.commandManager = new PaperCommandManager(this);

        commandManager.getCommandCompletions().registerAsyncCompletion("rplayers",
                context -> ReferralPlayer.find.all().stream()
                .map(ReferralPlayer::name)
                .collect(Collectors.toSet()));

        commandManager.getCommandCompletions().registerAsyncCompletion("types",
                context -> ReferralType.all().stream().map(ReferralType::identifier).collect(Collectors.toSet()));

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

        commandManager.getCommandContexts().registerContext(ReferralType.class, context -> {
            String identifier = context.popFirstArg();
            return ReferralType.byIdentifier(identifier)
                    .orElseThrow(() -> new InvalidCommandArgument("There is not referral type with the identifier " + identifier));
        });

        commandManager.getCommandContexts().registerContext(PromoCode.class, context -> {
            String name = context.popFirstArg();
            return PromoCode.find(name)
                    .orElseThrow(() -> new InvalidCommandArgument("Es gibt keinen Code mit dem Namen: " + name));
        });

        commandManager.getCommandContexts().registerContext(Instant.class, context -> {
            String arg = context.popFirstArg();
            try {
                return LocalDateTime.parse(
                        arg,
                        DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm", Locale.GERMANY)
                ).atZone(ZoneId.of("Europe/Berlin"))
                        .toInstant();
            } catch (Exception e) {
                throw new InvalidCommandArgument("Der Zeitpunkt \"" + arg + "\" ist falsch formatiert: 30.03.2021-21:54");
            }
        });

        commandManager.getCommandCompletions().registerAsyncCompletion("codes",
                context -> PromoCode.find.all().stream().map(PromoCode::name).collect(Collectors.toSet()));

        commandManager.registerCommand(new AdminCommands(this));
        commandManager.registerCommand(new PlayerCommands(this));
    }

    private void setupDatabase() {

        this.database = new EbeanWrapper(Config.builder(this)
                .entities(
                        Referral.class,
                        ReferralPlayer.class,
                        ReferralType.class,
                        PromoCode.class,
                        RedeemedCode.class
                )
                .build()).connect();
    }
}
