package systems.conduit.main.mixin;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.commands.GameModeCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = GameModeCommand.class, remap = false)
public abstract class GamemodeCommandMixin {

    /**
     * Fixes gamemode command output using the wrong gamemode after changed by the event.
     * @author Clutch
     */
    @Overwrite
    private static void logGamemodeChange(CommandSourceStack sourceStack, ServerPlayer player, GameType gameType) {
        Component var3 = new TranslatableComponent("gameMode." + player.gameMode.getGameModeForPlayer().getName());
        if (sourceStack.getEntity() == player) {
            sourceStack.sendSuccess(new TranslatableComponent("commands.gamemode.success.self", var3), true);
        } else {
            if (sourceStack.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
                player.sendMessage(new TranslatableComponent("gameMode.changed", var3));
            }
            sourceStack.sendSuccess(new TranslatableComponent("commands.gamemode.success.other", player.getDisplayName(), var3), true);
        }
    }
}
