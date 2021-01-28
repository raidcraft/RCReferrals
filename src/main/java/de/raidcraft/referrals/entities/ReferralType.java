package de.raidcraft.referrals.entities;

import io.ebean.Finder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.ebean.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Accessors(fluent = true)
@Table(name = "rcreferrals_types")
public class ReferralType extends BaseEntity {

    public static final Finder<UUID, ReferralType> find = new Finder<>(ReferralType.class);

    public static List<ReferralType> activeTypes() {

        return find.query()
                .where().eq("active", true)
                .findList();
    }

    private String identifier;
    private String name;
    private String description;
    private String text;
    private boolean active = true;
    @OneToMany
    private List<Referral> referrals = new ArrayList<>();
}
