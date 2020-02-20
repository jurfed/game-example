package com.octavianonline.games.wheelenium.screen;

import com.atsisa.gox.framework.animation.IAnimationFactory;
import com.atsisa.gox.framework.event.LanguageChangedEvent;
import com.atsisa.gox.framework.eventbus.IEventBus;
import com.atsisa.gox.framework.eventbus.NextObserver;
import com.atsisa.gox.framework.eventbus.Subscription;
import com.atsisa.gox.framework.infrastructure.IViewManager;
import com.atsisa.gox.framework.rendering.IRenderer;
import com.atsisa.gox.framework.screen.annotation.InjectView;
import com.atsisa.gox.framework.utility.Timeout;
import com.atsisa.gox.framework.utility.TimeoutCallback;
import com.atsisa.gox.framework.utility.logger.ILogger;
import com.atsisa.gox.framework.view.KeyframeAnimationView;
import com.atsisa.gox.framework.view.View;
import com.atsisa.gox.framework.view.ViewGroup;
import com.atsisa.gox.reels.ILinesModel;
import com.atsisa.gox.reels.ReelsPresentationStates;
import com.atsisa.gox.reels.event.PayTableModelChangedEvent;
import com.atsisa.gox.reels.event.PresentationStateChangedEvent;
import com.atsisa.gox.reels.model.ILinesModelProvider;
import com.atsisa.gox.reels.screen.PayTableScreen;
import com.atsisa.gox.reels.screen.model.PayTableScreenModel;
import com.octavianonline.framework.reels.components.paytable.PayTablePlateView;
import com.octavianonline.framework.reels.components.paytable.PayTableTextGroupView;
import com.octavianonline.framework.reels.components.paytable.PayTableWinAnimationView;
import com.octavianonline.games.wheelenium.event.symbols.PlayAnimationCommanEvent;
import com.octavianonline.games.wheelenium.event.symbols.StopAnimationCommanEvent;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * Represents the Pay table screen.
 */

public class WheeleniumPayTableScreenDesktop extends PayTableScreen {

    public static final String LAYOUT_ID_PROPERTY = "PaytableScreenLayoutId";

    private final Map<String, PayTablePlateView> symbolPlateViews = new HashMap<>();
    private final Map<String, PayTableTextGroupView> payTableTextGroupViews = new HashMap<>();
    private final ILinesModelProvider linesModelProvider;
    private final ILinesModel linesModel;
    private final List<Subscription> subscriptions = new ArrayList<>();
    WheeleniumPayTableScreenModel model;
    @InjectView

    ViewGroup languages;
    private int superSevenCount;
    private List<PayTableWinAnimationView> animations = new ArrayList<>();
    private String language;

    @InjectView

