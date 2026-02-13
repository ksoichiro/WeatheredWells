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

import com.weatheredwells.data.PlayerBuffData;
import com.weatheredwells.registry.ModParticles;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles water contact healing logic for players with Lingering buff.
 * Called every server tick.
 *
 * Healing rules:
 * - Player must be in water (isInWater)
 * - After continuous water contact for activation delay, healing starts
 * - Default activation delay: 5 seconds (100 ticks)
 * - With Attunement: 2 seconds (40 ticks)
 * - Heal 0.5 hearts (1.0 health) at interval based on Lingering level:
 *   - Level I: every 60 ticks (3 seconds)
 *   - Level II: every 50 ticks (2.5 seconds)
 *   - Level III: every 40 ticks (2 seconds)
 */
public class WaterHealingHandler {
    private static final int DEFAULT_ACTIVATION_DELAY = 100; // 5 seconds
    private static final int ATTUNEMENT_ACTIVATION_DELAY = 40; // 2 seconds
    private static final float HEAL_AMOUNT = 1.0f; // 0.5 hearts

    private static final Map<UUID, Integer> waterContactTicks = new HashMap<>();

    public static void onServerTick(MinecraftServer server) {
        PlayerBuffData data = PlayerBuffData.get(server.overworld());

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            int lingeringLevel = data.getLingeringLevel(player);
            if (lingeringLevel <= 0) {
                continue;
            }

            UUID uuid = player.getUUID();

            if (player.isInWater()) {
                int ticks = waterContactTicks.getOrDefault(uuid, 0) + 1;
                waterContactTicks.put(uuid, ticks);

                int activationDelay = data.hasAttunement(player)
                        ? ATTUNEMENT_ACTIVATION_DELAY
                        : DEFAULT_ACTIVATION_DELAY;

                if (ticks >= activationDelay) {
                    int healInterval = getHealInterval(lingeringLevel);
                    int ticksSinceActivation = ticks - activationDelay;
                    if (ticksSinceActivation % healInterval == 0) {
                        float currentHealth = player.getHealth();
                        float maxHealth = player.getMaxHealth();
                        if (currentHealth < maxHealth) {
                            player.heal(HEAL_AMOUNT);
                            ServerLevel level = (ServerLevel) player.level();
                            level.sendParticles(
                                    ModParticles.WATER_HEALING.get(),
                                    player.getX(),
                                    player.getY() + 0.5,
                                    player.getZ(),
                                    2,
                                    0.2,
                                    0.3,
                                    0.2,
                                    0
                            );
                        }
                    }
                }
            } else {
                waterContactTicks.remove(uuid);
            }
        }
    }

    private static int getHealInterval(int level) {
        return switch (level) {
            case 1 -> 60;  // 3 seconds
            case 2 -> 50;  // 2.5 seconds
            case 3 -> 40;  // 2 seconds
            default -> 60;
        };
    }
}
