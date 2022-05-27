package me.dustin.crash;

import me.dustin.crash.feature.*;
import me.dustin.jex.feature.mod.core.Category;
import me.dustin.jex.feature.plugin.JexPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashPlugin {
    private static final Logger logger = LogManager.getFormatterLogger("CrashAddonPlugin");

    public static final Category CRASH = new Category("Crash", 0xff800000);

    @JexPlugin.FeaturesLoad
    public void loadFeatures() {
        CrashFeatureManager.INSTANCE.load();
        logger.info("Added %d features".formatted(CrashFeatureManager.INSTANCE.getMyFeatures().size()));
    }

    @JexPlugin.DisablePlugin
    public void disable() {
        CrashFeatureManager.INSTANCE.disablePlugin();
        Category.values().remove(CRASH);
    }

    @JexPlugin.EnablePlugin
    public void enable() {
        CrashFeatureManager.INSTANCE.enablePlugin();
        Category.values().add(CRASH);
    }

}
