package de.raidcraft.referrals.art;

import de.raidcraft.referrals.entities.ReferralPlayer;
import io.artframework.ExecutionContext;
import io.artframework.Requirement;
import io.artframework.RequirementContext;
import io.artframework.Result;
import io.artframework.Target;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

@ART(value = "rcreferrals:referrals", alias = {"referrals", "refs", "refcount"}, description = "Checks the number of players the target referred.")
public class ReferralCountRequirement implements Requirement<OfflinePlayer> {

    @ConfigOption(required = true, position = 0, description = "The count of referrals of other players of the given player.")
    private int count = 1;

    @Override
    public Result test(@NonNull Target<OfflinePlayer> target, @NonNull ExecutionContext<RequirementContext<OfflinePlayer>> executionContext) {

        return resultOf(ReferralPlayer.of(target.source()).referrals().size() >= count);
    }
}
