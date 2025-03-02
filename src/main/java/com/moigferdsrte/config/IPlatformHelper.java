package com.moigferdsrte.config;

import com.moigferdsrte.Entrance;

import java.nio.file.Path;
import java.util.ServiceLoader;

public interface IPlatformHelper {
    public static final IPlatformHelper INSTANCE = load(IPlatformHelper.class);

    Path getConfigDirectory();

    Path getGameDirectory();

    boolean isModLoaded(String modid);

    private static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Entrance.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
