package com.mrmelon54.DraggableLists;

import com.mrmelon54.DraggableLists.config.ConfigStructure;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.gui.ConfigScreenProvider;
import me.shedaniel.autoconfig.gui.registry.DefaultGuiRegistryAccess;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Supplier;

public class DraggableLists {
    public static final String MOD_ID = "draggable_lists";
    public static final ConfigStructure CONFIG = AutoConfig.register(ConfigStructure.class, JanksonConfigSerializer::new).get();

    public static void init() {
    }

    public static Supplier<Screen> createConfigScreen(Screen screen) {
        ConfigManager<ConfigStructure> manager = (ConfigManager<ConfigStructure>) AutoConfig.getConfigHolder(ConfigStructure.class);
        return new ConfigScreenProvider<>(manager, new DefaultGuiRegistryAccess(), screen);
    }
}
