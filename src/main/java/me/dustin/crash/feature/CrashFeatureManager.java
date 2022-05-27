package me.dustin.crash.feature;

import me.dustin.events.EventManager;
import me.dustin.jex.feature.mod.core.Feature;
import me.dustin.jex.feature.mod.core.FeatureManager;

import java.util.ArrayList;

public enum CrashFeatureManager {
    INSTANCE;
    private final ArrayList<Feature> myFeatures = new ArrayList<>();
    /*
        For easily supporting disabling the plugin, seperate your own features in a list and manually register/unregister them when enabled/disabled
     */
    public void load() {
        myFeatures.add(new AACCrash());
        myFeatures.add(new BoatCrash());
        myFeatures.add(new BookCrash());
        myFeatures.add(new ContainerCrash());
        myFeatures.add(new CraftingCrash());
        myFeatures.add(new EntityCrash());
        myFeatures.add(new InvalidPosCrash());
        myFeatures.add(new LecternCrash());
        myFeatures.add(new LoginCrash());
        myFeatures.add(new MessageLagger());
        myFeatures.add(new MoveCrash());
        myFeatures.add(new NoComCrash());
        myFeatures.add(new PacketSpammer());
        myFeatures.add(new SignCrash());
        myFeatures.add(new TryUseCrash());
        FeatureManager.INSTANCE.getFeatures().addAll(myFeatures);
    }

    public void enablePlugin() {
        myFeatures.forEach(feature -> {
            if (feature.getState())
                EventManager.register(feature);
            FeatureManager.INSTANCE.getFeatures().add(feature);
        });
    }

    public void disablePlugin() {
        myFeatures.forEach(feature -> {
            if (feature.getState())
                EventManager.unregister(feature);
            FeatureManager.INSTANCE.getFeatures().remove(feature);
        });
    }

    public ArrayList<Feature> getMyFeatures() {
        return myFeatures;
    }
}
