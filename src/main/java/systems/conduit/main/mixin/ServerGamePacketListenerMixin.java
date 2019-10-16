package systems.conduit.main.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import systems.conduit.main.Conduit;
import systems.conduit.main.api.Player;
import systems.conduit.main.events.EventType;

@Mixin(value = ServerGamePacketListenerImpl.class, remap = false)
public class ServerGamePacketListenerMixin {

    @Shadow public ServerPlayer player;
    @Shadow @Final private MinecraftServer server;

    @Redirect(method = "handleChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    private void handleChat(PlayerList playerList, Component component, boolean b) {
        EventType.PlayerChatEvent event = new EventType.PlayerChatEvent((Player) this.player, component);
        Conduit.eventManager.dispatchEvent(event);
        Component eventMessage = event.getMessage();
        if (eventMessage != null) this.server.getPlayerList().broadcastMessage(eventMessage, b);
    }

    @ModifyArg(method = "handleChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;handleCommand(Ljava/lang/String;)V"))
    private String handleCommand(String message) {
        EventType.PlayerCommandEvent event = new EventType.PlayerCommandEvent((Player) this.player, message);
        Conduit.eventManager.dispatchEvent(event);
        return event.getMessage();
    }
}
