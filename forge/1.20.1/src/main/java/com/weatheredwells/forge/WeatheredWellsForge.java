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
package com.weatheredwells.forge;

import com.weatheredwells.WeatheredWells;
import com.weatheredwells.effects.WaterHealingHandler;
import com.weatheredwells.events.WeatheredWellsEvents;
import com.weatheredwells.registry.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod(WeatheredWells.MOD_ID)
public class WeatheredWellsForge {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, WeatheredWells.MOD_ID);
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, WeatheredWells.MOD_ID);
    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, WeatheredWells.MOD_ID);
    private static final DeferredRegister<StructureProcessorType<?>> PROCESSORS = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, WeatheredWells.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WeatheredWells.MOD_ID);

    private static final RegistryObject<Item> SOAKED_TOTEM = ITEMS.register("soaked_totem", ModItems::createSoakedTotem);
    private static final RegistryObject<Item> CLEAR_TOTEM = ITEMS.register("clear_totem", ModItems::createClearTotem);
    private static final RegistryObject<Item> DEEP_TOTEM = ITEMS.register("deep_totem", ModItems::createDeepTotem);

    private static final RegistryObject<MobEffect> WATERWAYS_LINGERING = EFFECTS.register("waterways_lingering", ModEffects::createWaterwaysLingering);
    private static final RegistryObject<MobEffect> WATERWAYS_ATTUNEMENT = EFFECTS.register("waterways_attunement", ModEffects::createWaterwaysAttunement);
    private static final RegistryObject<MobEffect> WATERWAYS_IMMERSION = EFFECTS.register("waterways_immersion", ModEffects::createWaterwaysImmersion);

    private static final RegistryObject<ParticleType<?>> WATER_HEALING = PARTICLES.register("water_healing", ModParticles::createWaterHealing);

    private static final RegistryObject<StructureProcessorType<?>> CHEST_LOOT = PROCESSORS.register("chest_loot", ModProcessors::createChestLoot);
    private static final RegistryObject<StructureProcessorType<?>> WATERLOG_REMOVAL = PROCESSORS.register("waterlog_removal", ModProcessors::createWaterlogRemoval);

    private static final RegistryObject<CreativeModeTab> WEATHERED_WELLS_TAB = TABS.register("weatheredwells", () ->
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .title(Component.translatable("itemGroup.weatheredwells.weatheredwells"))
                    .icon(() -> new ItemStack(SOAKED_TOTEM.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(SOAKED_TOTEM.get());
                        output.accept(CLEAR_TOTEM.get());
                        output.accept(DEEP_TOTEM.get());
                    })
                    .build()
    );

    public WeatheredWellsForge() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(modEventBus);
        EFFECTS.register(modEventBus);
        PARTICLES.register(modEventBus);
        PROCESSORS.register(modEventBus);
        TABS.register(modEventBus);

        modEventBus.addListener(this::onCommonSetup);

        MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) {
                WaterHealingHandler.onServerTick(event.getServer());
            }
        });
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> WeatheredWellsEvents.onPlayerJoin((ServerPlayer) event.getEntity()));
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerRespawnEvent event) -> WeatheredWellsEvents.onPlayerRespawn((ServerPlayer) event.getEntity()));

        // Assign Suppliers for shared registry access
        ModItems.SOAKED_TOTEM = SOAKED_TOTEM;
        ModItems.CLEAR_TOTEM = CLEAR_TOTEM;
        ModItems.DEEP_TOTEM = DEEP_TOTEM;
        ModEffects.WATERWAYS_LINGERING = WATERWAYS_LINGERING;
        ModEffects.WATERWAYS_ATTUNEMENT = WATERWAYS_ATTUNEMENT;
        ModEffects.WATERWAYS_IMMERSION = WATERWAYS_IMMERSION;
        ModParticles.WATER_HEALING = () -> (SimpleParticleType) WATER_HEALING.get();
        ModProcessors.CHEST_LOOT = CHEST_LOOT;
        ModProcessors.WATERLOG_REMOVAL = WATERLOG_REMOVAL;
        ModCreativeTabs.WEATHERED_WELLS_TAB = WEATHERED_WELLS_TAB;

        WeatheredWells.init();
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        ModProcessors.init();
    }
}
