package me.dustin.crash;

import me.dustin.crash.feature.*;
import me.dustin.jex.feature.mod.core.Category;
import me.dustin.jex.feature.mod.core.FeatureManager;
import me.dustin.jex.feature.plugin.JexPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashPlugin {
    private static final Logger logger = LogManager.getFormatterLogger("CrashAddonPlugin");

    public static final Category CRASH = new Category("Crash", 0xff800000);

    @JexPlugin.FeaturesLoad
    public void loadFeatures() {
        FeatureManager.INSTANCE.getFeatures().add(new AACCrash());
        FeatureManager.INSTANCE.getFeatures().add(new BoatCrash());
        FeatureManager.INSTANCE.getFeatures().add(new BookCrash());
        FeatureManager.INSTANCE.getFeatures().add(new ContainerCrash());
        FeatureManager.INSTANCE.getFeatures().add(new CraftingCrash());
        FeatureManager.INSTANCE.getFeatures().add(new EntityCrash());
        FeatureManager.INSTANCE.getFeatures().add(new InvalidPosCrash());
        FeatureManager.INSTANCE.getFeatures().add(new LecternCrash());
        FeatureManager.INSTANCE.getFeatures().add(new LoginCrash());
        FeatureManager.INSTANCE.getFeatures().add(new MessageLagger());
        FeatureManager.INSTANCE.getFeatures().add(new MoveCrash());
        FeatureManager.INSTANCE.getFeatures().add(new NoComCrash());
        FeatureManager.INSTANCE.getFeatures().add(new PacketSpammer());
        FeatureManager.INSTANCE.getFeatures().add(new SignCrash());
        FeatureManager.INSTANCE.getFeatures().add(new TryUseCrash());
        logger.info("Added 15 features");
    }

}
