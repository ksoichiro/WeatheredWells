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
- 1.21.1: Fabric, NeoForge
- 1.20.1: Fabric, Forge

## Build Commands

```bash
# Full build for default version (1.21.1)
./gradlew build

# Build for specific version
./gradlew build -Ptarget_mc_version=1.20.1
./gradlew build -Ptarget_mc_version=1.21.1

# Run client for testing
./gradlew fabric:runClient -Ptarget_mc_version=1.20.1
./gradlew forge:runClient -Ptarget_mc_version=1.20.1
./gradlew fabric:runClient -Ptarget_mc_version=1.21.1
./gradlew neoforge:runClient -Ptarget_mc_version=1.21.1

# Clean build
./gradlew clean build -Ptarget_mc_version=1.20.1
```

## Key Files

- `gradle.properties` - Mod version, target MC version
- `props/1.21.1.properties` - Version-specific dependencies
- `common-shared/.../WeatheredWells.java` - Main mod class
- `common-1.21.1/.../events/WeatheredWellsEvents.java` - Event handlers
- `common-1.21.1/.../mixin/` - Mixin implementations

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

**1.21.1 vs 1.20.1:**
- SavedData: 1.21 uses `HolderLookup.Provider` parameter, 1.20.1 does not
- ResourceLocation: 1.21 uses `fromNamespaceAndPath()`, 1.20.1 uses constructor
- Advancement: 1.21 uses `AdvancementHolder`, 1.20.1 uses `Advancement` directly
- MobEffect: 1.21 uses `Holder<MobEffect>`, 1.20.1 uses `MobEffect` directly
- Codec: 1.21 uses `MapCodec`, 1.20.1 uses `Codec`

### Forge 1.20.1 with Architectury Loom

When using Architectury Loom 1.11+ with Forge 1.20.1, you must add `loom.platform = forge` to `forge-{version}/gradle.properties`. This is required because Loom defaults to Fabric mode.

## Resources Location

- Assets: `common-1.21.1/src/main/resources/assets/weatheredwells/`
- Data (loot tables, structures, etc.): `common-1.21.1/src/main/resources/data/weatheredwells/`
- Mixin config: `common-1.21.1/src/main/resources/weatheredwells.mixins.json`
