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
package com.weatheredwells.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * World-level SavedData that stores per-player buff information.
 * Works across both Fabric and NeoForge without platform-specific APIs.
 */
public class PlayerBuffData extends SavedData {
    private static final String DATA_NAME = "weatheredwells_player_buffs";

    private final Map<UUID, PlayerBuff> playerBuffs = new HashMap<>();

    private static final Codec<PlayerBuff> PLAYER_BUFF_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("lingering_level").forGetter(buff -> buff.lingeringLevel),
                    Codec.BOOL.fieldOf("attunement").forGetter(buff -> buff.attunement)
            ).apply(instance, PlayerBuff::new)
    );

    public static final Codec<PlayerBuffData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Codec.STRING, PLAYER_BUFF_CODEC)
                            .fieldOf("players")
                            .forGetter(data -> {
                                Map<String, PlayerBuff> map = new HashMap<>();
                                data.playerBuffs.forEach((uuid, buff) ->
                                        map.put(uuid.toString(), buff));
                                return map;
                            })
            ).apply(instance, playersMap -> {
                PlayerBuffData data = new PlayerBuffData();
                playersMap.forEach((key, buff) ->
                        data.playerBuffs.put(UUID.fromString(key),
                                new PlayerBuff(buff.lingeringLevel, buff.attunement)));
                return data;
            })
    );

    public static final SavedDataType<PlayerBuffData> TYPE = new SavedDataType<>(
            DATA_NAME,
            PlayerBuffData::new,
            CODEC,
            null
    );

    public static PlayerBuffData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public int getLingeringLevel(ServerPlayer player) {
        PlayerBuff buff = playerBuffs.get(player.getUUID());
        return buff != null ? buff.lingeringLevel : 0;
    }

    public void setLingeringLevel(ServerPlayer player, int level) {
        PlayerBuff buff = playerBuffs.computeIfAbsent(player.getUUID(), k -> new PlayerBuff(0, false));
        buff.lingeringLevel = Math.min(level, 3);
        setDirty();
    }

    public boolean hasAttunement(ServerPlayer player) {
        PlayerBuff buff = playerBuffs.get(player.getUUID());
        return buff != null && buff.attunement;
    }

    public void setAttunement(ServerPlayer player, boolean attunement) {
        PlayerBuff buff = playerBuffs.computeIfAbsent(player.getUUID(), k -> new PlayerBuff(0, false));
        buff.attunement = attunement;
        setDirty();
    }

    private static class PlayerBuff {
        int lingeringLevel;
        boolean attunement;

        PlayerBuff(int lingeringLevel, boolean attunement) {
            this.lingeringLevel = lingeringLevel;
            this.attunement = attunement;
        }
    }
}
