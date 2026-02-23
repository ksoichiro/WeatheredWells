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

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public class ModItems {
    public static Supplier<Item> SOAKED_TOTEM;
    public static Supplier<Item> CLEAR_TOTEM;
    public static Supplier<Item> DEEP_TOTEM;

    public static Item createSoakedTotem() {
        return new Item(ItemHelper.properties("soaked_totem").rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public static Item createClearTotem() {
        return new Item(ItemHelper.properties("clear_totem").rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public static Item createDeepTotem() {
        return new Item(ItemHelper.properties("deep_totem").rarity(Rarity.UNCOMMON).stacksTo(1));
    }
}
