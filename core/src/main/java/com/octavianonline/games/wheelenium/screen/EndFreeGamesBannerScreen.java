package com.octavianonline.games.wheelenium.screen;

import aurelienribon.tweenengine.TweenCallback;
import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.animation.IAnimationFactory;
import com.atsisa.gox.framework.animation.TweenViewAnimation;
import com.atsisa.gox.framework.eventbus.IEventBus;
import com.atsisa.gox.framework.eventbus.NextObserver;
import com.atsisa.gox.framework.infrastructure.ISoundManager;
import com.atsisa.gox.framework.infrastructure.IViewManager;
import com.atsisa.gox.framework.rendering.IRenderer;
import com.atsisa.gox.framework.screen.Screen;
import com.atsisa.gox.framework.screen.annotation.InjectView;
import com.atsisa.gox.framework.utility.Timeout;
import com.atsisa.gox.framework.utility.TimeoutCallback;
import com.atsisa.gox.framework.utility.logger.ILogger;
import com.atsisa.gox.framework.view.RectangleViewMask;
import com.atsisa.gox.framework.view.TextView;
import com.atsisa.gox.framework.view.ViewGroup;
import com.atsisa.gox.reels.ICreditsFormatter;
import com.atsisa.gox.reels.model.IFreeGamesModelProvider;
import com.atsisa.gox.reels.model.RememberedMatrixModelProvider;
import com.atsisa.gox.reels.view.AbstractReelGroup;
import com.gwtent.reflection.client.HasReflect;
import com.gwtent.reflection.client.Reflectable;
import com.octavianonline.framework.reels.model.FreeGameMultiplierModelProvider;
import com.octavianonline.framework.reels.model.FreeGameTypeModelProvider;

import com.octavianonline.games.wheelenium.event.FreegamesEndPlateEvent;
import com.octavianonline.games.wheelenium.event.HideFreeBannerEndEvent;
import com.octavianonline.games.wheelenium.event.bonusanimate.ResetBonusValuesEvent;
import com.octavianonline.games.wheelenium.event.wheelfeature.HideWheelFeatureEvent;
import com.octavianonline.games.wheelenium.logic.WheeleniumSepartedNetLogic;
import com.octavianonline.games.wheelenium.screen.model.EndFreeGamesBannerModel;
import rx.Subscription;

import javax.inject.Inject;
import javax.inject.Named;

@Reflectable(superClasses = true, fields = false)
public class EndFreeGamesBannerScreen extends Screen<EndFreeGamesBannerModel> {

    @HasReflect
    public static final String LAYOUT_ID_PROPERTY = "EndFreeGamesBannerScreenLayoutId";
    @InjectView
    @HasReflect
    public TextView youWon1;
    @InjectView
    @HasReflect
    public TextView wonSum1;

    @InjectView
    @HasReflect
    public ViewGroup youWonPlate;
    @HasReflect
    String youWonEn = "Bonus win";
    @HasReflect
    String youWonDe = "Sie Haben";
    @HasReflect
    String youWonCs = "Bonus-výhra";
    @HasReflect
    AbstractReelGroup reelGroup;
    @HasReflect
    AbstractReelGroup reelGroup2;
    @HasReflect
    private FreeGameTypeModelProvider freeGameTypeModelProvider;
    @HasReflect
    private FreeGameMultiplierModelProvider freeGameMultiplierModelProvider;


    private Subscription endBannerUnsub;

    private Subscription hideBannerUnsub;
    @HasReflect
    private FreegamesEndPlateEvent freegamesEndPlateEvent;
    @HasReflect
    private RememberedMatrixModelProvider rememberedMatrixModelProvider;
    @HasReflect
    private ViewGroup dices;
    @HasReflect
    private ViewGroup scatters;
    @HasReflect
    ICreditsFormatter creditsFormatter;

    @Inject
    protected EndFreeGamesBannerScreen(@Named(LAYOUT_ID_PROPERTY) String layoutId, EndFreeGamesBannerModel model, IRenderer renderer, IViewManager viewManager, IAnimationFactory animationFactory, ILogger logger, IEventBus eventBus, ISoundManager soundManager, FreeGameTypeModelProvider freeGameTypeModelProvider, FreeGameMultiplierModelProvider freeGameMultiplierModelProvider, RememberedMatrixModelProvider rememberedMatrixModelProvider, ICreditsFormatter creditsFormatter) {
        super(layoutId, model, renderer, viewManager, animationFactory, logger, eventBus);
        this.freeGameTypeModelProvider = freeGameTypeModelProvider;
        this.freeGameMultiplierModelProvider = freeGameMultiplierModelProvider;

        this.freegamesEndPlateEvent = freegamesEndPlateEvent;
        this.rememberedMatrixModelProvider = rememberedMatrixModelProvider;
        this.creditsFormatter = creditsFormatter;
    }

