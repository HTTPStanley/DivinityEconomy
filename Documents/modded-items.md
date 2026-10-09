# Modded Items – Codebase Guide

Developer notes for the modded-item support used on hybrid servers (Arclight etc.). For the admin-facing guide see the
"Modded Items" section of the [README](../README.md).

## Goal

Hybrid servers register mod items as extra Bukkit `Material`s (namespaced keys such as `cobblemon:mago_berry`).
DivinityEconomy discovers them, adds them to `materials.yml` (disabled), and lets admins enable them.

## Flow

```
MaterialManager.init()
 ├─ loadItems()          TokenManager – vanilla keys from the bundled materials.yml, then any extra keys in the
 │                       server's materials.yml. Modded entries whose material doesn't resolve yet are logged as
 │                       "pending" (MODDED_Pending) and skipped.
 ├─ loadAliases()
 └─ runTaskLater(200)    reloadModdedItems()   (~10s later; hybrids register materials late)

/modded  ──► Modded command ──► MaterialManager.reloadModdedItems()
```

`MaterialManager.reloadModdedItems()` (returns number of items resolved + imported):

1. **Resolve** – for each key in `materials.yml` that isn't bundled and isn't in `itemMap`: `loadItem(...)`; if
   `check()` passes, add it to the market.
2. **Import** – for each `Material` whose namespace isn't `minecraft`: skip if already present; otherwise write
   `MATERIAL_ID`, `QUANTITY: 0`, `ALLOWED: false`, load it, and add it if `check()` passes (else roll the config back).
   An alias for the part after the colon is added via `TokenManager.addAlias`.
3. If anything was imported: `saveItems()` and `saveAliases()`.

## Key design rules

| Rule | Where |
|------|-------|
| **Unresolved materials never enter the market.** `MarketableBlock.check()` is `material != null`; there is no placeholder item. | `MarketableBlock` |
| **Imports are disabled with 0 stock**, admins opt in. `ALLOWED` is the same flag `/banitem` toggles (`/banitem <item> false` enables), so the normal ban/unban workflow applies; unbanning does not set stock. | `reloadModdedItems`, `BanItem` |
| **`.` is a config path separator**, so keys go through `ConfigKeys.safe()` (`.` → `_`); the real id lives in `MATERIAL_ID`. Key collisions are logged and skipped. | `ConfigKeys`, `reloadModdedItems`, `addAlias` |
| **Aliases never shadow** an existing alias or item. | `TokenManager.addAlias` |
| **Alias writes are async but serialised**: snapshot the map on the calling thread, write via the Bukkit scheduler under `aliasSaveLock`; inline if the plugin is disabled. | `TokenManager.saveAliases` |
| **Material resolution** tries `Material.valueOf`, `matchMaterial`, a `NamespacedKey`, then a scan of `Material.values()` by key/name. | `MarketableMaterial` constructor |
| **Every message is a `LangEntry`** (`MODDED_*`) with an entry in all 20 `src/locale/*.yml` files. | `LangEntry`, `src/locale` |

## Files

| File | Role |
|------|------|
| `commands/admin/Modded.java` | `/modded` command; own setting `Commands.Admin.Modded` (`Setting.COMMAND_MODDED_ENABLE_BOOLEAN`) |
| `market/items/materials/MaterialManager.java` | `reloadModdedItems()`, delayed startup scan |
| `market/TokenManager.java` | modded-key loading in `loadItems`, `addAlias`, `saveAliases`, alias-file corruption backup |
| `market/items/materials/MarketableMaterial.java` | material resolution |
| `market/items/materials/block/MarketableBlock.java` | strict `check()` |
| `utils/ConfigKeys.java` | dot-safe config keys (tested in `ConfigKeysTest`) |
| `src/resources/plugin.yml` | `modded` command, `de.admin.modded` permission (child of `de.admin`) |

## Adding a message

1. Add `MODDED_Something()` to `LangEntry` (under `// Modded Items`).
2. Add the key to `src/locale/en_GB.yml` and every other locale (keep `%s`/`%d` placeholders in the same order – the
   arguments are passed to `String.format`).
3. Use it with `getConsole().info(LangEntry.MODDED_Something.get(getMain()), args...)`.

## Known limitations

- Needs a server that exposes modded items through Bukkit's `Material`; nothing happens elsewhere.
- Items with `QUANTITY: 0` can't be priced until an admin sets stock (and `PRICE` for the Static pricing model).
- Pricing for imported items uses the defaults (`ELASTICITY` 0.7); there is no mod-aware price seeding.
- The import and collision paths have no automated tests (they need a server that registers modded materials).
- Locale translations of the `MODDED_*` messages are machine-generated.
