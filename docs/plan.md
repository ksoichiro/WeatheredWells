# WeatheredWells Development Plan

## Phase 1: Project Scaffolding

Set up the multi-loader, multi-version project structure based on the FarmersMiracle architecture.

### 1.1 Root Build Configuration

- Create `build.gradle` with Architectury Plugin 3.4 + Loom 1.11
- Create `settings.gradle` with dynamic project inclusion based on `target_mc_version`
- Create `gradle.properties` with default `target_mc_version=1.21.1`, mod metadata (`mod_id=weatheredwells`, `mod_version`, `maven_group`, etc.)
- Create `props/1.21.1.properties` with version-specific dependency versions (Fabric Loader, NeoForge, Architectury API, Fabric API, Minecraft mappings)
- Set `enabled_platforms=fabric,neoforge`
- Create `multi-version-tasks.gradle` for convenience build tasks (`build1_21_1`, `buildAll`, `release`, etc.)
- Set up Gradle wrapper (Java 21)

### 1.2 Directory Structure

Create the following module directories:

```
common-shared/src/main/java/com/weatheredwells/
common-1.21.1/src/main/java/com/weatheredwells/
common-1.21.1/src/main/resources/
fabric-base/src/main/java/com/weatheredwells/fabric/
fabric-1.21.1/src/main/java/com/weatheredwells/fabric/
fabric-1.21.1/src/main/resources/
neoforge-base/src/main/java/com/weatheredwells/neoforge/
neoforge-1.21.1/src/main/java/com/weatheredwells/neoforge/
neoforge-1.21.1/src/main/resources/
```

### 1.3 Module Build Files

- `common-1.21.1/build.gradle`: Architectury common config, include `common-shared` as srcDir, access widener, mixin config
- `fabric-1.21.1/build.gradle`: Fabric platform setup, shadow common module, remapJar with version suffix
- `neoforge-1.21.1/build.gradle`: NeoForge platform setup, shadow common module, remapJar with version suffix
- `fabric-base/build.gradle`, `neoforge-base/build.gradle`: Minimal placeholder configs

### 1.4 Entrypoints and Mod Metadata

- `common-shared`: `WeatheredWells.java` — main mod class with `MOD_ID = "weatheredwells"`, `init()` method calling registry initialization
- `fabric-1.21.1`: `WeatheredWellsFabric.java` implementing `ModInitializer`, `fabric.mod.json`
- `neoforge-1.21.1`: `WeatheredWellsNeoForge.java` with `@Mod` annotation, `neoforge.mods.toml`
- Mixin config: `weatheredwells.mixins.json`
- Access widener: `weatheredwells.accesswidener`

### 1.5 Verification

- Confirm `./gradlew build` succeeds for both Fabric and NeoForge
- Confirm the mod loads in a dev environment (runClient)

---

## Phase 2: Custom Items (Totems)

### 2.1 Item Registration

In `common-shared/registry/ModItems.java`, register three totem items using Architectury's `DeferredRegister`:

| Item | ID |
|------|----|
| Soaked Totem | `weatheredwells:soaked_totem` |
| Clear Totem | `weatheredwells:clear_totem` |
| Deep Totem | `weatheredwells:deep_totem` |

- Items should be non-stackable (max stack size 1) or have a small stack size, since they are collectible progression items.
- Register a creative tab (`weatheredwells:weatheredwells_tab`) containing all totems.

### 2.2 Item Assets

- Textures: `assets/weatheredwells/textures/item/soaked_totem.png`, `clear_totem.png`, `deep_totem.png`
- Item models: `assets/weatheredwells/models/item/soaked_totem.json`, etc.
- Language files: `assets/weatheredwells/lang/en_us.json`, `ja_jp.json`

---

## Phase 3: Structures (Wells)

### 3.1 NBT Structure Files

Create NBT structure files for each well type. Each well consists of two parts connected via Jigsaw:

| Well Type | Above Ground NBT | Underground NBT |
|-----------|-------------------|-----------------|
| Forgotten Well | `forgotten_well_top` | `forgotten_well_bottom` |
| Mossy Well | `mossy_well_top` | `mossy_well_bottom` |
| Dry Well | `dry_well_top` | `dry_well_bottom` |
| Clear Well | `clear_well_top` | `clear_well_bottom` |
| Sunken Well | `sunken_well_top` | `sunken_well_bottom` |

- Build each structure in-game using Structure Blocks, then export as NBT files.
- Place Jigsaw blocks to connect above-ground and underground parts.
- Each underground part contains a chest with a loot table reference.

### 3.2 Worldgen Configuration

In `data/weatheredwells/`:

- **Structure definitions** (`worldgen/structure/`): Define each well as a Jigsaw structure.
- **Template pools** (`worldgen/template_pool/`): Define pools for above-ground and underground parts.
- **Structure sets** (`worldgen/structure_set/`): Configure spacing (300+ blocks between wells) and separation. All well types can be in a single structure set with random selection, or separate sets with coordinated exclusion zones.
- **Biome tags** (`tags/worldgen/biome/`): Define which biomes wells can spawn in (e.g., plains, forests, swamps — avoid ocean/nether/end).

### 3.3 Loot Tables

In `data/weatheredwells/loot_table/`:

- `well_chest.json`: A single loot table used by all well chests. Randomly selects one totem from the three types with equal probability. Each chest contains exactly one totem.

---