    KeyframeAnimationView logoAnim;
    /**
     * Initializes a new instance of the {@link WheeleniumPayTableScreenDesktop} class.
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
    public WheeleniumPayTableScreenDesktop(@Named(LAYOUT_ID_PROPERTY) String layoutId, WheeleniumPayTableScreenModel model, IRenderer renderer, IViewManager viewManager,
                                           IAnimationFactory animationFactory, ILogger logger, IEventBus eventBus, ILinesModelProvider linesModelProvider) {
        super(layoutId, model, renderer, viewManager, animationFactory, logger, eventBus);
        this.model = model;
        this.linesModelProvider = linesModelProvider;
        this.linesModel = this.linesModelProvider.getLinesModel();
    }

    @Override
    protected void registerEvents() {
        super.registerEvents();
    }

    @Override
    protected void beforeActivated() {
        super.beforeActivated();
        this.subscriptions.add(getEventBus().register(new WheeleniumPayTableScreenDesktop.PresentationStateChangedEventObserver(), PresentationStateChangedEvent.class));
    }

    @Override
    protected void beforeDeactivated() {
        super.beforeDeactivated();
        this.subscriptions.forEach(Subscription::unsubscribe);
    }

    @Override
    protected void afterActivated() {
        getEventBus().register(new PlayAnimationObserver(), PlayAnimationCommanEvent.class);
        getEventBus().register(new StopAnimationCommanObserver(), StopAnimationCommanEvent.class);
        getEventBus().register(new LanguageChangedEventObserver(), LanguageChangedEvent.class);
        language = model.getTranslator().getActiveLanguageCode().toLowerCase();
        languages.getChildren().forEach(view -> {
            setLanguageCode(view);
        });
        new Timeout(9000, new LogoAnimation(), true);
    }




    private class LanguageChangedEventObserver extends NextObserver<LanguageChangedEvent> {

        @Override
        public void onNext(LanguageChangedEvent event) {
            language = event.getLanguageCode().toLowerCase();
            languages.getChildren().forEach(view -> {
                setLanguageCode(view);
            });
        }
    }

    private void handlePresentationStateChangedEvent(PresentationStateChangedEvent event) {
        switch (event.getStateName()) {
            case ReelsPresentationStates.IDLE:
                this.stopAllAnimation();
                break;
            default:
                break;
        }
    }


    private void stopAllAnimation() {
        this.symbolPlateViews.values().forEach(view -> view.setState("Idle"));
        this.payTableTextGroupViews.values().forEach(PayTableTextGroupView::stopAllWinAnimation);
    }

    @Override
    public void handlePayTableModelChangedEvent(PayTableModelChangedEvent payTableModelChangedEvent) {
        getModel().updatePayTableValues(payTableModelChangedEvent.getValues());
    }

    void setLanguageCode(View view) {
        view.getTags().forEach(s -> {
            if (s != null && !s.equals(language)) {
                view.setVisible(false);
            } else {
                view.setVisible(true);
            }
        });

    }

    private class PlayAnimationObserver extends NextObserver<PlayAnimationCommanEvent> {


        @Override
        public void onNext(PlayAnimationCommanEvent playAnimationCommanEvent) {
            animations = new ArrayList<>();
            Map<String, Integer> winSymbolCount = SymbolsAnimationScreen.getWinSymbolsCount();
            Map<String, String> winSymbolLongShort = SymbolsAnimationScreen.getWinSymbolsLongShort();

            winSymbolCount.forEach((symbol, integer) -> {

                Set<Integer> indicatorsFor4 = new HashSet<>();

                Set<Integer> animationFrames = new HashSet<>();
                Set<Integer> invisible = new HashSet<>();

                if (symbol.equals("SEVEN")) {
                    animationFrames.add(0);
                    indicatorsFor4.add(integer);
                    invisible.add(0);
                    invisible.add(1);
                    PayTableWinAnimationView animationView = ((PayTableWinAnimationView) findViewById("SEVENGROUP"));

                    if (winSymbolLongShort.get("SEVEN").equals("Long")) {
                        animationFrames.add(1);
                    } else {
                        animationFrames.add(2);
                    }
                    animationView.playAnimation(indicatorsFor4, animationFrames, invisible);
                    animations.add(animationView);
                }

                if (symbol.equals("BELL")) {
                    animationFrames.add(0);
                    indicatorsFor4.add(integer);
                    invisible.add(0);
                    invisible.add(1);
                    PayTableWinAnimationView animationView = ((PayTableWinAnimationView) findViewById("BELLGROUP"));
                    if (winSymbolLongShort.get("BELL").equals("Long")) {
                        animationFrames.add(1);
                    } else {
                        animationFrames.add(2);
                    }
                    animationView.playAnimation(indicatorsFor4, animationFrames, invisible);
                    animations.add(animationView);
                }

                if (symbol.equals("CHERRY")) {
                    animationFrames.add(0);
                    indicatorsFor4.add(integer);
                    invisible.add(0);
                    invisible.add(1);
                    animationFrames.add(1);

                    PayTableWinAnimationView animationView = ((PayTableWinAnimationView) findViewById("CHERRYGROUP"));
                    animationView.playAnimation(indicatorsFor4, animationFrames, invisible);
                    animations.add(animationView);
                }

                if (symbol.equals("LEMON")) {
                    animationFrames.add(0);
                    indicatorsFor4.add(integer);
                    invisible.add(0);
                    invisible.add(1);
                    animationFrames.add(1);

                    PayTableWinAnimationView animationView = ((PayTableWinAnimationView) findViewById("LEMONGROUP"));
                    animationView.playAnimation(indicatorsFor4, animationFrames, invisible);
                    animations.add(animationView);
                }

                /**
                 * ORANGE and PLUM
                 */
                {


                    if (symbol.equals("ORANGE")) {
                        animationFrames.add(0);
                        indicatorsFor4.add(integer);
                        invisible.add(0);
                        invisible.add(1);
                        animationFrames.add(1);

                        if (winSymbolCount.containsKey("PLUM")) {
                            animationFrames.add(0);
                            indicatorsFor4.add(winSymbolCount.get("PLUM"));
                            invisible.add(0);
                            invisible.add(2);
                            animationFrames.add(2);
                        }

                        PayTableWinAnimationView animationView = ((PayTableWinAnimationView) findViewById("ORANGEGROUP"));
                        animationView.playAnimation(indicatorsFor4, animationFrames, invisible);
                        animations.add(animationView);
                    } else if (symbol.equals("PLUM")) {
                        animationFrames.add(0);
                        indicatorsFor4.add(integer);
                        invisible.add(0);
                        invisible.add(2);
                        animationFrames.add(2);

                        if (winSymbolCount.containsKey("ORANGE")) {
                            animationFrames.add(0);
                            indicatorsFor4.add(winSymbolCount.get("ORANGE"));
                            invisible.add(0);
                            invisible.add(1);
                            animationFrames.add(1);
                        }

                        PayTableWinAnimationView animationView = ((PayTableWinAnimationView) findViewById("ORANGEGROUP"));
                        animationView.playAnimation(indicatorsFor4, animationFrames, invisible);
                        animations.add(animationView);
                    }
                }


