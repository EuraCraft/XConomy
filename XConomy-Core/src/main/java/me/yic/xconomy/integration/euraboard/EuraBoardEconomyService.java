/*
 *  This file (EuraBoardEconomyService.java) is a part of project XConomy
 */
package me.yic.xconomy.integration.euraboard;

import me.yic.xconomy.data.DataCon;
import me.yic.xconomy.data.syncdata.PlayerData;

import java.util.UUID;

public class EuraBoardEconomyService {

    public EuraBoardEconomyRecord getByXConomyPlayerId(UUID xConomyPlayerId) {
        if (xConomyPlayerId == null) {
            return unavailable(EuraBoardEconomySourceState.PLAYER_NOT_FOUND);
        }
        try {
            return toRecord(DataCon.getPlayerData(xConomyPlayerId));
        } catch (RuntimeException e) {
            return unavailable(EuraBoardEconomySourceState.SOURCE_UNAVAILABLE);
        }
    }

    public EuraBoardEconomyRecord getByPlayerName(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            return unavailable(EuraBoardEconomySourceState.PLAYER_NOT_FOUND);
        }
        try {
            return toRecord(DataCon.getPlayerData(playerName));
        } catch (RuntimeException e) {
            return unavailable(EuraBoardEconomySourceState.SOURCE_UNAVAILABLE);
        }
    }

    private EuraBoardEconomyRecord toRecord(PlayerData playerData) {
        if (playerData == null) {
            return unavailable(EuraBoardEconomySourceState.PLAYER_NOT_FOUND);
        }
        return new EuraBoardEconomyRecord(
                playerData.getUniqueId(),
                playerData.getName(),
                playerData.getBalance(),
                EuraBoardEconomySourceState.AVAILABLE,
                null
        );
    }

    private EuraBoardEconomyRecord unavailable(EuraBoardEconomySourceState sourceState) {
        return new EuraBoardEconomyRecord(null, null, null, sourceState, null);
    }
}
