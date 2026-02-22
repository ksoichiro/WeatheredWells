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
import com.weatheredwells.effects.WaterwaysLingeringEffect;
import com.weatheredwells.effects.WaterwaysAttunementEffect;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(WeatheredWells.MOD_ID, Registries.MOB_EFFECT);

    public static final RegistrySupplier<MobEffect> WATERWAYS_LINGERING =
            EFFECTS.register("waterways_lingering", WaterwaysLingeringEffect::new);

    public static final RegistrySupplier<MobEffect> WATERWAYS_ATTUNEMENT =
            EFFECTS.register("waterways_attunement", WaterwaysAttunementEffect::new);

    public static void register() {
        EFFECTS.register();
        WeatheredWells.LOGGER.debug("Registered ModEffects");
    }
}
