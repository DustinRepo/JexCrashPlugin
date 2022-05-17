package me.dustin.crash;

import me.dustin.crash.feature.LoginCrash;
import me.dustin.jex.feature.mod.core.FeatureManager;
import me.dustin.jex.feature.plugin.JexPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashPlugin {
    private static final Logger logger = LogManager.getFormatterLogger("CrashAddonPlugin");
    @JexPlugin.FeaturesLoad
    public void loadFeatures() {
        FeatureManager.INSTANCE.getFeatures().add(new LoginCrash());
        logger.info("Added 1 feature");
    }

    @JexPlugin.ClientLoad
    public void loadClient() {
        logger.info("CrashAddonPlugin loaded");
    }

}
