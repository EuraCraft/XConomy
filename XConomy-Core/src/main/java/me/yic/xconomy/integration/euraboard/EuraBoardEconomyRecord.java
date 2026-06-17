/*
 *  This file (EuraBoardEconomyRecord.java) is a part of project XConomy
 */
package me.yic.xconomy.integration.euraboard;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class EuraBoardEconomyRecord {
    private final UUID xConomyPlayerId;
    private final String playerName;
    private final BigDecimal gtaDollarBalance;
    private final EuraBoardEconomySourceState sourceState;
    private final Instant lastUpdatedAt;

    public EuraBoardEconomyRecord(UUID xConomyPlayerId, String playerName, BigDecimal gtaDollarBalance,
                                  EuraBoardEconomySourceState sourceState, Instant lastUpdatedAt) {
        this.xConomyPlayerId = xConomyPlayerId;
        this.playerName = playerName;
        this.gtaDollarBalance = gtaDollarBalance;
        this.sourceState = sourceState;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public UUID getXConomyPlayerId() {
        return xConomyPlayerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public BigDecimal getGtaDollarBalance() {
        return gtaDollarBalance;
    }

    public EuraBoardEconomySourceState getSourceState() {
        return sourceState;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }
}
