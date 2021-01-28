package de.raidcraft.referrals;

import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.annotation.ConfigurationElement;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;
import de.exlll.configlib.format.FieldNameFormatters;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Getter
@Setter
public class PluginConfig extends BukkitYamlConfiguration {

    @Comment("The time in ticks how long a new player can provide his referral reason.")
    private long referralTimeout = 0;
    @Comment("The time in ticks the message after the login should be delayed.")
    private long loginMessageDelay = 1200;
    private DatabaseConfig database = new DatabaseConfig();

    public PluginConfig(Path path) {

        super(path, BukkitYamlProperties.builder().setFormatter(FieldNameFormatters.LOWER_UNDERSCORE).build());
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
