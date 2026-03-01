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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Increases underwater movement speed for entities with the Immersion effect.
 * In 1.20.1, WATER_MOVEMENT_EFFICIENCY attribute doesn't exist, so we use a mixin.
 * The default swim speed factor is 0.02; we increase it to 0.06 to roughly
 * triple the underwater movement speed.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntitySwimMixin {

    @ModifyVariable(method = "travel", at = @At("STORE"), ordinal = 0)
    private float weatheredwells$boostSwimSpeed(float swimSpeed) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.isInWater() && self.hasEffect(ModEffects.WATERWAYS_IMMERSION.get())) {
            return swimSpeed * 3.0f;
        }
        return swimSpeed;
    }
}
