package de.raidcraft.referrals;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import de.raidcraft.referrals.entities.Referral;
import de.raidcraft.referrals.entities.ReferralPlayer;
import de.raidcraft.referrals.entities.ReferralType;
import de.raidcraft.referrals.listener.RewardListener;
import io.artframework.ArtContext;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

            @Nested
            @DisplayName("type")
            class type {

                @BeforeEach
                void setUp() {

                    getPlugin().getPluginConfig().setTypes(Map.of(
                            "test", new PluginConfig.ReferralType()
                                    .name("Test")
                                    .description("Test Desc")
                                    .text("with ")
                    ));

                    getPlugin().getReferralManager().load();
                }

                @Test
                @DisplayName("referral by type should work")
                void shouldWork() {

                    PlayerMock player = getServer().addPlayer();
                    player.performCommand("ref type test");

                    assertThat(ReferralPlayer.of(player))
                            .extracting(ReferralPlayer::referral)
                            .isNotNull()
                            .extracting(Referral::type)
                            .isNotNull()
                            .extracting(ReferralType::identifier)
                            .isEqualTo("test");
                }
            }

            @Nested
            @DisplayName("claim")
            class claim {

                private ArtContext rewards;

                @BeforeEach
                void setUp() {

                    RewardListener rewardListener = mock(RewardListener.class);
                    rewards = mock(ArtContext.class);
                    when(rewardListener.getPlayerRewards()).thenReturn(rewards);
                    getPlugin().setRewardListener(rewardListener);
                }

                @Test
                @DisplayName("should not be able to claim rewards if player has no active referrals")
                void shouldNotClaimRewardsIfNoReferrals() {

                    PlayerMock player = getServer().addPlayer();
                    player.performCommand("ref claim");

                    verify(rewards, never()).execute(any());
                }

                @Test
                @DisplayName("should be able to claim pending rewards only if claimable")
                void shouldBeAbleToClaimPendingRewards() {

                    PlayerMock foobar = getServer().addPlayer("foobar");
                    PlayerMock newPlayer = getServer().addPlayer("new");
                    newPlayer.performCommand("ref by foobar");
                    ReferralPlayer.of(newPlayer).referral().claimable(true).save();

                    foobar.performCommand("ref claim");

                    verify(rewards, times(1)).execute(foobar);
                    assertThat(Referral.find.all())
                            .asList()
                            .noneMatch(o -> ((Referral)o).rewardPending());
                }
            }
        }
    }
}
