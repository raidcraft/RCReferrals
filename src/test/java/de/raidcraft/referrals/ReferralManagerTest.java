package de.raidcraft.referrals;

import de.raidcraft.referrals.entities.ReferralType;
import io.ebean.Model;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReferralManagerTest extends TestBase {

    private ReferralManager manager;

    @Override
    @BeforeEach
    void setUp() {

        super.setUp();
        manager = new ReferralManager(getPlugin());
    }

    @Override
    @AfterEach
    void tearDown() {

        super.tearDown();
        ReferralType.all().forEach(Model::deletePermanent);
    }

    @Test
    @DisplayName("should load referral types from config")
    void shouldLoadTypesFromConfig() {

        getPlugin().getPluginConfig().setTypes(Map.of(
                "test", new PluginConfig.ReferralType()
                            .name("Test")
                            .description("Test Desc")
                            .text("with ")
        ));

        manager.load();

        assertThat(ReferralType.all())
                .hasSize(1)
                .first()
                .extracting(
                        ReferralType::identifier,
                        ReferralType::name,
                        ReferralType::description,
                        ReferralType::text,
                        ReferralType::deleted
                ).contains(
                    "test",
                    "Test",
                    "Test Desc",
                    "with ",
                    false);
    }

    @Test
    @DisplayName("should soft delete obsolete type entries")
    void shouldSoftDeleteObsoleteEntries() {

        new ReferralType()
                .identifier("foobar")
                .name("Foo")
                .insert();

        manager.load();

        assertThat(ReferralType.all())
                .isEmpty();

        assertThat(ReferralType.find.query().setIncludeSoftDeletes().findList())
                .hasSize(1);
    }
}