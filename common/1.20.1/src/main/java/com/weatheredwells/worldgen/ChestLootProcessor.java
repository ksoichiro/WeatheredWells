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
package com.weatheredwells.worldgen;

import com.mojang.serialization.Codec;
import com.weatheredwells.WeatheredWells;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

/**
 * Structure processor that automatically assigns the well loot table
 * to any chest block placed by the structure.
 */
public class ChestLootProcessor extends StructureProcessor {
    public static final ChestLootProcessor INSTANCE = new ChestLootProcessor();
    public static final Codec<ChestLootProcessor> CODEC = Codec.unit(INSTANCE);

    public static StructureProcessorType<ChestLootProcessor> TYPE;

    private static final ResourceLocation LOOT_TABLE =
            new ResourceLocation(WeatheredWells.MOD_ID, "chests/well_chest");

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
            LevelReader level,
            BlockPos pos,
            BlockPos pivot,
            StructureTemplate.StructureBlockInfo originalInfo,
            StructureTemplate.StructureBlockInfo modifiedInfo,
            StructurePlaceSettings settings) {

        BlockState state = modifiedInfo.state();
        if (state.is(Blocks.CHEST)) {
            CompoundTag nbt = modifiedInfo.nbt() != null ? modifiedInfo.nbt().copy() : new CompoundTag();
            nbt.putString("LootTable", LOOT_TABLE.toString());
            nbt.putLong("LootTableSeed", settings.getRandom(modifiedInfo.pos()).nextLong());
            return new StructureTemplate.StructureBlockInfo(modifiedInfo.pos(), state, nbt);
        }

        return modifiedInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TYPE;
    }
}
