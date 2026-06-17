# EuraBoard Economy Contract

## Scope

XConomy is the source of truth for GTA dollar balances that EuraBoard may display later on `/me/economy`.

This integration exposes the current XConomy player balance only. It does not create a ledger, does not derive variations, and does not synthesize historical transactions.

## Maven Project Structure

The repository is a Maven multi-module project rooted at `pom.xml`.

- `XConomy-Core`: shared economy logic, API, cache, SQL access, synchronization data, commands.
- `XConomy-Bukkit`: Bukkit/Spigot platform implementation and Vault economy provider; shades `XConomy-Core`.
- `XConomy-Paper`: Paper-specific package; depends on `XConomy-Bukkit`.
- `XConomy-Sponge7`: Sponge 7 package; shades `XConomy-Core`.
- `XConomy-Sponge8`: Sponge 8 package; shades `XConomy-Core`.

Build from the repository root so Maven can resolve the local module dependencies in the reactor.

```bash
mvn clean install
```

## Balance Source

The canonical in-memory class is:

- `me.yic.xconomy.data.syncdata.PlayerData`
- field: `balance`
- getter: `getBalance()`

The read path used by XConomy is:

- `me.yic.xconomy.data.DataCon#getPlayerData(UUID)`
- `me.yic.xconomy.data.DataCon#getPlayerData(String)`
- `me.yic.xconomy.data.DataLink#getPlayerData(...)`
- `me.yic.xconomy.data.sql.SQL#getPlayerData(UUID/String)`

The canonical SQL table is:

- default table: `xconomy`
- MySQL/MariaDB with configured table suffix: `xconomy_<suffix>`
- columns:
  - `UID`: player identifier stored by XConomy
  - `player`: last known player name
  - `balance`: current GTA dollar balance
  - `hidden`: top-list visibility flag, not part of the EuraBoard balance contract

SQLite uses the same table schema inside XConomy's configured SQLite database file.

## Player Key

The XConomy player key is the `UID` column in the `xconomy` table, represented in Java as `UUID`.

In normal UUID modes, this is the player's UUID. In `SemiOnline` mode, XConomy can resolve an incoming UUID through the `xconomyuuid` table and use the mapped `DUUID` as the stored account key. EuraBoard should therefore treat `EuraBoardEconomyRecord#getXConomyPlayerId()` as the authoritative XConomy account id returned by XConomy, not blindly assume it is always the lookup UUID.

The player name is read from `xconomy.player` when available.

## Transaction History

XConomy can create `xconomyrecord` only when all of these are true:

- storage is MySQL/MariaDB;
- `Settings.transaction-record` is enabled;
- the mutation path records the operation.

That table is not a reliable complete source for reconstructing balance history because:

- it is optional and MySQL-only;
- it can start after existing balances already exist;
- initial account creation/imported balances are not a complete transaction stream;
- bulk operations can be recorded without per-player ids;
- it stores a post-change balance but not a complete before/after model;
- direct database edits are outside the record stream.

For that reason, this integration does not expose history, recent variation, generated transactions, or a synthetic ledger.

## EuraBoard Consumption

EuraBoard may consume the read-only projection:

- `me.yic.xconomy.integration.euraboard.EuraBoardEconomyRecord`
- `me.yic.xconomy.integration.euraboard.EuraBoardEconomyService`
- `me.yic.xconomy.api.XConomyAPI#getEuraBoardEconomyRecord(UUID)`
- `me.yic.xconomy.api.XConomyAPI#getEuraBoardEconomyRecord(String)`

`EuraBoardEconomyRecord` exposes:

- `getXConomyPlayerId()`: authoritative XConomy player id, or `null` when unavailable;
- `getPlayerName()`: XConomy player name, or `null` when unavailable;
- `getGtaDollarBalance()`: real current XConomy balance, or `null` when unavailable;
- `getSourceState()`: `AVAILABLE`, `PLAYER_NOT_FOUND`, or `SOURCE_UNAVAILABLE`;
- `getLastUpdatedAt()`: currently `null`, because the canonical balance row does not store a reliable last update timestamp.

## EuraBoard Must Not Invent

EuraBoard must not invent:

- balance variations;
- transaction rows;
- historical balances;
- timestamps when `lastUpdatedAt` is `null`;
- balances derived from GTAAchievement or any other plugin.

If the source state is not `AVAILABLE`, EuraBoard should treat the balance as unavailable instead of displaying a fabricated value.

## Current Limits

- The integration exposes current player balances only.
- Non-player accounts in `xconomynon` are not part of this EuraBoard player economy contract.
- Last update time is not available from the canonical `xconomy` row.
- No historical reconstruction is provided.
- Name-based lookups follow XConomy's existing name and UUID-mode behavior.
