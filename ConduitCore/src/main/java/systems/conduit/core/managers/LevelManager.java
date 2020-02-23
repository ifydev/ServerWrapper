package systems.conduit.core.managers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorage;
import systems.conduit.core.Conduit;

import java.util.Optional;

// TODO: This needs to be moved to the API
public class LevelManager {

    /**
     * Attempt to find a level by a given name.
     *
     * @since API 0.1
     * @param name the name of the level
     * @return a level with the name, if found.
     */
    public Optional<ServerLevel> getLevel(String name) {
        return Conduit.getServer().map(server -> server.getLevel(DimensionType.getByName(ResourceLocation.of(name, '/'))));
    }

    /**
     * Create a new level with given information.
     *
     * @since API 0.1
     *
     * @param name the name of the level
     * @param dimension dimension metadata
     * @return the created level, if it could be created. Empty otherwise.
     */
    public Optional<ServerLevel> createLevel(String name, DimensionType dimension) {
        return Conduit.getServer().map(server -> {
            LevelStorage storage = server.getStorageSource().selectLevel(name, (MinecraftServer) server);
            LevelData data = storage.prepareLevel();
            if (data == null) return null;

            data.setLevelName(name);
            LevelSettings settings = new LevelSettings(data);
            ServerLevel level = new ServerLevel((MinecraftServer) server, server.getExecutor(), storage, data, dimension, server.getProfiler(), new ChunkProgressListener() {
                @Override
                public void updateSpawnPos(ChunkPos chunkPos) {
                }

                @Override
                public void onStatusChange(ChunkPos chunkPos, ChunkStatus chunkStatus) {
                }

                @Override
                public void stop() {
                }
            });

            if (!data.isInitialized()) {
                level.setInitialSpawn(settings);
                data.setInitialized(true);
            }
            server.getLevels().put(dimension, level);

            return null;
        });
    }
}