package com.octavianonline.games.wheelenium.screen;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.animation.IAnimationFactory;
import com.atsisa.gox.framework.eventbus.IEventBus;
import com.atsisa.gox.framework.infrastructure.IViewManager;
import com.atsisa.gox.framework.rendering.IRenderer;
import com.atsisa.gox.framework.screen.annotation.ExposeMethod;
import com.atsisa.gox.framework.utility.logger.ILogger;
import com.atsisa.gox.framework.view.ButtonView;
import com.atsisa.gox.reels.model.IDebugDataModelProvider;
import com.atsisa.gox.reels.screen.DebugScreen;
import com.atsisa.gox.reels.screen.model.DebugScreenModel;
import com.gwtent.reflection.client.annotations.Reflect_Full;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Reflect_Full
public class DragonsDebugScreen extends DebugScreen {

    public static final String LAYOUT_ID_PROPERTY = "DebugScreenLayoutId";
//    JFrame dialogFrame;

    /**
     * Initializes a new instance of the {@link DebugScreen} class.
     *
     * @param layoutId               layout identifier
     * @param model                  the model that extends the debug screen model
     * @param renderer               {@link IRenderer}
     * @param viewManager            {@link IViewManager}
     * @param animationFactory       {@link IAnimationFactory}
     * @param logger                 {@link ILogger}
     * @param eventBus               {@link IEventBus}
     * @param debugDataModelProvider
     */
    @Inject
    public DragonsDebugScreen(@Named(LAYOUT_ID_PROPERTY) String layoutId, DebugScreenModel model, IRenderer renderer, IViewManager viewManager, IAnimationFactory animationFactory, ILogger logger, IEventBus eventBus, IDebugDataModelProvider debugDataModelProvider) {
        super(layoutId, model, renderer, viewManager, animationFactory, logger, eventBus, debugDataModelProvider);
//        dialogFrame = new JFrame("Enter seed");
    }

    @Override
    protected void afterActivated() {
        super.afterActivated();
    }


    static final List<String> bonusSeeds = new ArrayList(){{add("a87ed78c");}};
//    static final List<String> bonusSeeds = new ArrayList(){{add("a87ed78c"); add("9d6074fa");add("2aaab876");add("3e4cb8c0"); add("c2487035");add("afccb245");add("23cdce18");}};

    @ExposeMethod
    public void enterSeed() {
        Random ran = new Random();
        int index = ran.nextInt(bonusSeeds.size());
        String seed = bonusSeeds.get(index);
        super.updateSeed(seed);
    }
}