    @Override
    protected void afterActivated() {
        super.afterActivated();
        getEventBus().register(new FreegamesEndPlateObserver(), FreegamesEndPlateEvent.class);
        reelGroup = GameEngine.current().getViewManager().findViewById("baseGameScreen", "reelGroupView");
        reelGroup2 = GameEngine.current().getViewManager().findViewById("baseGameScreen", "reelGroupView2");
        dices = GameEngine.current().getViewManager().findViewById("baseGameScreen", "dices");
        scatters = GameEngine.current().getViewManager().findViewById("baseGameScreen", "scatters");
        findViewById("endFreeGamesBannerScreen").setMasking(new RectangleViewMask(0, 0, 1920, 1080));
    }
    @HasReflect
    void resetBannerCoord() {
        youWonPlate.setX(816);
        youWonPlate.setY(692);
        youWonPlate.setAlpha(0);
        youWonPlate.setScaleX(0.2f);
        youWonPlate.setScaleY(0.2f);
        youWonPlate.setVisible(true);
    }
    @HasReflect
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    SHOW BANNER SCREEN     !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    void firstStepBannerAnimation(int step) {
        TweenViewAnimation bannerAnimation = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
        bannerAnimation.setTargetView(youWonPlate);
        endBannerUnsub = bannerAnimation.getTweenStateObservable(). subscribe(new AnimationObserver(step));
        switch (step) {
            case 1:
                bannerAnimation.setDestinationAlpha(1);
                bannerAnimation.setDestinationX(458);
                bannerAnimation.setDestinationY(-66);
                bannerAnimation.setDestinationScaleX(0.7f);
                bannerAnimation.setDestinationScaleY(0.7f);
                bannerAnimation.setTimeSpan(350);
                break;
            case 2:
                bannerAnimation.setDestinationX(244);
                bannerAnimation.setDestinationY(-16);
                bannerAnimation.setDestinationScaleX(1f);
                bannerAnimation.setDestinationScaleY(0.9f);
                bannerAnimation.setTimeSpan(350);
                break;
        }
        bannerAnimation.play();
    }
    @HasReflect
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! HIDE BANNER SCREEN !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    void HideBannerAnimation(int step) {
        TweenViewAnimation bannerAnimation = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
        bannerAnimation.setTargetView(youWonPlate);
        hideBannerUnsub = bannerAnimation.getTweenStateObservable().subscribe(new AnimationHideObserver(step));
        switch (step) {
            case 1:

                bannerAnimation.setDestinationAlpha(1);
                bannerAnimation.setDestinationX(382);
                bannerAnimation.setDestinationY(64);
                bannerAnimation.setDestinationScaleX(0.8f);
                bannerAnimation.setDestinationScaleY(0.7f);
                bannerAnimation.setTimeSpan(350);
                break;
            case 2:
                bannerAnimation.setDestinationAlpha(0f);
                bannerAnimation.setDestinationX(-110);
                bannerAnimation.setDestinationY(-332);
                bannerAnimation.setDestinationScaleX(1.5f);
                bannerAnimation.setDestinationScaleY(1.4f);
                bannerAnimation.setTimeSpan(350);
                break;
        }
        bannerAnimation.play();

    }


    @Reflectable(fields = false)
    class FreegamesEndPlateObserver extends NextObserver<FreegamesEndPlateEvent> {

        @Override
        public void onNext(FreegamesEndPlateEvent freegamesEndPlateEvent) {
            resetBannerCoord();

            String language = getModel().getTranslator().getActiveLanguageCode();

            if (!freegamesEndPlateEvent.isRetrigger()) {
                switch (language) {
                    case "EN":
                        youWon1.setText(youWonEn);

                        break;
                    case "DE":
                        youWon1.setText(youWonDe);

                        break;
                    case "CS":
                        youWon1.setText(youWonCs);

                        break;
                }
                String winValue = creditsFormatter.format(WheeleniumSepartedNetLogic.getWeelAmount());
                getModel().setProperty("win", winValue);
            } else {
                GameEngine.current().getSoundManager().stop("free_loop");
                {
                    // GameEngine.current().getSoundManager().play("free_intro");
                    new Timeout(7000, () -> {
                        GameEngine.current().getSoundManager().play("free_loop");
                    }, true);
                }
                switch (language) {
                    case "EN":
                        youWon1.setText(youWonEn);

                        break;
                    case "DE":
                        youWon1.setText(youWonDe);

                        break;
                    case "CS":
                        youWon1.setText(youWonCs);

                        break;
                }
                freegamesEndPlateEvent.setRetrigger(false);
            }

            firstStepBannerAnimation(1);
        }
    }

    @Reflectable(fields = false)
    class AnimationObserver extends NextObserver<Integer> {
        @HasReflect
        int step;

        AnimationObserver(int step) {
            this.step = step;
        }
        @HasReflect
        @Override
        public void onNext(Integer integer) {
            if (integer == TweenCallback.COMPLETE) {
                endBannerUnsub.unsubscribe();
                if (step < 2) {
                    firstStepBannerAnimation(++step);
                } else {
                    getEventBus().post(new HideWheelFeatureEvent());
                    getEventBus().post(new ResetBonusValuesEvent());
                    new Timeout(2000, () -> {
                        HideBannerAnimation(1);
                    }, true);
                }
            }
        }
    }

    @Reflectable(fields = false)
    class AnimationHideObserver extends NextObserver<Integer> {
        @HasReflect
        int step;

        AnimationHideObserver(int step) {
            this.step = step;
        }
        @HasReflect
        @Override
        public void onNext(Integer integer) {
            if (integer == TweenCallback.COMPLETE) {
                hideBannerUnsub.unsubscribe();
                if (step < 2) {
                    HideBannerAnimation(++step);
                } else {
                    youWonPlate.setVisible(false);
                    getEventBus().post(new HideFreeBannerEndEvent());

                }
            }
        }
    }


}
