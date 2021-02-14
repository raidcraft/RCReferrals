package de.raidcraft.referrals;

import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.annotation.ConfigurationElement;
import de.exlll.configlib.annotation.ElementType;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;
import de.exlll.configlib.format.FieldNameFormatters;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PluginConfig extends BukkitYamlConfiguration {

    @Comment("The time how long a new player can provide his referral reason.")
    private String referralTimeout = "0";
    @Comment("The time in ticks the message after the login should be delayed.")
    private long loginMessageDelay = 1200;
    @Comment("The play time that is required for friend referrals to work.")
    private String requiredPlayTime = "6h";
    @Comment("The Plan activity index that is required for referrals to work.")
    private double requiredActivityIndex = 2;
    @ElementType(ReferralType.class)
    private Map<String, ReferralType> types = new HashMap<>();
    @Comment("An ART list of rewards new players get when they answer how they found the server.")
    private List<String> newPlayerRewards = new ArrayList<>();
    @Comment("An ART list of rewards for players that referred other players")
    private List<String> playerRewards = new ArrayList<>();
    private DatabaseConfig database = new DatabaseConfig();

    public PluginConfig(Path path) {

        super(path, BukkitYamlProperties.builder().setFormatter(FieldNameFormatters.LOWER_UNDERSCORE).build());
    }

    @ConfigurationElement
    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class ReferralType {

        private String name = "n/a";
        private String description = "n/a";
        private String text = "n/a";
        private boolean active = true;
    }

    @ConfigurationElement
    @Getter
    @Setter
    public static class DatabaseConfig {

        private String username = "sa";
        private String password = "sa";
        private String driver = "h2";
        private String url = "jdbc:h2:~/referrals.db";
    }
}
