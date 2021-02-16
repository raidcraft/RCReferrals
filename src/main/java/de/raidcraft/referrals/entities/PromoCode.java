package de.raidcraft.referrals.entities;

import io.ebean.Finder;
import io.ebean.annotation.DbDefault;
import io.ebean.annotation.DbJson;
import io.ebean.annotation.Index;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.ebean.BaseEntity;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Getter
@Setter
@Accessors(fluent = true)
@Table(name = "rcreferrals_promo_codes")
public class PromoCode extends BaseEntity {

    public static final Finder<UUID, PromoCode> find = new Finder<>(PromoCode.class);

    public static PromoCode getOrCreate(String name) {

        return find(name).orElseGet(() -> {
            PromoCode code = new PromoCode(name);
            code.insert();
            return code;
        });
    }

    public static Optional<PromoCode> find(String name) {

        return find.query().where()
                .ieq("name", name)
                .findOneOrEmpty();
    }

    @Index(unique = true)
    private String name;
    private String description;
    private int amount;
    private Instant start;
    private Instant end;
    private boolean enabled;

    PromoCode(String name) {

        this.name = name;
    }

    @DbJson
    @DbDefault("[]")
    private List<String> rewards = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RedeemedCode> redeemedCodes = new ArrayList<>();

    public boolean hasCode(ReferralPlayer player) {

        return redeemedCodes().stream()
                .map(RedeemedCode::player)
                .anyMatch(p -> p.equals(player));
    }

}
