package de.raidcraft.referrals;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import de.raidcraft.referrals.entities.Referral;
import de.raidcraft.referrals.entities.ReferralPlayer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SuppressWarnings("ALL")
public class IntegrationTest extends TestBase {

    @Nested
    @DisplayName("Commands")
    class Commands {

        private Player player;

        @BeforeEach
        void setUp() {
            player = getServer().addPlayer();
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

                    ReferralPlayer referralPlayer = referralPlayer("foobar");
                    PlayerMock newPlayer = getServer().addPlayer();

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

                @Test
                @DisplayName("should not allow referral of referred players")
                void shouldNotAllowReferringWhenAlreadyReferred() {

                    ReferralPlayer referralPlayer = referralPlayer("foobar");
                    PlayerMock newPlayer = getServer().addPlayer();

                    newPlayer.performCommand("ref by " + referralPlayer.name());

                    ReferralPlayer second = referralPlayer("second");
                    newPlayer.performCommand("ref by " + second.name());

                    Optional<ReferralPlayer> player = ReferralPlayer.byId(newPlayer.getUniqueId());

                    assertThat(player)
                            .isPresent().get()
                            .extracting(ReferralPlayer::referral)
                            .isNotNull()
                            .extracting(Referral::referredBy)
                            .isEqualTo(referralPlayer);

                    assertThat(ReferralPlayer.byId(second.id()))
                            .isPresent().get()
                            .extracting(ReferralPlayer::referrals)
                            .asList()
                            .isEmpty();
                }

                @Test
                @DisplayName("should not allow referral after timeout")
                void shouldNotAllowReferralWhenTimeout() {

                    getPlugin().getPluginConfig().setReferralTimeout("10s");

                    ReferralPlayer referralPlayer = referralPlayer("foobar");
                    PlayerMock newPlayer = getServer().addPlayer();
                    Optional<ReferralPlayer> player = ReferralPlayer.byId(newPlayer.getUniqueId());
                    player.get().firstJoin(Instant.now().minus(1, ChronoUnit.MINUTES)).save();

                    newPlayer.performCommand("ref by " + referralPlayer.name());

                    assertThat(ReferralPlayer.byId(newPlayer.getUniqueId()))
                            .isPresent().get()
                            .extracting(ReferralPlayer::referral)
                            .isNull();
                }
            }
        }
    }
}
