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
import com.atsisa.gox.framework.screen.annotation.ExposeMethod;
import com.atsisa.gox.framework.screen.annotation.InjectView;
import com.atsisa.gox.framework.utility.Timeout;
import com.atsisa.gox.framework.utility.TimeoutCallback;
import com.atsisa.gox.framework.utility.logger.ILogger;
import com.atsisa.gox.framework.view.*;
import com.atsisa.gox.reels.*;
import com.atsisa.gox.reels.animation.ReelAnimation;
import com.atsisa.gox.reels.command.SkipCommand;
import com.atsisa.gox.reels.command.SpinCommand;
import com.atsisa.gox.reels.event.LinesModelChangedEvent;
import com.atsisa.gox.reels.event.PresentationStateChangedEvent;
import com.atsisa.gox.reels.model.ILinesModelProvider;
import com.atsisa.gox.reels.view.ReelGroupView;
import com.atsisa.gox.reels.view.ReelView;
import com.gwtent.reflection.client.HasReflect;
import com.gwtent.reflection.client.Reflectable;
import com.octavianonline.games.wheelenium.action.wheelenium.ShowStopLines;
import com.octavianonline.games.wheelenium.event.PushSymbolAnimationsInWinningLinesCommand;
import com.octavianonline.games.wheelenium.event.ResetLongSpinEvent;
import com.octavianonline.games.wheelenium.event.ShowAllHiddenSymbolsCommand;
import com.octavianonline.games.wheelenium.event.Turbo;
import com.octavianonline.games.wheelenium.event.bonusanimate.StartBonusAnimateEvent;
import com.octavianonline.games.wheelenium.event.jumpbonus.JumpBonus;
import com.octavianonline.games.wheelenium.event.jumpbonus.Skip4BonusJump;
import com.octavianonline.games.wheelenium.event.jumpbonus.StartAnimation;
import com.octavianonline.games.wheelenium.event.symbols.*;
import com.octavianonline.games.wheelenium.event.wheelfeature.ShowStopEvent;
import com.octavianonline.games.wheelenium.screen.model.SymbolsAnimationModel;
import rx.Subscription;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@Reflectable(superClasses = true, fields = false)
public class SymbolsAnimationScreen extends Screen<SymbolsAnimationModel> {

    @HasReflect
    public static final String LAYOUT_ID_PROPERTY = "SymbolsAnimationScreenLayoutId";
    private final static int FIRST_STEP = 1;
    static Map<String, Integer> winSymbolsCount = new HashMap();
    static Map<String, String> winSymbolsLongShort = new HashMap();
    static String biggetWinLineSound;

    @InjectView
    @HasReflect
    public ViewGroup WATERMELON;
    @InjectView
    @HasReflect
    public ViewGroup allSymbols;
    @InjectView
    @HasReflect
    public ViewGroup bgr;
    @InjectView
    @HasReflect
    public ViewGroup frames;
    @InjectView
    @HasReflect
    public ButtonView spinReelsArea;
    @InjectView
    @HasReflect
    public ButtonView sr1;
    @InjectView
    @HasReflect
    public ButtonView sr2;
    @InjectView
    @HasReflect
    public ButtonView sr3;
    @InjectView
    @HasReflect
    public ButtonView sr4;
    @InjectView
    @HasReflect
    public ButtonView sr5;

    @InjectView
    @HasReflect
    ViewGroup symbolsAnimationScreen;

    @InjectView
    @HasReflect
    ViewGroup BONUS;

    @HasReflect
    ICreditsFormatter creditsFormatter;
    ILinesModelProvider linesModelProvider;
    List<Iterable<String>> stoppedSymbols;
    Iterable<? extends IWinLineInfo> winLines;
    List<List<String>> listOfStoppedSymbols = new ArrayList<>();
    KeyframeAnimationView bonus;
    @InjectView
    @HasReflect
    KeyframeAnimationView longSpinView;
    ReelGroupView reelGroupView;
    ImageView reel4Bgr;
    IEventBus eventBus;
    private Subscription longSpinUnsub;
    private int steps = 0;
    private float maskHeigh = 846;
    private Subscription[] bonusSubscrs = new Subscription[5];
    private List<ButtonView> stopReelButtons = new ArrayList<>();
    private PresentationStateChangedEvent presentationState;

