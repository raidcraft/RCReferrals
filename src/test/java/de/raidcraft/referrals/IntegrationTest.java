package de.raidcraft.referrals;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import de.raidcraft.referrals.entities.Referral;
import de.raidcraft.referrals.entities.ReferralPlayer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class IntegrationTest {

    private ServerMock server;
    private RCReferrals plugin;

    @BeforeEach
    void setUp() {

        this.server = MockBukkit.mock();
        this.plugin = MockBukkit.load(RCReferrals.class);
    }

    @AfterEach
    void tearDown() {

        MockBukkit.unmock();
    }

    @Nested
    @DisplayName("Commands")
    class Commands {

        private Player player;

        @BeforeEach
        void setUp() {
            player = server.addPlayer();
        }

        @Nested
        @DisplayName("/ref")
        class PlayerCommands {

            @Nested
            @DisplayName("by")
            class refby {

                @Test
                @DisplayName("should work")
                void shouldWork() {

                    ReferralPlayer referralPlayer = ReferralPlayer.of(server.addPlayer("foobar"));
                    PlayerMock newPlayer = server.addPlayer();

                    newPlayer.performCommand("ref by " + referralPlayer.name());

                    Optional<ReferralPlayer> player = ReferralPlayer.byId(newPlayer.getUniqueId());

                    assertThat(player)
                            .isPresent().get()
                            .extracting(ReferralPlayer::referral)
                            .isNotNull()
                            .extracting(Referral::referredBy)
                            .isEqualTo(referralPlayer);

                    assertThat(ReferralPlayer.byId(referralPlayer.id()))
                            .isPresent().get()
                            .extracting(ReferralPlayer::referrals)
                            .asList()
                            .hasSize(1)
                            .contains(player.get().referral());
                }
            }
        }
    }
}
