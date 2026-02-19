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

import com.mojang.serialization.MapCodec;
import com.weatheredwells.WeatheredWells;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Structure processor that tracks intentional waterlogging from NBT and
 * removes all waterlogging during placement. The StructureStartMixin
 * restores intentional waterlogging after placement and removes
 * unintentional waterlogging caused by adjacent water (Aquifer system).
 */
public class WaterlogRemovalProcessor extends StructureProcessor {
    public static final WaterlogRemovalProcessor INSTANCE = new WaterlogRemovalProcessor();
    public static final MapCodec<WaterlogRemovalProcessor> CODEC = MapCodec.unit(INSTANCE);

    public static StructureProcessorType<WaterlogRemovalProcessor> TYPE;

    /**
     * Thread-safe set to track blocks that have intentional waterlogging (from NBT).
     * These positions will be restored by the StructureStartMixin after placement.
     */
    public static final Set<BlockPos> INTENTIONAL_WATERLOGGING =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

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

        if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
            BlockState originalState = originalInfo.state();
            boolean originalWaterlogged = originalState.hasProperty(BlockStateProperties.WATERLOGGED)
                    && originalState.getValue(BlockStateProperties.WATERLOGGED);

            if (originalWaterlogged) {
                // Record intentional waterlogging for restoration by Mixin
                INTENTIONAL_WATERLOGGING.add(modifiedInfo.pos().immutable());
                WeatheredWells.LOGGER.debug("Recorded intentional waterlogging at {}", modifiedInfo.pos());
            }

            // Remove all waterlogging; Mixin will restore intentional ones after placement
            boolean currentWaterlogged = state.getValue(BlockStateProperties.WATERLOGGED);
            if (currentWaterlogged) {
                BlockState newState = state.setValue(BlockStateProperties.WATERLOGGED, false);
                return new StructureTemplate.StructureBlockInfo(
                        modifiedInfo.pos(), newState, modifiedInfo.nbt());
            }
        }

        return modifiedInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TYPE;
    }
}
