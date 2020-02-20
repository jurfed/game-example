package com.octavianonline.games.wheelenium.screen;

import com.atsisa.gox.framework.animation.IAnimationFactory;
import com.atsisa.gox.framework.eventbus.IEventBus;
import com.atsisa.gox.framework.infrastructure.IViewManager;
import com.atsisa.gox.framework.rendering.IRenderer;
import com.atsisa.gox.framework.screen.Screen;
import com.atsisa.gox.framework.screen.model.ScreenModel;
import com.atsisa.gox.framework.utility.logger.ILogger;
import com.gwtent.reflection.client.annotations.Reflect_Full;

import javax.inject.Inject;

@Reflect_Full
public class WheeleniumReelsControlScreen extends Screen {

    @Inject
    public WheeleniumReelsControlScreen(String layoutId, ScreenModel model, IRenderer renderer, IViewManager viewManager, IAnimationFactory animationFactory, ILogger logger, IEventBus eventBus) {
        super(layoutId, model, renderer, viewManager, animationFactory, logger, eventBus);
    }
}
