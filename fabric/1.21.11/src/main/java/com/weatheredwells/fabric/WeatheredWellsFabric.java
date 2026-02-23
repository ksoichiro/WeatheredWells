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
package com.weatheredwells.fabric;

import com.weatheredwells.WeatheredWells;
import com.weatheredwells.effects.WaterHealingHandler;
import com.weatheredwells.events.WeatheredWellsEvents;
import com.weatheredwells.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import java.util.function.Supplier;

public class WeatheredWellsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // Register items
        ModItems.SOAKED_TOTEM = register(BuiltInRegistries.ITEM, "soaked_totem", ModItems.createSoakedTotem());
        ModItems.CLEAR_TOTEM = register(BuiltInRegistries.ITEM, "clear_totem", ModItems.createClearTotem());
        ModItems.DEEP_TOTEM = register(BuiltInRegistries.ITEM, "deep_totem", ModItems.createDeepTotem());

        // Register effects
        ModEffects.WATERWAYS_LINGERING = register(BuiltInRegistries.MOB_EFFECT, "waterways_lingering", ModEffects.createWaterwaysLingering());
        ModEffects.WATERWAYS_ATTUNEMENT = register(BuiltInRegistries.MOB_EFFECT, "waterways_attunement", ModEffects.createWaterwaysAttunement());

        // Register particles
        ModParticles.WATER_HEALING = register(BuiltInRegistries.PARTICLE_TYPE, "water_healing", ModParticles.createWaterHealing());

        // Register processors
        StructureProcessorType<?> chestLoot = Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR,
                Identifier.fromNamespaceAndPath(WeatheredWells.MOD_ID, "chest_loot"), ModProcessors.createChestLoot());
        ModProcessors.CHEST_LOOT = () -> chestLoot;
        StructureProcessorType<?> waterlogRemoval = Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR,
                Identifier.fromNamespaceAndPath(WeatheredWells.MOD_ID, "waterlog_removal"), ModProcessors.createWaterlogRemoval());
        ModProcessors.WATERLOG_REMOVAL = () -> waterlogRemoval;
        ModProcessors.init();

        // Register creative tabs
        CreativeModeTab tab = FabricItemGroup.builder()
                .title(Component.translatable("itemGroup.weatheredwells.weatheredwells"))
                .icon(() -> new ItemStack(ModItems.SOAKED_TOTEM.get()))
                .displayItems((parameters, output) -> {
                    output.accept(ModItems.SOAKED_TOTEM.get());
                    output.accept(ModItems.CLEAR_TOTEM.get());
                    output.accept(ModItems.DEEP_TOTEM.get());
                })
                .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
                Identifier.fromNamespaceAndPath(WeatheredWells.MOD_ID, "weatheredwells"), tab);
        ModCreativeTabs.WEATHERED_WELLS_TAB = () -> tab;

        // Register events
        ServerTickEvents.END_SERVER_TICK.register(WaterHealingHandler::onServerTick);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> WeatheredWellsEvents.onPlayerJoin(handler.player));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> WeatheredWellsEvents.onPlayerRespawn(newPlayer));

        WeatheredWells.init();
    }

    private static <T> Supplier<T> register(Registry<? super T> registry, String name, T entry) {
        T registered = Registry.register(registry, Identifier.fromNamespaceAndPath(WeatheredWells.MOD_ID, name), entry);
        return () -> registered;
    }
}
