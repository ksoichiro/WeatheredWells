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
package com.weatheredwells.mixin;

import com.weatheredwells.WeatheredWells;
import com.weatheredwells.worldgen.WaterlogRemovalProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Mixin to prevent unintentional waterlogging in well structure generation.
 *
 * Strategy:
 * 1. Before placement (HEAD): Remove water blocks within structure bounds
 *    to prevent waterloggable blocks from becoming waterlogged during placement.
 * 2. After placement (RETURN): Restore intentional waterlogging (recorded by
 *    WaterlogRemovalProcessor) and remove any unintentional waterlogging.
 */
@Mixin(StructureStart.class)
public abstract class StructureStartMixin {

    @Shadow
    private Structure structure;

    @Shadow
    public abstract List<StructurePiece> getPieces();

    @Inject(method = "placeInChunk", at = @At("HEAD"))
    private void removeWaterBeforePlacement(
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BoundingBox chunkBox,
            ChunkPos chunkPos,
            CallbackInfo ci) {

        if (!isWeatheredWellsStructure(level)) {
            return;
        }

        for (StructurePiece piece : getPieces()) {
            BoundingBox pieceBox = piece.getBoundingBox();
            if (!pieceBox.intersects(chunkBox)) {
                continue;
            }

            int minX = Math.max(pieceBox.minX(), chunkBox.minX());
            int minY = Math.max(pieceBox.minY(), chunkBox.minY());
            int minZ = Math.max(pieceBox.minZ(), chunkBox.minZ());
            int maxX = Math.min(pieceBox.maxX(), chunkBox.maxX());
            int maxY = Math.min(pieceBox.maxY(), chunkBox.maxY());
            int maxZ = Math.min(pieceBox.maxZ(), chunkBox.maxZ());

            for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
                BlockState state = level.getBlockState(pos);
                if (state.getFluidState().isSource() && state.getBlock() == Blocks.WATER) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
    }

    @Inject(method = "placeInChunk", at = @At("RETURN"))
    private void fixWaterloggingAfterPlacement(
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BoundingBox chunkBox,
            ChunkPos chunkPos,
            CallbackInfo ci) {

        if (!isWeatheredWellsStructure(level)) {
            WaterlogRemovalProcessor.INTENTIONAL_WATERLOGGING.clear();
            return;
        }

        for (StructurePiece piece : getPieces()) {
            BoundingBox pieceBox = piece.getBoundingBox();
            if (!pieceBox.intersects(chunkBox)) {
                continue;
            }

            // Expand by 1 block in X/Z to catch waterlogging from adjacent chunks
            int minX = Math.max(pieceBox.minX(), chunkBox.minX() - 1);
            int minY = Math.max(pieceBox.minY(), chunkBox.minY());
            int minZ = Math.max(pieceBox.minZ(), chunkBox.minZ() - 1);
            int maxX = Math.min(pieceBox.maxX(), chunkBox.maxX() + 1);
            int maxY = Math.min(pieceBox.maxY(), chunkBox.maxY());
            int maxZ = Math.min(pieceBox.maxZ(), chunkBox.maxZ() + 1);

            for (BlockPos pos : BlockPos.betweenClosed(minX, minY, minZ, maxX, maxY, maxZ)) {
                BlockState state = level.getBlockState(pos);

                if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
                    boolean currentWaterlogged = state.getValue(BlockStateProperties.WATERLOGGED);
                    BlockPos immutablePos = pos.immutable();
                    boolean shouldBeWaterlogged =
                            WaterlogRemovalProcessor.INTENTIONAL_WATERLOGGING.contains(immutablePos);

                    if (currentWaterlogged != shouldBeWaterlogged) {
                        BlockState newState = state.setValue(
                                BlockStateProperties.WATERLOGGED, shouldBeWaterlogged);
                        level.setBlock(pos, newState, 2);
                    }

                    if (shouldBeWaterlogged) {
                        WaterlogRemovalProcessor.INTENTIONAL_WATERLOGGING.remove(immutablePos);
                    }
                }
            }
        }

        // Safety cleanup
        if (WaterlogRemovalProcessor.INTENTIONAL_WATERLOGGING.size() > 10000) {
            WeatheredWells.LOGGER.warn(
                    "INTENTIONAL_WATERLOGGING set is too large ({}), clearing",
                    WaterlogRemovalProcessor.INTENTIONAL_WATERLOGGING.size());
            WaterlogRemovalProcessor.INTENTIONAL_WATERLOGGING.clear();
        }
    }

    private boolean isWeatheredWellsStructure(WorldGenLevel level) {
        ResourceLocation loc = level.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getKey(structure);
        return loc != null && loc.getNamespace().equals(WeatheredWells.MOD_ID);
    }
}
