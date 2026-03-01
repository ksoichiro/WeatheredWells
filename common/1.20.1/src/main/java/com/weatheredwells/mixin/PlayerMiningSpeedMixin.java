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

import com.weatheredwells.registry.ModEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Cancels the underwater mining speed penalty for players with the Immersion effect.
 * In 1.20.1, SUBMERGED_MINING_SPEED attribute doesn't exist, so we use a mixin.
 * Underwater mining without Aqua Affinity is 5x slower; this mixin multiplies
 * the result by 5.0 to cancel that penalty.
 */
@Mixin(Player.class)
public abstract class PlayerMiningSpeedMixin {

    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    private void weatheredwells$boostUnderwaterMining(BlockState state, CallbackInfoReturnable<Float> cir) {
        Player player = (Player) (Object) this;
        if (player.isEyeInFluid(net.minecraft.tags.FluidTags.WATER)
                && !EnchantmentHelper.hasAquaAffinity(player)
                && player.hasEffect(ModEffects.WATERWAYS_IMMERSION.get())) {
            cir.setReturnValue(cir.getReturnValue() * 5.0f);
        }
    }
}
