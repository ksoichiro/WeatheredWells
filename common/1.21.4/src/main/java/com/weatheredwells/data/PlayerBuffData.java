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

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * World-level SavedData that stores per-player buff information.
 * Works across both Fabric and NeoForge without platform-specific APIs.
 */
public class PlayerBuffData extends SavedData {
    private static final String DATA_NAME = "weatheredwells_player_buffs";
    private static final String TAG_PLAYERS = "players";
    private static final String TAG_LINGERING_LEVEL = "lingering_level";
    private static final String TAG_ATTUNEMENT = "attunement";

    private final Map<UUID, PlayerBuff> playerBuffs = new HashMap<>();

    public static PlayerBuffData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new Factory<>(PlayerBuffData::new, PlayerBuffData::load, null),
                DATA_NAME
        );
    }

    public static PlayerBuffData load(CompoundTag tag, HolderLookup.Provider registries) {
        PlayerBuffData data = new PlayerBuffData();
        CompoundTag playersTag = tag.getCompound(TAG_PLAYERS);
        for (String key : playersTag.getAllKeys()) {
            UUID uuid = UUID.fromString(key);
            CompoundTag playerTag = playersTag.getCompound(key);
            PlayerBuff buff = new PlayerBuff(
                    playerTag.getInt(TAG_LINGERING_LEVEL),
                    playerTag.getBoolean(TAG_ATTUNEMENT)
            );
            data.playerBuffs.put(uuid, buff);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag playersTag = new CompoundTag();
        for (Map.Entry<UUID, PlayerBuff> entry : playerBuffs.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putInt(TAG_LINGERING_LEVEL, entry.getValue().lingeringLevel);
            playerTag.putBoolean(TAG_ATTUNEMENT, entry.getValue().attunement);
            playersTag.put(entry.getKey().toString(), playerTag);
        }
        tag.put(TAG_PLAYERS, playersTag);
        return tag;
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