    @Inject
    protected SymbolsAnimationScreen(@Named(LAYOUT_ID_PROPERTY) String layoutId, SymbolsAnimationModel model, IRenderer renderer, IViewManager viewManager, IAnimationFactory animationFactory, ILogger logger, IEventBus eventBus, ISoundManager soundManager, ICreditsFormatter creditsFormatter, ILinesModelProvider linesModelProvider) {
        super(layoutId, model, renderer, viewManager, animationFactory, logger, eventBus);
        this.creditsFormatter = creditsFormatter;
        this.linesModelProvider = linesModelProvider;
        this.eventBus = eventBus;
    }

//    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Long spin animation !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    public static Map<String, Integer> getWinSymbolsCount() {
        return winSymbolsCount;
    }

    public static Map<String, String> getWinSymbolsLongShort() {
        return winSymbolsLongShort;
    }

    public static String getBiggetWinLineSound() {
        return biggetWinLineSound;
    }

    @Override
    protected void afterActivated() {
        super.afterActivated();
        getEventBus().register(new StartWinAnimationObserver(), StartWinAnimationEvent.class);
        getEventBus().register(new PushSymbolAnimationsInWinningLinesCommandObserver(), PushSymbolAnimationsInWinningLinesCommand.class);
        getEventBus().register(new PresentationStateChangedEventObserver(), PresentationStateChangedEvent.class);
        getEventBus().register(new PlayLongAnimationObserver(), PlayLongAnimation.class);
        getEventBus().register(new StopLongAnimationObserver(), StopLongAnimation.class);
        getEventBus().register(new JumpBonusObserver(), JumpBonus.class);
        getEventBus().register(new Skip4BonusJumpObserver(), Skip4BonusJump.class);
        getEventBus().register(new LinesModelChangedEventObserver(), LinesModelChangedEvent.class);
        getEventBus().register(new StartBonusAnimateObserver(), StartBonusAnimateEvent.class);
        getEventBus().register(new ShowStopLinesObserver(), ShowStopEvent.class);
        getEventBus().register(new ShowAllHiddenSymbolsObserver(), ShowAllHiddenSymbolsCommand.class);
        getEventBus().register(new ResetLongSpinObserver(), ResetLongSpinEvent.class);
        if (GameEngine.current().getGameConfiguration().getResolution().name().equals("FHD_TWO_MONITORS")) {
            bonus = GameEngine.current().getViewManager().findViewById("payTableScreen", "bonus");
            this.stopReelButtons = Arrays.asList(sr1, sr2, sr3, sr4, sr5);
        }

        reelGroupView = GameEngine.current().getViewManager().findViewById("baseGameScreen", "reelGroupView");
        reel4Bgr = GameEngine.current().getViewManager().findViewById("baseGameScreen", "reel4Bgr");

        if (!GameEngine.current().getGameConfiguration().getResolution().name().equals("FHD_TWO_MONITORS")) {
            symbolsAnimationScreen.setMasking(new RectangleViewMask(0, -20, 1920, 1100));
        } else {
            symbolsAnimationScreen.setMasking(new RectangleViewMask(0, 0, 1920, 1080));
        }
        if (GameEngine.current().getGameConfiguration().getResolution().name().equals("FHD_TWO_MONITORS")) {
            maskHeigh = 846;
        } else {
            maskHeigh = 846;
        }

    }


    private class StartBonusAnimateObserver extends NextObserver<StartBonusAnimateEvent> {

        @Override
        public void onNext(StartBonusAnimateEvent startBonusAnimateEvent) {
            allSymbols.getChildren().forEach(view -> {
                ((ViewGroup) view).getChildren().forEach(view1 -> {
                    ((KeyframeAnimationView) view1).setVisible(false);
                    ((KeyframeAnimationView) view1).stop();
                });
            });


            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 3; j++) {
                    String bonus = reelGroupView.getReel(i).getDisplayedSymbol(j).getName();
                    if (bonus.equals("BONUS")) {
                        reelGroupView.getReel(i).getDisplayedSymbol(j).setVisible(false);
                        KeyframeAnimationView bonusAnim = ((KeyframeAnimationView) findViewById("" + i + j + "LongBONUS"));
                        bonusAnim.setVisible(true);
                        bonusAnim.play();
                    }
                }
            }

            if (GameEngine.current().getGameConfiguration().getResolution().name().equals("FHD_TWO_MONITORS")) {
                ((KeyframeAnimationView) GameEngine.current().getViewManager().findViewById("payTableScreen", "bonus")).play();
            }

        }
    }

    public void handleLinesModelChangedEvent(LinesModelChangedEvent event) {
        switch (event.getChangeType()) {
            case LinesModelChangedEvent.CURRENT_WINNING_LINE:
                frames.getChildren().forEach(view -> {
                    view.setVisible(false);
                    ((KeyframeAnimationView) view).stop();
                });

                bgr.getChildren().forEach(view -> {
                    view.setVisible(false);
                });

                List<Integer> positions = new ArrayList<>();
                Optional<IWinLineInfo> lineinfo = event.getLinesModel().getCurrentWinningLine();
                lineinfo.ifPresent(iWinLineInfo -> {
                    Iterable<Integer> linePosition = iWinLineInfo.getPositions();
                    linePosition.forEach(positions::add);
                    for (int i = 0; i < 5; i++) {
                        if (positions.get(i) >= 0) {
                            KeyframeAnimationView frame = findViewById(i + "" + positions.get(i) + "frame");
                            frame.setVisible(true);
                            frame.play();

                            ImageView plug = findViewById(i + "" + positions.get(i) + "plug");
                            plug.setVisible(true);
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    void formWinLines() {
        winLines = linesModelProvider.getLinesModel().getWinningLines();
    }

    private void handlePushSymbolAnimationsInWinningLinesCommand(PushSymbolAnimationsInWinningLinesCommand command) {
        stoppedSymbols = command.getStoppedSymbols();
    }

    private void handlePresentationStateChangedEvent(PresentationStateChangedEvent presentationStateChangedEvent) {
        if (spinReelsArea != null && spinReelsArea.isVisible()) {
            spinReelsArea.setVisible(false);
        }

        switch (presentationStateChangedEvent.getStateName()) {
            case ReelsPresentationStates.IDLE:
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 3; j++) {
                        String bonus = reelGroupView.getReel(i).getDisplayedSymbol(j).getName();
                        if (bonus.equals("BONUS")) {
//                            reelGroupView.getReel(i).getDisplayedSymbol(j).setState("IDLE");
                            KeyframeAnimationView bonusAnim = ((KeyframeAnimationView) findViewById("" + i + j + "LongBONUS"));
                            bonusAnim.setVisible(true);
                            bonusAnim.play();
                        }
                    }
                }


                allSymbols.getChildren().forEach(view -> {
                    ((ViewGroup) view).getChildren().forEach(view1 -> {
                        ((KeyframeAnimationView) view1).setVisible(false);
                        ((KeyframeAnimationView) view1).stop();
                    });
                });

                bgr.getChildren().forEach(bgr -> {
                    bgr.setVisible(false);
                });

                frames.getChildren().forEach(frame -> {
                    frame.setVisible(false);
                    ((KeyframeAnimationView) frame.setVisible(false)).stop();
                });

                if (spinReelsArea != null && !spinReelsArea.isVisible()) {
                    spinReelsArea.setVisible(true);
                }
                if (bonus != null) {
                    bonus.stop();
                }
                getEventBus().post(new StopAnimationCommanEvent());
                break;
            case ReelsPresentationStates.RUNNING_REELS:
                if (!GameEngine.current().getGameConfiguration().getResolution().name().equals("FHD_TWO_MONITORS")) {
                    if (!stopReelButtons.isEmpty()) {
                        stopReelButtons.forEach(rectangleShapeView -> {
                            if (rectangleShapeView.isVisible()) {
                                rectangleShapeView.setVisible(false);
                            }
                        });
                    }
                }
                break;
            default:
        }
    }

    TweenViewAnimation bannerAnimation;
    void framelongSpinStepsAnim() {
        if(bannerAnimation!=null && bannerAnimation.isPlaying()){
            bannerAnimation.stop();
        }
        bannerAnimation = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
        bannerAnimation.setTargetView(longSpinView);
        longSpinUnsub = bannerAnimation.getTweenStateObservable().subscribe(new AnimationObserver());

        TweenViewAnimation reelAnim = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
        reelAnim.setTargetView(reelGroupView.getReel(4));

        TweenViewAnimation reel4BgrAnim = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
        reel4BgrAnim.setTargetView(reel4Bgr);

        switch (steps) {
            case 0:

                View base = GameEngine.current().getViewManager().getLayout("baseGameScreen").getRootView();//.setDepth(1000);
                base.setDepth(8);

                View symbolsAnimation = GameEngine.current().getViewManager().getLayout("symbolsAnimationScreen").getRootView();//.setDepth(1000);
                symbolsAnimation.setDepth(9);
                new Timeout(100, new SetMaskForLongSpin(), true);
                reelAnim.setDestinationScaleY(1.2f);
                reelAnim.setDestinationScaleX(1.2f);
                reelAnim.setTimeSpan(250);
                reel4BgrAnim.setDestinationScaleY(1.2f);
                reel4BgrAnim.setDestinationScaleX(1.2f);
                reel4BgrAnim.setTimeSpan(250);

                longSpinView.setVisible(true);
                longSpinView.play();
                bannerAnimation.setDestinationY(510);
                bannerAnimation.setDestinationScaleX(1.9f);
                bannerAnimation.setDestinationScaleY(2.2f);
                bannerAnimation.setTimeSpan(250);
                break;
            case 1:
                bannerAnimation.setDestinationY(440);
                bannerAnimation.setDestinationScaleX(1.5f);
                bannerAnimation.setDestinationScaleY(1.8f);
                bannerAnimation.setTimeSpan(250);

                reelAnim.setDestinationScaleY(1f);
                reelAnim.setDestinationScaleX(1f);
                reelAnim.setTimeSpan(250);

                reel4BgrAnim.setDestinationScaleY(1f);
                reel4BgrAnim.setDestinationScaleX(1f);
                reel4BgrAnim.setTimeSpan(250);

                break;
        }
        bannerAnimation.play();
        reelAnim.play();
        reel4BgrAnim.play();
    }

    void resetlongSpinParameters() {
        if (GameEngine.current().getGameConfiguration().getResolution().name().equals("FHD_TWO_MONITORS")) {
            reelGroupView.setMasking(new RectangleViewMask(-84, 0, 1570, 846));
            maskHeigh = 846;
        } else {
            reelGroupView.setMasking(new RectangleViewMask(-84, -20, 1570, 866));
            maskHeigh = 846;
        }

        reelGroupView.getReel(4).setScaleX(1f);
        reelGroupView.getReel(4).setScaleY(1f);
        reel4Bgr.setScaleX(1f);
        reel4Bgr.setScaleY(1f);

        longSpinView.setScaleX(1.5f);
        longSpinView.setScaleY(1.8f);
        longSpinView.setY(440);
        longSpinView.stop();
        longSpinView.setVisible(false);
        steps = 0;
    }

    void jumpBonusAnim(String id, int reelPosition, int step) {

        KeyframeAnimationView bonus = findViewById(id);

        if (reelPosition == 4 && step == 0) {

            if (!GameEngine.current().getGameConfiguration().getResolution().name().equals("FHD_TWO_MONITORS")) {
                bonus.setScaleX(2.12f);
                bonus.setScaleX(2.12f);
            } else {
                bonus.setScaleX(1.2f);
                bonus.setScaleX(1.2f);
            }
        }
        if (step == 0) {
            bonus.setVisible(true);
            bonus.play();
        }
        TweenViewAnimation jump = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
        bonusSubscrs[reelPosition] = jump.getTweenStateObservable().subscribe(new BonusJumpObserver(id, reelPosition, step));
        if (reelPosition == 0 && step == 0) {
            GameEngine.current().getSoundManager().play("scatterStop1");
        }

        if (reelPosition == 2 && step == 0) {
            GameEngine.current().getSoundManager().play("scatterStop2");
        }

        if (step == 0) {

            if (reelPosition == 4) {
                reelGroupView.getReel(4).getDisplayedSymbols().forEach(abstractSymbol -> {
                    if (abstractSymbol.getName().equals("BONUS")) {
                        GameEngine.current().getSoundManager().play("scatterStop3");
                    }
                });

                if (!GameEngine.current().getGameConfiguration().getResolution().name().equals("FHD_TWO_MONITORS")) {
                    jump.setDestinationScaleX(3.717f);
                    jump.setDestinationScaleY(3.717f);
                } else {
                    jump.setDestinationScaleX(2.1f);
                    jump.setDestinationScaleY(2.1f);
                }

            } else {
                if (!GameEngine.current().getGameConfiguration().getResolution().name().equals("FHD_TWO_MONITORS")) {
                    jump.setDestinationScaleX(2.832f);
                    jump.setDestinationScaleY(2.832f);
                } else {
                    jump.setDestinationScaleX(1.6f);
                    jump.setDestinationScaleY(1.6f);
                }


            }
            jump.setTargetView(bonus);
            jump.setTimeSpan(270);

        } else if (step == 1) {
            jump.setTargetView(bonus);
            jump.setTimeSpan(270);
            if (!GameEngine.current().getGameConfiguration().getResolution().name().equals("FHD_TWO_MONITORS")) {
                jump.setDestinationScaleX(1.77f);
                jump.setDestinationScaleY(1.77f);
            } else {
                jump.setDestinationScaleX(1f);
                jump.setDestinationScaleY(1f);
            }
        }
        jump.play();
    }

    @ExposeMethod
    public void stopReel(int index) {
        if (index == 4) {
            WheeleniumBaseGameScreen.reel4Skip = true;
        }

        this.stopReelButtons.get(index).setVisible(false);
        final ReelView reel = (ReelView) reelGroupView.getReel(index);
        final AbstractReelGame game = (AbstractReelGame) GameEngine.current().getGame();
        final List<Iterable<String>> stoppedSymbols = game.getReelGameStateHolder().getStoppedSymbols();
        ReelAnimation reelAnimation = (ReelAnimation) reelGroupView.getReel(index).getReelAnimation();
        reelAnimation.setSpeedIncrease(10);
        reelAnimation.speedUp();

        if (reel.getReelState().name().equals("SPINNING")) {
            reel.forceStopOnSymbols(stoppedSymbols.get(index));
        }
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!! BONUS JUMP ANIMATION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    @ExposeMethod
    public void spin() {
        getEventBus().post(new SpinCommand());
    }

    public void reset4jumpAnimation() {


        getEventBus().post(new StartAnimation());
    }

    private class LinesModelChangedEventObserver extends NextObserver<LinesModelChangedEvent> {
        @Override
        public void onNext(LinesModelChangedEvent event) {
            handleLinesModelChangedEvent(event);
        }
    }

    class StartWinAnimationObserver extends NextObserver<StartWinAnimationEvent> {

        @Override
        public void onNext(StartWinAnimationEvent startWinAnimationEvent) {
            winSymbolsCount = new HashMap<>();
            winSymbolsLongShort = new HashMap<>();
            formWinLines();

            List<String> list;
            listOfStoppedSymbols = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                list = new ArrayList<>();
                Iterable<String> reelSymbols = stoppedSymbols.get(i);
                reelSymbols.forEach(list::add);
                listOfStoppedSymbols.add(list);
            }

            boolean biggerFirstLine = false;
            for (IWinLineInfo lineInfo : winLines) {
                String animationname = lineInfo.getAnimationName();
                Iterable<Integer> positions = lineInfo.getPositions();
                List<Integer> listOfPositions = new ArrayList<>();
                positions.forEach(listOfPositions::add);
                String symbolName = "";
                int symbolCount = 0;

                for (int j = 0; j < 5; j++) {
                    int pos = listOfPositions.get(j);
                    if (pos >= 0) {
                        symbolCount++;
                        symbolName = listOfStoppedSymbols.get(j).get(pos);
                        KeyframeAnimationView animationView = findViewById(j + "" + pos + animationname + symbolName);
                        animationView.setVisible(true);
                        animationView.play();
                        if (symbolName.equals("BONUS") && bonus != null) {
                            bonus.play();
                        }
                    }
                }
                winSymbolsCount.put(symbolName, symbolCount);

                if (!biggerFirstLine) {
                    biggerFirstLine = true;
                    biggetWinLineSound = symbolName + animationname;
                }
                winSymbolsLongShort.put(symbolName, animationname);
            }
            getEventBus().post(new PlayAnimationCommanEvent());
        }

    }

    private class PushSymbolAnimationsInWinningLinesCommandObserver extends NextObserver<PushSymbolAnimationsInWinningLinesCommand> {

        @Override
        public void onNext(PushSymbolAnimationsInWinningLinesCommand command) {
            handlePushSymbolAnimationsInWinningLinesCommand(command);
        }
    }

    private class PresentationStateChangedEventObserver extends NextObserver<PresentationStateChangedEvent> {

        @Override
        public void onNext(PresentationStateChangedEvent presentationStateChangedEvent) {
            handlePresentationStateChangedEvent(presentationStateChangedEvent);
            presentationState = presentationStateChangedEvent;
        }
    }


    class PlayLongAnimationObserver extends NextObserver<PlayLongAnimation> {

        @Override
        public void onNext(PlayLongAnimation playLongAnimation) {
            steps = FIRST_STEP;
            resetlongSpinParameters();
            GameEngine.current().getSoundManager().play("longSpinBonus");
            framelongSpinStepsAnim();
        }
    }

    class SetMaskForLongSpin implements TimeoutCallback {

        @Override
        public void onTimeout() {
            if (GameEngine.current().getGameConfiguration().getResolution().name().equals("FHD_TWO_MONITORS")) {
                if (maskHeigh < 990) {
                    maskHeigh += 25;
                    new Timeout(25, new SetMaskForLongSpin(), true);
                } else {
                    maskHeigh = 990;
                }
                reelGroupView.setMasking(new RectangleViewMask(-84, 0, 1570, maskHeigh));
            } else {
                if (maskHeigh < 990) {
                    maskHeigh += 15;
                    new Timeout(25, new SetMaskForLongSpin(), true);
                } else {
                    maskHeigh = 990;
                }
                reelGroupView.setMasking(new RectangleViewMask(-84, -20, 1570, maskHeigh + 20));
            }
        }
    }

    class StopLongAnimationObserver extends NextObserver<StopLongAnimation> {

        @Override
        public void onNext(StopLongAnimation stopLongAnimation) {
            steps = 1;
            framelongSpinStepsAnim();
        }
    }

    @Reflectable(fields = false)
    class AnimationObserver extends NextObserver<Integer> {

        @HasReflect
        @Override
        public void onNext(Integer integer) {
            if (integer == TweenCallback.COMPLETE) {
                longSpinUnsub.unsubscribe();

                if (steps == 0) {
/*                        View base = GameEngine.current().getViewManager().getLayout("baseGameScreen").getRootView();//.setDepth(1000);
                        base.setDepth(10000);*/
                }

                if (steps == 1) {//end long spin animation
                    longSpinView.setVisible(false);
                    GameEngine.current().getSoundManager().stop("longSpinBonus");
                    longSpinView.stop();
                    resetlongSpinParameters();

                    View base = GameEngine.current().getViewManager().getLayout("baseGameScreen").getRootView();//.setDepth(1000);
                    base.setDepth(0);
                    View symbolsAnimation = GameEngine.current().getViewManager().getLayout("symbolsAnimationScreen").getRootView();//.setDepth(1000);
                    symbolsAnimation.setDepth(1);
                    getEventBus().post(new StartAnimation());
                }
            }
        }
    }


    class ResetLongSpinObserver extends NextObserver<ResetLongSpinEvent> {

        @Override
        public void onNext(ResetLongSpinEvent resetLongSpinEvent) {
            resetLongSpin();
        }
    }

    public void resetLongSpin(){
        if(bannerAnimation!=null && bannerAnimation.isPlaying()){
            bannerAnimation.stop();
        }

        longSpinView.setVisible(false);
        GameEngine.current().getSoundManager().stop("longSpinBonus");
        longSpinView.stop();
        resetlongSpinParameters();

        View base = GameEngine.current().getViewManager().getLayout("baseGameScreen").getRootView();//.setDepth(1000);
        base.setDepth(0);
        View symbolsAnimation = GameEngine.current().getViewManager().getLayout("symbolsAnimationScreen").getRootView();//.setDepth(1000);
        symbolsAnimation.setDepth(1);
        reelGroupView.getReel(4).setScaleX(1f);
        reelGroupView.getReel(4).setScaleY(1f);
        reel4Bgr.setScaleX(1f);
        reel4Bgr.setScaleY(1f);
        getEventBus().post(new StartAnimation());

    }

    class JumpBonusObserver extends NextObserver<JumpBonus> {

        @Override
        public void onNext(JumpBonus jumpBonus) {
            jumpBonusAnim(jumpBonus.getBunusID(), jumpBonus.getReelPosition(), 0);

        }
    }

    class BonusJumpObserver extends NextObserver<Integer> {
        String id;
        int reelPosition;
        int step;

        BonusJumpObserver(String id, int reelPosition, int step) {
            this.id = id;
            this.reelPosition = reelPosition;
            this.step = step;
        }

        @Override
        public void onNext(Integer integer) {
            if (integer == TweenCallback.COMPLETE) {
                bonusSubscrs[reelPosition].unsubscribe();
                if (step == 1 && reelPosition == 4) {
                    eventBus.post(new StopLongAnimation());
                }
                if (step < 1) {
                    jumpBonusAnim(id, reelPosition, ++step);
                } else {
                    KeyframeAnimationView bonusShort = ((KeyframeAnimationView) findViewById(id));
                    bonusShort.stop();
                    bonusShort.setVisible(false);
                }

            }
        }
    }

    class Skip4BonusJumpObserver extends NextObserver<Skip4BonusJump> {

        @Override
        public void onNext(Skip4BonusJump skip4BonusJump) {
            eventBus.post(new StopLongAnimation());
        }
    }

    private class ShowStopLinesObserver extends NextObserver<ShowStopEvent> {

        @Override
        public void onNext(ShowStopEvent showStopLines) {
            if (GameEngine.current().getGameConfiguration().getResolution().name().equals("FHD_TWO_MONITORS")) {
                stopReelButtons.forEach(buttonView -> {
                    buttonView.setVisible(true);
                });
            }
        }
    }


    private class ShowAllHiddenSymbolsObserver extends NextObserver<ShowAllHiddenSymbolsCommand> {

        @Override
        public void onNext(ShowAllHiddenSymbolsCommand showAllHiddenSymbolsCommand) {
            BONUS.getChildren().forEach(view -> {
                if (((KeyframeAnimationView) view).isVisible()) {
                    ((KeyframeAnimationView) view).stop();
                    ((KeyframeAnimationView) view).setVisible(false);
                }
            });
        }
    }

    @ExposeMethod
    @HasReflect
    public void stopReels() {
        String name = presentationState.getStateName();
        if (name.equals("StoppingReels")) {
            getEventBus().post(new SkipCommand());
        }
    }

}
