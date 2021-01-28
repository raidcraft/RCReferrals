package de.raidcraft.referrals;

public class ReferralException extends Exception {

    public ReferralException(String message) {
        super(message);
    }

    public ReferralException(String message, Throwable cause) {
        super(message, cause);
    }
}
