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
package com.weatheredwells.events;

import com.weatheredwells.WeatheredWells;
import com.weatheredwells.data.PlayerBuffData;
import com.weatheredwells.effects.WaterHealingHandler;
import com.weatheredwells.registry.ModEffects;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * Registers and handles mod events:
 * - Server tick: water healing logic
 * - Player advancement: grants buffs based on achievement
 * - Player join/respawn: restores MobEffect display from saved data
 */
public class WeatheredWellsEvents {
    private static final ResourceLocation ADV_WATER_SEEPS_IN =
            new ResourceLocation(WeatheredWells.MOD_ID, "water_seeps_in");
    private static final ResourceLocation ADV_CLARITY_SETTLES =
            new ResourceLocation(WeatheredWells.MOD_ID, "clarity_settles");
    private static final ResourceLocation ADV_DEPTHS_ENDURED =
            new ResourceLocation(WeatheredWells.MOD_ID, "depths_endured");
    private static final ResourceLocation ADV_AT_HOME_IN_WATER =
            new ResourceLocation(WeatheredWells.MOD_ID, "at_home_in_water");

    private static final int INFINITE_DURATION = -1;

    public static void register() {
        TickEvent.SERVER_POST.register(WaterHealingHandler::onServerTick);
        PlayerEvent.PLAYER_ADVANCEMENT.register(WeatheredWellsEvents::onAdvancement);
        PlayerEvent.PLAYER_JOIN.register(WeatheredWellsEvents::onPlayerJoin);
        PlayerEvent.PLAYER_RESPAWN.register(WeatheredWellsEvents::onPlayerRespawn);
    }

    private static void onAdvancement(ServerPlayer player, Advancement advancement) {
        ResourceLocation id = advancement.getId();
        PlayerBuffData data = PlayerBuffData.get(player.server.overworld());

        if (id.equals(ADV_WATER_SEEPS_IN)
                || id.equals(ADV_CLARITY_SETTLES)
                || id.equals(ADV_DEPTHS_ENDURED)) {
            int currentLevel = data.getLingeringLevel(player);
            int newLevel = currentLevel + 1;
            data.setLingeringLevel(player, newLevel);
            applyLingeringEffect(player, newLevel);
            WeatheredWells.LOGGER.debug("Granted waterways lingering level {} to {}",
                    newLevel, player.getName().getString());
        }

        if (id.equals(ADV_AT_HOME_IN_WATER)) {
            data.setAttunement(player, true);
            applyAttunementEffect(player);
            WeatheredWells.LOGGER.debug("Granted waterways attunement to {}",
                    player.getName().getString());
        }
    }

    private static void onPlayerJoin(ServerPlayer player) {
        restoreBuffEffects(player);
    }

    private static void onPlayerRespawn(ServerPlayer player, boolean endConquered) {
        restoreBuffEffects(player);
    }

    private static void restoreBuffEffects(ServerPlayer player) {
        PlayerBuffData data = PlayerBuffData.get(player.server.overworld());
        int lingeringLevel = data.getLingeringLevel(player);
        if (lingeringLevel > 0) {
            applyLingeringEffect(player, lingeringLevel);
        }
        if (data.hasAttunement(player)) {
            applyAttunementEffect(player);
        }
    }

    private static void applyLingeringEffect(ServerPlayer player, int level) {
        MobEffect effect = ModEffects.WATERWAYS_LINGERING.get();
        player.removeEffect(effect);
        player.addEffect(new MobEffectInstance(
                effect,
                INFINITE_DURATION,
                level - 1, // amplifier: 0 = Level I, 1 = Level II, etc.
                true,      // ambient (no particles)
                false,     // no visible particles
                true       // show icon
        ));
    }

    private static void applyAttunementEffect(ServerPlayer player) {
        MobEffect effect = ModEffects.WATERWAYS_ATTUNEMENT.get();
        player.addEffect(new MobEffectInstance(
                effect,
                INFINITE_DURATION,
                0,
                true,      // ambient (no particles)
                false,     // no visible particles
                true       // show icon
        ));
    }
}
