package de.raidcraft.template.events;

import de.raidcraft.template.entities.ReferralPlayer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerReferredByPlayerEvent extends RCReferralEvent implements Cancellable {
    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final ReferralPlayer referral;
    private final ReferralPlayer referredBy;
    private boolean cancelled;

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
