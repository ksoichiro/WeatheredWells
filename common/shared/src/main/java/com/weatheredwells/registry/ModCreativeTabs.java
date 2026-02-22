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
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(WeatheredWells.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> WEATHERED_WELLS_TAB = TABS.register(
            "weatheredwells",
            () -> CreativeTabRegistry.create(builder ->
                    builder.title(Component.translatable("itemGroup.weatheredwells.weatheredwells"))
                            .icon(() -> new ItemStack(ModItems.SOAKED_TOTEM.get()))
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.SOAKED_TOTEM.get());
                                output.accept(ModItems.CLEAR_TOTEM.get());
                                output.accept(ModItems.DEEP_TOTEM.get());
                            })
            )
    );

    public static void register() {
        TABS.register();
        WeatheredWells.LOGGER.debug("Registered ModCreativeTabs");
    }
}
