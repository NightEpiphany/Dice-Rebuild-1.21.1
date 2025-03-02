package com.moigferdsrte.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moigferdsrte.Entrance;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;

public class DiceCubeConfig {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .create();
    public final GeneralSettings generalSettings = new GeneralSettings();
    private final ConfigImpl keyBinding = new ConfigImpl();
    private File file;

    public static DiceCubeConfig load(File file) {
        DiceCubeConfig config;
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                config = GSON.fromJson(reader, DiceCubeConfig.class);
            } catch (Exception e) {
                Entrance.LOGGER.error("Could not parse config, falling back to defaults!", e);
                config = new DiceCubeConfig();
            }
        } else {
            config = new DiceCubeConfig();
        }
        config.file = file;
        config.save();

        return config;
    }

    public ConfigImpl getKeyBinding() {
        return this.keyBinding;
    }

    public void save() {
        File dir = this.file.getParentFile();

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Could not create parent directories");
            }
        } else if (!dir.isDirectory()) {
            throw new RuntimeException("The parent file is not a directory");
        }

        try (FileWriter writer = new FileWriter(this.file)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            throw new RuntimeException("Could not save configuration file", e);
        }
    }


    public static class GeneralSettings {
        public boolean enable = true;
        public boolean diceGenerateParticle = true;
    }

    public static class ConfigImpl implements ClientTickEvents.EndTick {

        public final KeyBinding toggleParticle;

        public ConfigImpl() {
            this.toggleParticle = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.dice.toggle_particle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.dice"));
        }

        @Override
        public void onEndTick(MinecraftClient minecraftClient) {
            while (this.toggleParticle.wasPressed()) {
                TestClient.config().generalSettings.enable = !TestClient.config().generalSettings.enable;
                TestClient.config().save();

            }
        }
    }
}