## Phase 4: Advancements (Progress)

### 4.1 Advancement Definitions

In `data/weatheredwells/advancement/`:

| Advancement | ID | Trigger |
|-------------|----|---------|
| Water Seeps In | `weatheredwells:water_seeps_in` | Obtain `weatheredwells:soaked_totem` (inventory_changed) |
| Clarity Settles | `weatheredwells:clarity_settles` | Obtain `weatheredwells:clear_totem` (inventory_changed) |
| Depths Endured | `weatheredwells:depths_endured` | Obtain `weatheredwells:deep_totem` (inventory_changed) |
| At Home in Water | `weatheredwells:at_home_in_water` | All three above advancements completed |

- "At Home in Water" uses a parent/requirement structure or a custom criterion that checks all three totems have been obtained.
- Advancements should have display icons (corresponding totem items), titles, and descriptions in both English and Japanese.

### 4.2 Buff Grant on Advancement

When an advancement is granted, the corresponding buff level must be applied. This requires event handling (not just data-driven advancements):

- Listen for advancement grant events (via Architectury's platform-abstracted event or Mixin).
- On grant of `water_seeps_in` / `clarity_settles` / `depths_endured`: grant or upgrade "Blessing of the Waterways: Lingering" (level I → II → III based on count of these three).
- On grant of `at_home_in_water`: grant "Blessing of the Waterways: Attunement I".

---

## Phase 5: Buff System

### 5.1 Custom Effects

Register two custom mob effects using Architectury's `DeferredRegister`:

| Effect | ID | Behavior |
|--------|----|----------|
| Blessing of the Waterways: Lingering | `weatheredwells:waterways_lingering` | Heal when in water after delay |
| Blessing of the Waterways: Attunement | `weatheredwells:waterways_attunement` | Reduce delay before healing starts |

### 5.2 Permanent Buff Storage

Since effects must persist across death and relog, use **player capability/attachment data** (not vanilla potion effects, which expire):

- Store granted buff levels per player in persistent data (saved with the world).
- On player tick, check if the player has the buff and apply the healing logic directly.
- Use `SavedData` (or equivalent per-player persistent storage) to track which buffs each player has earned.

### 5.3 Water Contact Healing Logic

Implement in a player tick event handler:

1. Check if the player has the Lingering buff.
2. Check if the player is in water (`player.isInWater()`).
3. Track consecutive water contact time per player (reset when leaving water).
4. Determine activation delay:
   - Default: 5 seconds (100 ticks)
   - With Attunement: 2 seconds (40 ticks)
5. Once delay is met, heal 0.5 hearts (1.0 health) at the interval determined by Lingering level:
   - Level I: every 3 seconds (60 ticks)
   - Level II: every 2.5 seconds (50 ticks)
   - Level III: every 2 seconds (40 ticks)

### 5.4 Buff Persistence Across Sessions

- Save buff data to world/player data on server stop and player logout.
- Load buff data on server start and player login.
- Re-apply tracking state (water contact timer resets on login — this is acceptable).

---

## Phase 6: Mod Removal Safety

Per the spec, the mod should aim to continue working after removal:

- Totems are regular items — they will become "unknown items" after mod removal but won't corrupt the world.
- Structures are placed as vanilla blocks — they remain in the world.
- Advancements will be silently ignored.
- Buffs stored in custom data will be ignored (healing stops, no crash).
- No custom blocks or entities that would cause chunk errors.

No special implementation needed beyond following standard practices (avoid custom block entities, custom world data that causes errors on missing mod).

---

## Phase 7: Assets and Polish

### 7.1 Textures

- Create pixel art textures for the three totems (16x16).
- Optionally create a mod icon for mod loader listings.

### 7.2 Localization

- `en_us.json`: English translations for items, effects, advancements, creative tab.
- `ja_jp.json`: Japanese translations.

### 7.3 Descriptions

- `README.md`: Project description (use the GitHub introduction from the spec).
- CurseForge / Modrinth descriptions (from the spec).

---

## Phase 8: Testing and Release

### 8.1 In-Game Testing

- Verify wells generate in the world with correct spacing.
- Verify chests contain exactly one random totem.
- Verify advancements trigger on totem pickup.
- Verify buff levels increase correctly with each advancement.
- Verify water healing mechanics (delay, interval, heal amount).
- Verify Attunement reduces delay.
- Verify buffs persist across death, relog, and server restart.
- Test on both Fabric and NeoForge.

### 8.2 Mod Removal Testing

- Place totems and earn buffs, then remove the mod.
- Verify the world loads without errors.

### 8.3 Build and Release

- Run `./gradlew buildAll` to produce JARs for all platforms.
- Output naming: `weatheredwells-{version}+1.21.1-fabric.jar`, `weatheredwells-{version}+1.21.1-neoforge.jar`
- Set license to LGPL-3.0-only in mod metadata and repository.

---

## Implementation Order Summary

| Order | Phase | Dependencies |
|-------|-------|--------------|
| 1 | Project Scaffolding | None |
| 2 | Custom Items (Totems) | Phase 1 |
| 3 | Structures (Wells) | Phase 1 |
| 4 | Advancements | Phase 2 |
| 5 | Buff System | Phase 4 |
| 6 | Mod Removal Safety | Phase 5 (verification) |
| 7 | Assets and Polish | Phase 2, 3 |
| 8 | Testing and Release | All |
