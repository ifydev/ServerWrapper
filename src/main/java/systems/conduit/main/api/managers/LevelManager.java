package systems.conduit.main.api.managers;

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
import systems.conduit.main.Conduit;

import javax.annotation.Nullable;
import java.util.Optional;

public class LevelManager {

    public Optional<ServerLevel> getLevel(String name) {
        return Conduit.getServer().map(server -> server.getLevel(DimensionType.getByName(ResourceLocation.of(name, '/'))));
    }

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
                public void onStatusChange(ChunkPos chunkPos, @Nullable ChunkStatus chunkStatus) {
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
