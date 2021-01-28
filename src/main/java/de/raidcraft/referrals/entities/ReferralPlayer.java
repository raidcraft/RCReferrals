package de.raidcraft.referrals.entities;

import io.ebean.Finder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.ebean.BaseEntity;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Getter
@Setter
@Accessors(fluent = true)
@Table(name = "rcreferrals_players")
public class ReferralPlayer extends BaseEntity {

    public static final Finder<UUID, ReferralPlayer> find = new Finder<>(ReferralPlayer.class);

    /**
     * Gets or creates a new achievement player from the given offline player.
     * <p>The id will be the same as the players id.
     *
     * @param player the player to create or get the achievement player for
     * @return the created or existing achievement player
     */
    public static ReferralPlayer of(OfflinePlayer player) {

        return Optional.ofNullable(find.byId(player.getUniqueId()))
                .orElseGet(() -> {
                    ReferralPlayer achievementPlayer = new ReferralPlayer(player);
                    achievementPlayer.insert();
                    return achievementPlayer;
                });
    }

    /**
     * Tries to find an achievement player with the given id.
     * <p>The id is the same as the Minecraft's player id.
     * <p>Returns an empty optional if no player by the id is found.
     *
     * @param uuid the unique id of the player
     * @return the player or an empty optional
     */
    public static Optional<ReferralPlayer> byId(UUID uuid) {

        if (uuid == null) return Optional.empty();

        return Optional.ofNullable(find.byId(uuid));
    }

    /**
     * Tries to find a player with the given name.
     *
     * @param name the name of the player
     * @return the player if found
     */
    public static Optional<ReferralPlayer> byName(String name) {

        return find.query().where().ieq("name", name).findOneOrEmpty();
    }

    /**
     * The name of the player.
     */
    @Setter(AccessLevel.PRIVATE)
    private String name;

    /**
     * The referral that brought the player to the server.
     */
    @OneToOne(mappedBy = "player", orphanRemoval = true, cascade = CascadeType.ALL)
    private Referral referral;

    /**
     * The last known ip address of the player.
     */
    private String lastIpAddress;

    /**
     * The list of referrals this player made.
     */
    @OneToMany(mappedBy = "referredBy", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Referral> referrals = new ArrayList<>();

    ReferralPlayer(OfflinePlayer player) {

        this.id(player.getUniqueId());
        this.name(player.getName());
    }

    /**
     * @return the offline player of this achievement player
     */
    public OfflinePlayer offlinePlayer() {

        return Bukkit.getOfflinePlayer(id());
    }
}