                /**
                 * WATERMELON anf grapes and PLUM
                 */
                {


                    if (symbol.equals("GRAPES")) {
                        animationFrames.add(0);
                        indicatorsFor4.add(integer);
                        invisible.add(0);
                        invisible.add(1);
                        animationFrames.add(1);

                        if (winSymbolCount.containsKey("WATERMELON")) {
                            animationFrames.add(0);
                            indicatorsFor4.add(winSymbolCount.get("WATERMELON"));
                            invisible.add(0);
                            invisible.add(2);
                            animationFrames.add(2);
                        }

                        PayTableWinAnimationView animationView = ((PayTableWinAnimationView) findViewById("WATERMELONGROUP"));
                        animationView.playAnimation(indicatorsFor4, animationFrames, invisible);
                        animations.add(animationView);
                    } else if (symbol.equals("WATERMELON")) {
                        animationFrames.add(0);
                        indicatorsFor4.add(integer);
                        invisible.add(0);
                        invisible.add(2);
                        animationFrames.add(2);

                        if (winSymbolCount.containsKey("GRAPES")) {
                            animationFrames.add(0);
                            indicatorsFor4.add(winSymbolCount.get("GRAPES"));
                            invisible.add(0);
                            invisible.add(1);
                            animationFrames.add(1);
                        }

                        PayTableWinAnimationView animationView = ((PayTableWinAnimationView) findViewById("WATERMELONGROUP"));
                        animationView.playAnimation(indicatorsFor4, animationFrames, invisible);
                        animations.add(animationView);
                    }
                }

            });


        }
    }

    private class StopAnimationCommanObserver extends NextObserver<StopAnimationCommanEvent> {

        @Override
        public void onNext(StopAnimationCommanEvent stopAnimationCommanEvent) {
            animations.forEach(payTableWinAnimationView -> {
                payTableWinAnimationView.stopAnimation();
            });
        }
    }

    class LogoAnimation implements TimeoutCallback{
        @Override
        public void onTimeout() {
            logoAnim.play();
            new Timeout(13000, new LogoAnimation(), true);
        }
    }

    private class PresentationStateChangedEventObserver extends NextObserver<PresentationStateChangedEvent> {
        @Override
        public void onNext(PresentationStateChangedEvent event) {
            handlePresentationStateChangedEvent(event);
        }
    }


}