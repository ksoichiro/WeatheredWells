# Weathered Wells - Development Guide

## Project Structure

Multi-loader Minecraft mod using Architectury:
- `common-shared/` - Version-agnostic shared code (registries)
- `common-{version}/` - Version-specific common code (effects, events, mixins)
- `fabric-{version}/` - Fabric platform implementation
- `neoforge-{version}/` - NeoForge platform implementation (1.21+)
- `forge-{version}/` - Forge platform implementation (1.20.x)
- `fabric-base/`, `neoforge-base/`, `forge-base/` - Base platform configs
- `props/` - Version-specific properties files

Supported versions:
- 1.21.5: Fabric, NeoForge
- 1.21.4: Fabric, NeoForge
- 1.21.3: Fabric, NeoForge
- 1.21.1: Fabric, NeoForge
- 1.20.1: Fabric, Forge

## Build Commands

```bash
# Full build for default version (1.21.5)
./gradlew build

# Build for specific version
./gradlew build -Ptarget_mc_version=1.20.1
./gradlew build -Ptarget_mc_version=1.21.1
./gradlew build -Ptarget_mc_version=1.21.3
./gradlew build -Ptarget_mc_version=1.21.4
./gradlew build -Ptarget_mc_version=1.21.5

# Run client for testing
./gradlew fabric:runClient -Ptarget_mc_version=1.20.1
./gradlew forge:runClient -Ptarget_mc_version=1.20.1
./gradlew fabric:runClient -Ptarget_mc_version=1.21.1
./gradlew neoforge:runClient -Ptarget_mc_version=1.21.1
./gradlew fabric:runClient -Ptarget_mc_version=1.21.3
./gradlew neoforge:runClient -Ptarget_mc_version=1.21.3
./gradlew fabric:runClient -Ptarget_mc_version=1.21.4
./gradlew neoforge:runClient -Ptarget_mc_version=1.21.4
./gradlew fabric:runClient -Ptarget_mc_version=1.21.5
./gradlew neoforge:runClient -Ptarget_mc_version=1.21.5

# Clean build
./gradlew clean build -Ptarget_mc_version=1.20.1
```

## Key Files

- `gradle.properties` - Mod version, target MC version
- `props/1.21.5.properties` - Version-specific dependencies (1.21.5)
- `props/1.21.4.properties` - Version-specific dependencies (1.21.4)
- `props/1.21.3.properties` - Version-specific dependencies (1.21.3)
- `props/1.21.1.properties` - Version-specific dependencies (1.21.1)
- `common-shared/.../WeatheredWells.java` - Main mod class
- `common-{version}/.../events/WeatheredWellsEvents.java` - Event handlers
- `common-{version}/.../mixin/` - Mixin implementations

## Registries (common-shared)

- `ModItems` - Totem items
- `ModEffects` - Custom mob effects
- `ModParticles` - Particle types
- `ModProcessors` - Structure processors
- `ModCreativeTabs` - Creative mode tab

## Development Notes

- Use Architectury API for cross-loader compatibility
- Platform-specific code goes in `fabric-*/`, `neoforge-*/`, or `forge-*/`
- Version-specific common code goes in `common-{version}/`
- Structures use Jigsaw system with template pools
- Player buff data persists via SavedData (world-level storage)

### Version-specific API differences

**1.21.5 vs 1.21.4:**
- SavedData: 1.21.5 uses Codec-based `SavedDataType<T>` instead of `SavedData.Factory<T>` with manual NBT save/load
- CompoundTag: `getCompound()`, `getInt()`, `getBoolean()` return `Optional`; use `getCompoundOrEmpty()`, `getIntOr()`, `getBooleanOr()` for non-optional access; `getAllKeys()` renamed to `keySet()`
- Advancement background: 1.21.5 uses `gui/advancements/backgrounds/<name>` resource location instead of direct texture path
- pack_format: 1.21.5 uses 71, 1.21.4 uses 61

**1.21.4 vs 1.21.3:**
- Client Items: 1.21.4 requires item model definitions in `assets/<namespace>/items/<path>.json` (Client Items system)
- pack_format: 1.21.4 uses 61, 1.21.3 uses 57
- Java API: No breaking changes for this mod's usage

**1.21.3 vs 1.21.1:**
- RegistryAccess: 1.21.3 uses `lookupOrThrow()`, 1.21.1 uses `registryOrThrow()`
- Item.Properties: 1.21.3 requires `setId(ResourceKey)` before Item construction (handled by `ItemHelper`)
- pack_format: 1.21.3 uses 57, 1.21.1 uses 48

**1.21.1 vs 1.20.1:**
- SavedData: 1.21 uses `HolderLookup.Provider` parameter, 1.20.1 does not
- ResourceLocation: 1.21 uses `fromNamespaceAndPath()`, 1.20.1 uses constructor
- Advancement: 1.21 uses `AdvancementHolder`, 1.20.1 uses `Advancement` directly
- MobEffect: 1.21 uses `Holder<MobEffect>`, 1.20.1 uses `MobEffect` directly
- Codec: 1.21 uses `MapCodec`, 1.20.1 uses `Codec`

### Platform gradle.properties

Platform-specific subprojects require `loom.platform` in their `gradle.properties`:
- `neoforge-{version}/gradle.properties`: `loom.platform=neoforge`
- `forge-{version}/gradle.properties`: `loom.platform=forge`

This is required because Architectury Loom defaults to Fabric mode.

## Resources Location

- Assets: `common-{version}/src/main/resources/assets/weatheredwells/`
- Data (loot tables, structures, etc.): `common-{version}/src/main/resources/data/weatheredwells/`
- Mixin config: `common-{version}/src/main/resources/weatheredwells.mixins.json`
