package com.octavianonline.games.wheelenium.screen;

import com.atsisa.gox.framework.eventbus.IEventBus;
import com.atsisa.gox.framework.infrastructure.IViewManager;
import com.atsisa.gox.framework.utility.Timeout;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.framework.reels.utils.GameHelper;
import com.octavianonline.framework.standby.OctavianStandbyScene;

@Reflect_Full
public class WheeleniumTestScene extends OctavianStandbyScene {

    private static final int TIMEOUT_DELAY = 2000;
    private Timeout timeout;

    public WheeleniumTestScene(IViewManager viewManager, IEventBus eventBus,
                               int sceneIndex, String viewGroupID, String screenID) throws com.atsisa.gox.framework.exception.ValidationException {
        super(viewManager, eventBus, sceneIndex, viewGroupID, screenID);
    }

    @Override
    public void constructViews() {
    }

    private void initTimeout() {
        GameHelper.clearTimeout(timeout);
        timeout = new Timeout(TIMEOUT_DELAY, this::closeScene, true);
    }

    @Override
    public void play() {
        show();
        initTimeout();
    }

    @Override
    public void stop() {
        closeScene();
    }

    @Override
    public void terminate() {
        hide();
        GameHelper.clearTimeout(timeout);
    }

    private void closeScene() {
        hide();
        complete();
    }
}
