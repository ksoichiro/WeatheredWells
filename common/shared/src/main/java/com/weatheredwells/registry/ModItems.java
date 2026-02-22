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
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(WeatheredWells.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> SOAKED_TOTEM = ITEMS.register("soaked_totem",
            () -> new Item(ItemHelper.properties("soaked_totem").rarity(Rarity.UNCOMMON).stacksTo(1)));

    public static final RegistrySupplier<Item> CLEAR_TOTEM = ITEMS.register("clear_totem",
            () -> new Item(ItemHelper.properties("clear_totem").rarity(Rarity.UNCOMMON).stacksTo(1)));

    public static final RegistrySupplier<Item> DEEP_TOTEM = ITEMS.register("deep_totem",
            () -> new Item(ItemHelper.properties("deep_totem").rarity(Rarity.UNCOMMON).stacksTo(1)));

    public static void register() {
        ITEMS.register();
        WeatheredWells.LOGGER.debug("Registered ModItems");
    }
}
