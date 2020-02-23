package systems.conduit.core.plugin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import systems.conduit.core.Conduit;
import systems.conduit.core.commands.BaseCommand;
import systems.conduit.core.datastore.DatastoreController;
import systems.conduit.core.events.EventListener;
import systems.conduit.core.plugin.annotation.PluginMeta;
import systems.conduit.core.plugin.config.Configuration;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// TODO: This needs to be moved to the API
public abstract class Plugin {

    @Getter(AccessLevel.MODULE) @Setter(AccessLevel.MODULE) PluginClassLoader classLoader;
    @Getter @Setter(AccessLevel.MODULE) private PluginMeta meta;
    @Getter @Setter(AccessLevel.MODULE) private PluginState pluginState = PluginState.UNLOADED;
    @Getter(AccessLevel.PUBLIC) private Map<Integer, Map<EventListener, List<Method>>> events = new ConcurrentHashMap<>();
    @Setter(AccessLevel.MODULE) private Configuration config = null;
    @Getter @Setter(AccessLevel.MODULE) private DatastoreController datastore;

    protected abstract void onEnable();

    protected abstract void onDisable();

    @SafeVarargs
    protected final void registerListeners(Class<? extends EventListener>... clazz) {
        Arrays.stream(clazz).forEach(eventClass -> Conduit.getEventManager().registerEventClass(this, eventClass));
    }

    /**
     * Get the plugin's configuration in the type provided.
     *
     * @param <T> the configuration type
     * @return the plugin's configuration
     */
    @SuppressWarnings("unchecked")
    public <T extends Configuration> Optional<T> getConfig() {
        // TODO: Can this be improved?

        if (this.config == null) return Optional.empty();
        try {
            return Optional.of((T) this.config);
        } catch (ClassCastException ignored) {
            return Optional.empty();
        }
    }

    protected void registerCommands(BaseCommand... commands) {
        Conduit.getCommandManager().registerCommand(commands);
    }

    public String toStringColored() {
        ChatFormatting color = ChatFormatting.RED;
        if (pluginState == PluginState.ENABLING || pluginState == PluginState.DISABLING) color = ChatFormatting.YELLOW;
        if (pluginState == PluginState.ENABLED) color = ChatFormatting.GREEN;
        return new TextComponent(meta.name()).withStyle(color).getColoredString();
    }

    @Override
    public String toString() {
        return meta.name();
    }
}