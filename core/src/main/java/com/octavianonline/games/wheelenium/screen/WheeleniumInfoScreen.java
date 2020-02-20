package com.octavianonline.games.wheelenium.screen;

import com.atsisa.gox.framework.animation.IAnimationFactory;
import com.atsisa.gox.framework.animation.TweenViewAnimationData;
import com.atsisa.gox.framework.eventbus.IEventBus;
import com.atsisa.gox.framework.eventbus.NextObserver;
import com.atsisa.gox.framework.eventbus.annotation.Subscribe;
import com.atsisa.gox.framework.infrastructure.IViewManager;
import com.atsisa.gox.framework.rendering.IRenderer;
import com.atsisa.gox.framework.screen.model.ScreenModel;
import com.atsisa.gox.framework.utility.IFinishCallback;
import com.atsisa.gox.framework.utility.localization.ITranslator;
import com.atsisa.gox.reels.event.PayTableModelChangedEvent;
import com.atsisa.gox.reels.screen.InfoScreen;
import com.atsisa.gox.reels.screen.model.PayTableScreenModel;
import com.atsisa.gox.reels.screen.transition.InfoScreenTransition;
import com.atsisa.gox.framework.utility.logger.ILogger;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.framework.reels.screens.OctavianInfoScreen;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Represents the info screen.
 */
@Reflect_Full
public class WheeleniumInfoScreen extends OctavianInfoScreen {


    /**
     * Initializes a new instance of the {@link InfoScreen} class.
     *
     * @param layoutId             layout identifier
     * @param model                {@link ScreenModel}
     * @param renderer             {@link IRenderer}
     * @param viewManager          {@link IViewManager}
     * @param animationFactory     {@link IAnimationFactory}
     * @param logger               {@link ILogger}
     * @param eventBus             {@link IEventBus}
     * @param infoScreenTransition {@link InfoScreenTransition}
     * @param translator
     */
    @Inject
    public WheeleniumInfoScreen(@Named(LAYOUT_ID_PROPERTY)String layoutId, IRenderer renderer, IViewManager viewManager, IAnimationFactory animationFactory, ILogger logger, IEventBus eventBus, WheeleniumScreenTransition infoScreenTransition, ITranslator translator, WheeleniumPayTableScreenModel model) {
        super(layoutId, model, renderer, viewManager, animationFactory, logger, eventBus, infoScreenTransition, translator);
    }

    /**
     * Registers events.
     */
    protected void registerEvents() {
        super.registerEvents();
        getEventBus().register(new PayTableModelChangedEventObserver(), PayTableModelChangedEvent.class);
    }

    @Override
    public void show(String viewId, TweenViewAnimationData viewAnimationData, IFinishCallback finishCallback) {
        viewAnimationData.setTimeSpan(0);
        super.show(viewAnimationData, finishCallback);

    }

    @Override
    protected void show(TweenViewAnimationData viewAnimationData, IFinishCallback finishCallback, Object triggeredEvent) {
        if (isActive()) {
            viewAnimationData.setTimeSpan(0);
            showNext(finishCallback, triggeredEvent);
        } else {
            super.show(viewAnimationData, finishCallback, triggeredEvent);

        }
    }


    @Subscribe
    public void handlePayTableModelChangedEvent(PayTableModelChangedEvent event) {
        ((WheeleniumPayTableScreenModel) getModel()).updatePayTableValues(event.getValues());
    }

    private class PayTableModelChangedEventObserver extends NextObserver<PayTableModelChangedEvent> {

        @Override
        public void onNext(final PayTableModelChangedEvent payTableModelChangedEvent) {
            handlePayTableModelChangedEvent(payTableModelChangedEvent);
        }
    }

    @Override
    public void hide(TweenViewAnimationData viewAnimationData, final IFinishCallback callback) {
        viewAnimationData.setTimeSpan(0);
        super.hide(viewAnimationData, callback);
    }

}
