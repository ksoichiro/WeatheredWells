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
package com.weatheredwells.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Blessing of the Waterways: Immersion effect.
 * Grants water breathing (via WaterHealingHandler tick), increased underwater
 * movement speed, and increased underwater mining speed via attribute modifiers.
 */
public class WaterwaysImmersionEffect extends MobEffect {
    public WaterwaysImmersionEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x0077BE); // Deep ocean blue
        addAttributeModifier(
                Attributes.WATER_MOVEMENT_EFFICIENCY,
                ResourceLocation.fromNamespaceAndPath("weatheredwells", "waterways_immersion.water_movement_efficiency"),
                1.0,
                AttributeModifier.Operation.ADD_VALUE
        );
        addAttributeModifier(
                Attributes.SUBMERGED_MINING_SPEED,
                ResourceLocation.fromNamespaceAndPath("weatheredwells", "waterways_immersion.submerged_mining_speed"),
                0.8,
                AttributeModifier.Operation.ADD_VALUE
        );
    }
}
