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

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Blessing of the Waterways: Immersion effect.
 * Display-only; water breathing handled by WaterHealingHandler,
 * movement/mining speed handled by mixins (1.20.1 lacks water-specific attributes).
 */
public class WaterwaysImmersionEffect extends MobEffect {
    public WaterwaysImmersionEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x0077BE); // Deep ocean blue
    }
}
