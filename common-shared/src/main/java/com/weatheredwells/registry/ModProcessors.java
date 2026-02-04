/*
 * Copyright (C) 2025 ksoichiro
 *
 * This file is part of Weathered Wells.
 *
 * Weathered Wells is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * Weathered Wells is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Weathered Wells. If not, see <https://www.gnu.org/licenses/>.
 */
package com.weatheredwells.registry;

import com.weatheredwells.WeatheredWells;
import com.weatheredwells.worldgen.ChestLootProcessor;
import com.weatheredwells.worldgen.WaterlogRemovalProcessor;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class ModProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSOR_TYPES =
            DeferredRegister.create(WeatheredWells.MOD_ID, Registries.STRUCTURE_PROCESSOR);

    public static final RegistrySupplier<StructureProcessorType<ChestLootProcessor>> CHEST_LOOT =
            PROCESSOR_TYPES.register("chest_loot",
                    () -> () -> ChestLootProcessor.CODEC);

    public static final RegistrySupplier<StructureProcessorType<WaterlogRemovalProcessor>> WATERLOG_REMOVAL =
            PROCESSOR_TYPES.register("waterlog_removal",
                    () -> () -> WaterlogRemovalProcessor.CODEC);

    public static void register() {
        PROCESSOR_TYPES.register();
        WeatheredWells.LOGGER.debug("Registered ModProcessors");
    }

    public static void init() {
        // Called after registration to wire the TYPE fields
        ChestLootProcessor.TYPE = CHEST_LOOT.get();
        WaterlogRemovalProcessor.TYPE = WATERLOG_REMOVAL.get();
    }
}
