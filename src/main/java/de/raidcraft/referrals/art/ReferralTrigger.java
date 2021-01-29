package de.raidcraft.referrals.art;

import de.raidcraft.referrals.entities.Referral;
import io.artframework.ExecutionContext;
import io.artframework.Requirement;
import io.artframework.RequirementContext;
import io.artframework.Result;
import io.artframework.Target;
import io.artframework.Trigger;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import lombok.NonNull;

@ART(value = "rcreferrals:referral", alias = {"referral", "ref"},
        description = {
            "The trigger is executed when a player answers the referral question.",
            "The trigger always fires for both the referral and referred."
})
public class ReferralTrigger implements Trigger, Requirement<Referral> {

    @ConfigOption(required = true, position = 0, description = "The count of referrals of other players of player that referred the new player.")
    private int count = 1;

    @Override
    public Result test(@NonNull Target<Referral> target, @NonNull ExecutionContext<RequirementContext<Referral>> executionContext) {

        return resultOf(!target.source().isPlayerReferral() || target.source().referredBy().referrals().size() >= count);
    }
}
