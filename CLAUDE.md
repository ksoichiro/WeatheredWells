# Weathered Wells - Development Guide

## Project Structure

Multi-loader Minecraft mod using Architectury:
- `common-shared/` - Version-agnostic shared code (registries)
- `common-1.21.1/` - Version-specific common code (effects, events, mixins)
- `fabric-1.21.1/` - Fabric platform implementation
- `neoforge-1.21.1/` - NeoForge platform implementation
- `fabric-base/`, `neoforge-base/` - Base platform configs
- `props/` - Version-specific properties files

## Build Commands

```bash
# Full build (both platforms)
./gradlew build

# Single platform
./gradlew fabric-1.21.1:build
./gradlew neoforge-1.21.1:build

# Run client for testing
./gradlew fabric-1.21.1:runClient
./gradlew neoforge-1.21.1:runClient

# Clean build
./gradlew clean build
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
- Platform-specific code goes in `fabric-*/` or `neoforge-*/`
- Version-specific common code goes in `common-{version}/`
- Structures use Jigsaw system with template pools
- Player buff data persists via player data attachments

## Resources Location

- Assets: `common-1.21.1/src/main/resources/assets/weatheredwells/`
- Data (loot tables, structures, etc.): `common-1.21.1/src/main/resources/data/weatheredwells/`
- Mixin config: `common-1.21.1/src/main/resources/weatheredwells.mixins.json`
