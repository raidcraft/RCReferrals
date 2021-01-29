package de.raidcraft.referrals;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import de.raidcraft.referrals.entities.ReferralPlayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public abstract class TestBase {

    private ServerMock server;
    private RCReferrals plugin;

    @BeforeEach
    void setUp() {

        this.server = MockBukkit.mock();
        this.plugin = MockBukkit.load(RCReferrals.class);
    }

    @AfterEach
    void tearDown() {

        ReferralPlayer.find.query().delete();
        MockBukkit.unmock();
    }

    protected ReferralPlayer referralPlayer(String name) {

        return ReferralPlayer.byName(name)
                .orElseGet(() -> ReferralPlayer.of(server.addPlayer(name)));
    }
}
