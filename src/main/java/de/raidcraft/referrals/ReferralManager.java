package de.raidcraft.referrals;

import de.raidcraft.referrals.entities.ReferralType;
import io.ebean.Model;
import io.ebean.Transaction;
import lombok.extern.java.Log;

import java.util.List;
import java.util.Map;

@Log(topic = "RCReferrals")
public final class ReferralManager {

    private final RCReferrals plugin;

    ReferralManager(RCReferrals plugin) {

        this.plugin = plugin;
    }

    void load() {

        loadReferralTypes();
    }

    private void loadReferralTypes() {

        try (Transaction transaction = plugin.getDatabase().beginTransaction()) {
            for (Map.Entry<String, PluginConfig.ReferralType> entry : plugin.getPluginConfig().getTypes().entrySet()) {
                ReferralType.find.query()
                        .where().eq("identifier", entry.getKey())
                        .findOneOrEmpty()
                        .orElse(new ReferralType())
                        .identifier(entry.getKey())
                        .name(entry.getValue().name())
                        .description(entry.getValue().description())
                        .text(entry.getValue().text())
                        .deleted(!entry.getValue().active())
                        .save();
                log.info("loaded referral type: " + entry.getKey());
            }
            transaction.commit();
            log.info("loaded " + plugin.getPluginConfig().getTypes().size() + " referral types");
        }

        try (Transaction transaction = plugin.getDatabase().beginTransaction()) {
            List<ReferralType> obsoleteTypes = ReferralType.find.query()
                    .where().notIn("identifier", plugin.getPluginConfig().getTypes().keySet())
                    .findList();
            obsoleteTypes.forEach(Model::delete);
            transaction.commit();
            log.info("soft deleted " + obsoleteTypes.size() + " obsolete referral types");
        }
    }
}
