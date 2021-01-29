package de.raidcraft.referrals.entities;

import io.ebean.Finder;
import io.ebean.annotation.SoftDelete;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.ebean.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Getter
@Setter
@Accessors(fluent = true)
@Table(name = "rcreferrals_types")
public class ReferralType extends BaseEntity {

    public static final Finder<UUID, ReferralType> find = new Finder<>(ReferralType.class);

    public static Optional<ReferralType> byIdentifier(String identifier) {

        return find.query()
                .where().ieq("identifier", identifier)
                .findOneOrEmpty();
    }

    public static List<ReferralType> all() {

        return find.query()
                .findList();
    }

    private String identifier;
    private String name;
    private String description;
    private String text;
    @SoftDelete
    private boolean deleted;
    @OneToMany
    private List<Referral> referrals = new ArrayList<>();
}
