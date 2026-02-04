# Weathered Wells

## Overview

- Multiple types of wells are scattered throughout the world.
- Travel between wells and collect custom items called totems.
- Wells are spaced apart at a certain distance to avoid being too close to each other (300+ blocks).
- There is no correlation between well types and totem types, and each well has a single chest containing one randomly placed totem.
- Achievements are unlocked when obtaining new types of totems and when collecting all types.
- Upon achieving progress, health regenerates when the user is in contact with water.
- However, since water can be easily carried with water buckets and could become a powerful cheat item, regeneration begins gradually after a certain number of seconds of contact with water.
- As the number of achievements increases, the regeneration speed increases. When all totems are obtained, the time before regeneration starts is shortened. These are granted as permanent buffs.

## Structure Types

Each structure (well) consists of two NBT structures as its basic configuration: an above-ground part and an underground part, connected using Jigsaw.

- EN: Forgotten Well / JA: 朽ちた井戸
	- ID: weatheredwells:forgotten_well
		- Above ground: weatheredwells:forgotten_well_top
		- Underground: weatheredwells:forgotten_well_bottom
- EN: Mossy Well / JA: 苔むした井戸
		- Above ground: weatheredwells:mossy_well_top
		- Underground: weatheredwells:mossy_well_bottom
- EN: Dry Well / JA: 乾いた井戸
		- Above ground: weatheredwells:dry_well_top
		- Underground: weatheredwells:dry_well_bottom
- EN: Clear Well / JA: 澄んだ井戸
		- Above ground: weatheredwells:clear_well_top
		- Underground: weatheredwells:clear_well_bottom
- EN: Sunken Well / JA: 沈んだ井戸
		- Above ground: weatheredwells:sunken_well_top
		- Underground: weatheredwells:sunken_well_bottom

## Types of Items (Totems)

- EN: Soaked Totem / JA: 湿ったトーテム
	- ID: weatheredwells:soaked_totem
- EN: Clear Totem / JA: 澄んだトーテム
	- ID: weatheredwells:clear_totem
- EN: Deep Totem / JA: 深層のトーテム
	- ID: weatheredwells:deep_totem

## Types of Progress

- EN: Water Seeps In / JA: 水が馴染む
	- ID: weatheredwells:water_seeps_in
	- Achievement condition: Obtain soaked totem
- EN: Clarity Settles / JA: 澄み渡る感覚
	- ID: weatheredwells:clarity_settles
	- Achievement condition: Obtain clear totem
- EN: Depths Endured / JA: 深みに適応する
	- ID: weatheredwells:depths_endured
	- Achievement condition: Acquire deep totem
- EN: At Home in Water / JA: 水と共にある
	- ID: weatheredwells:at_home_in_water
	- Achievement condition: When the above three progress milestones are achieved

## Buff Types

1. Health gradually recovers after touching water for a certain period of time
	- Name:
		- JA: 水脈の加護：滞留 I - III
		- EN: Blessing of the Waterways: Lingering I - III
	- ID:
		- weatheredwells:waterways_lingering
	- Duration: Infinite (permanent buff per player)
	- Effect:
		- When in contact with water (body is inside water blocks), after 5 seconds have passed, health recovers by 0.5 hearts every 3 seconds
		- Recovery speed increases as level goes up
			- "Blessing of the Waterways: Lingering I": every 3 seconds
			- "Blessing of the Waterways: Lingering II": every 2.5 seconds
			- "Blessing of the Waterways: Lingering III": every 2 seconds

2. Reducing the time from water contact to recovery initiation
	- Name:
		- JA: 水脈の加護：同化 I
		- EN: Blessing of the Waterways: Attunement I
	- ID:
		- weatheredwells:waterways_attunement
	- Duration: Infinite (permanent buff per player)
	- Effect:
		- Recovery activation time from "Blessing of the Waterways: Lingering" is reduced to 2 seconds

## User Introduction

For GitHub:

> A small exploration mod where collecting totems from weathered wells grants gentle healing effects near water.

For CurseForge:

> Explore weathered wells scattered across the world and collect totems to gain subtle healing effects when touching water.

For Modrinth:

> Journey between weathered wells and uncover totems that let your body quietly attune to water.

## Architecture
- Minecraft mod using Architectury
- Compatible with both Fabric and NeoForge
- Initially implemented for Minecraft version 1.21.1
- Aims to continue working after mod removal
    - Accepts that some features like spawn point fixing will not function when the mod is removed
- The project will be configured with Gradle as a multi-project setup

## Directory

- common-shared
    - Common code without loader dependencies or version dependencies. Not a Gradle subproject, but incorporated as one of the srcDirs from each version-specific subproject
- common-1.21.1
    - Common code for Minecraft 1.21.1 without loader dependencies. Gradle subproject.
- fabric-base
    - Code for Fabric without Minecraft version dependencies. Gradle subproject.
- fabric-1.21.1
    - Code for Fabric and Minecraft 1.21.1. Gradle subproject. Depends on fabric-base.
- neoforge-base
    - Code for NeoForge without Minecraft version dependencies. Gradle subproject.
- neoforge-1.21.1
    - Code for NeoForge and Minecraft 1.21.1. Gradle subproject. Depends on neoforge-base.

## Misc

- License is LGPL-3.0-only
