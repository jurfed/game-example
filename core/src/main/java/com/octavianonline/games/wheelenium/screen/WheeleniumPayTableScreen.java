package com.octavianonline.games.wheelenium.screen;

import com.atsisa.gox.framework.animation.IAnimationFactory;
import com.atsisa.gox.framework.eventbus.IEventBus;
import com.atsisa.gox.framework.eventbus.NextObserver;
import com.atsisa.gox.framework.infrastructure.IViewManager;
import com.atsisa.gox.framework.rendering.IRenderer;
import com.atsisa.gox.framework.screen.annotation.InjectView;
import com.atsisa.gox.framework.utility.Timeout;
import com.atsisa.gox.framework.utility.TimeoutCallback;
import com.atsisa.gox.framework.utility.logger.ILogger;
import com.atsisa.gox.framework.view.ViewGroup;
import com.atsisa.gox.reels.event.BetModelChangedEvent;
import com.atsisa.gox.reels.event.PayTableModelChangedEvent;
import com.atsisa.gox.reels.event.PresentationStateChangedEvent;
import com.atsisa.gox.reels.model.ILinesModelProvider;
import com.atsisa.gox.reels.screen.PayTableScreen;
import com.atsisa.gox.reels.screen.model.PayTableScreenModel;
import com.gwtent.reflection.client.annotations.Reflect_Full;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Represents the Pay table screen.
 */
@Reflect_Full
public class WheeleniumPayTableScreen extends PayTableScreen {

    public static final String LAYOUT_ID_PROPERTY = "PaytableScreenLayoutId";
    private long previousBet = 20;

    @InjectView
    ViewGroup payTable;

    private Timeout payTableHideTimeout;

    /**
     * Initializes a new instance of the {@link WheeleniumPayTableScreen} class.
     *
     * @param layoutId           layout identifier
     * @param model              {@link PayTableScreenModel}
     * @param renderer           {@link IRenderer}
     * @param viewManager        {@link IViewManager}
     * @param animationFactory   {@link IAnimationFactory}
     * @param logger             {@link ILogger}
     * @param eventBus           {@link IEventBus}
     * @param linesModelProvider
     */
    @Inject
    public WheeleniumPayTableScreen(@Named(LAYOUT_ID_PROPERTY) String layoutId, WheeleniumPayTableScreenModel model, IRenderer renderer, IViewManager viewManager,
                                    IAnimationFactory animationFactory, ILogger logger, IEventBus eventBus, ILinesModelProvider linesModelProvider) {
        super(layoutId, model, renderer, viewManager, animationFactory, logger, eventBus);
    }

    @Override
    protected void registerEvents() {
        super.registerEvents();
    }

    @Override
    protected void beforeActivated() {
        super.beforeActivated();
    }

    class ShowPaytable extends NextObserver<BetModelChangedEvent> {

        @Override
        public void onNext(BetModelChangedEvent betModelChangedEvent) {

            if (previousBet != betModelChangedEvent.getBetModel().getTotalBet()) {
                showPayTable();
            }
            previousBet = betModelChangedEvent.getBetModel().getTotalBet();
        }
    }

    private void showPayTable() {
        if (payTableHideTimeout != null && !payTableHideTimeout.isCleaned()) {
            payTableHideTimeout.clear();
        }
        payTableHideTimeout = new Timeout(3000, new HidePayTableTimeOut(), true);
        payTable.setVisible(true);
    }

    private class PresentationStateChangedEventObserver extends NextObserver<PresentationStateChangedEvent> {
        @Override
        public void onNext(PresentationStateChangedEvent event) {
            handlePresentationStateChangedEvent(event);
        }
    }

    private void handlePresentationStateChangedEvent(PresentationStateChangedEvent event) {

        payTable.setVisible(false);
        if (payTableHideTimeout != null && !payTableHideTimeout.isCleaned()) {
            payTableHideTimeout.clear();
        }

    }

    class HidePayTableTimeOut implements TimeoutCallback {

        @Override
        public void onTimeout() {
            payTable.setVisible(false);
        }
    }


    @Override
    protected void afterActivated() {
        super.afterActivated();
        getEventBus().register(new ShowPaytable(), BetModelChangedEvent.class);
        getEventBus().register(new PresentationStateChangedEventObserver(), PresentationStateChangedEvent.class);
    }


    @Override
    public void handlePayTableModelChangedEvent(PayTableModelChangedEvent payTableModelChangedEvent) {
        getModel().updatePayTableValues(payTableModelChangedEvent.getValues());
    }


}
