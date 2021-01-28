package de.raidcraft.referrals;

import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.annotation.ConfigurationElement;
import de.exlll.configlib.annotation.ElementType;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;
import de.exlll.configlib.format.FieldNameFormatters;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PluginConfig extends BukkitYamlConfiguration {

    @Comment("The time how long a new player can provide his referral reason.")
    private String referralTimeout = "0";
    @Comment("The time in ticks the message after the login should be delayed.")
    private long loginMessageDelay = 1200;
    @ElementType(ReferralType.class)
    private Map<String, ReferralType> types = new HashMap<>();
    private DatabaseConfig database = new DatabaseConfig();

    public PluginConfig(Path path) {

        super(path, BukkitYamlProperties.builder().setFormatter(FieldNameFormatters.LOWER_UNDERSCORE).build());
    }

    @ConfigurationElement
    @Getter
    @Setter
    public static class ReferralType {

        private String name;
        private String description;
        private String text;
        private boolean active;
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
