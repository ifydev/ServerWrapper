package me.ifydev.serverwrapper.mixin.event;

import me.ifydev.serverwrapper.ServerWrapper;
import me.ifydev.serverwrapper.events.EventType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Innectic
 * @since 10/6/2019
 */
@Mixin(value = ServerPlayerGameMode.class, remap = false)
public abstract class ServerPlayerGameModeMixin {

    @Shadow public ServerPlayer player;
    @Shadow public ServerLevel level;

    @Inject(method = "updateGameMode", at = @At("HEAD"))
    public void updateGameMode(GameType gameType, CallbackInfo ci) {
        // TODO: Event cancellations
        ServerWrapper.eventManager.dispatchEvent(new EventType.PlayerGameModeChangeEvent(player, gameType));
    }

    @Inject(method = "destroyAndAck", at = @At("HEAD"))
    public void destroyAndAck(BlockPos blockPos, ServerboundPlayerActionPacket.Action action, CallbackInfo ci) {
        // TODO: Event cancellations
        EventType.BlockBreakEvent event = new EventType.BlockBreakEvent(player, this.level.getBlockState(blockPos));
        ServerWrapper.eventManager.dispatchEvent(event);
    }
}
