package dev.cromo29.ultraportals.utility;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Utility {

    public static Set<Entity> getEntitiesInChunks(Location location, int chunkRadius) {
        if (location == null) return new HashSet<>();

        Block block = location.getBlock();

        Set<Entity> entities = new HashSet<>();
        for (int x = -16 * chunkRadius; x <= 16 * chunkRadius; x += 16) {
            for (int z = -16 * chunkRadius; z <= 16 * chunkRadius; z += 16) {
                Collections.addAll(entities, block.getRelative(x, 0, z).getChunk().getEntities());
            }
        }
        return entities;
    }
}
