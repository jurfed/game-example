package com.octavianonline.games.wheelenium.screen;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.animation.IAnimationFactory;
import com.atsisa.gox.framework.command.HideScreenCommand;
import com.atsisa.gox.framework.event.IEventListener;
import com.atsisa.gox.framework.event.InputEvent;
import com.atsisa.gox.framework.event.InputEventType;
import com.atsisa.gox.framework.eventbus.IEventBus;
import com.atsisa.gox.framework.eventbus.NextObserver;
import com.atsisa.gox.framework.eventbus.annotation.Subscribe;
import com.atsisa.gox.framework.infrastructure.IViewManager;
import com.atsisa.gox.framework.infrastructure.SoundManager;
import com.atsisa.gox.framework.rendering.IRenderer;
import com.atsisa.gox.framework.screen.annotation.ExposeMethod;
import com.atsisa.gox.framework.screen.annotation.InjectView;
import com.atsisa.gox.framework.screen.model.ScreenModel;
import com.atsisa.gox.framework.utility.logger.ILogger;
import com.atsisa.gox.framework.view.*;
import com.atsisa.gox.reels.AbstractReelGame;
import com.atsisa.gox.reels.ICreditsFormatter;
import com.atsisa.gox.reels.IWinLineInfo;
import com.atsisa.gox.reels.ReelsPresentationStates;
import com.atsisa.gox.reels.animation.IReelAnimation;
import com.atsisa.gox.reels.animation.ReelAnimation;
import com.atsisa.gox.reels.command.*;
import com.atsisa.gox.reels.event.LinesModelChangedEvent;
import com.atsisa.gox.reels.event.PresentationStateChangedEvent;
import com.atsisa.gox.reels.fsm.IReelGameStateHolder;
import com.atsisa.gox.reels.screen.BaseGameScreen;
import com.atsisa.gox.reels.screen.model.BottomPanelScreenModel;
import com.atsisa.gox.reels.view.AbstractReel;
import com.atsisa.gox.reels.view.AbstractSymbol;
import com.atsisa.gox.reels.view.ReelGroupView;
import com.atsisa.gox.reels.view.ReelView;
import com.atsisa.gox.reels.view.state.ReelState;
import com.gwtent.reflection.client.HasReflect;
import com.gwtent.reflection.client.Reflectable;
import com.gwtent.reflection.client.annotations.Reflect_Full;
//import com.octavianonline.framework.core.events.SetTurboModeOffEvent;
//import com.octavianonline.framework.core.events.SetTurboModeOnEvent;
import com.octavianonline.framework.musical.autoplayer.AutoPlayer;
import com.octavianonline.framework.reels.event.*;
import com.octavianonline.framework.reels.service.ReelsService;
import com.octavianonline.framework.risk.card.logic.CardRiskPresentation;
import com.octavianonline.games.wheelenium.action.wheelenium.WaitForBonus;
import com.octavianonline.games.wheelenium.event.ShowAllHiddenSymbolsCommand;
import com.octavianonline.games.wheelenium.event.Turbo;
import com.octavianonline.games.wheelenium.event.html.ShowWinSumForHtml;
import com.octavianonline.games.wheelenium.event.jumpbonus.JumpBonus;
import com.octavianonline.games.wheelenium.event.jumpbonus.Skip4BonusJump;
import com.octavianonline.games.wheelenium.event.symbols.FindLongSpinEvent;
import com.octavianonline.games.wheelenium.event.symbols.PlayLongAnimation;
import com.octavianonline.games.wheelenium.event.wheelfeature.HideButtonsEvent;
import com.octavianonline.games.wheelenium.event.wheelfeature.ShowStopEvent;
import rx.Observable;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * Represents the base game screen
 */
@Reflectable(superClasses = true, fields = false)
public class WheeleniumBaseGameScreen extends BaseGameScreen {

    /**
     * Flag: is debug button visible
     */
    private final String DEBUG_BUTTON_VISIBLE = "debugButtonVisible";
    @Inject
    public ReelsService reelsHelper;
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
    public ReelGroupView reelGroupView;
    public AutoPlayer autoPlayer;
    @InjectView
    @HasReflect
    public ButtonView fast;
    public float speed = 2.5f;
    long startSpinTime;
    IEventBus eventBus;
    boolean firstReelBonus = false;
    private List<ButtonView> stopReelButtons = new ArrayList<>();
    private String resolution;
    private int bonusCount = 0;
    private rx.Observable<ReelState>[] reelStateObservable = new Observable[5];

    @HasReflect
    ICreditsFormatter creditsFormatter;

    @HasReflect
    private BottomPanelScreenModel bottomPanelScreenModel;

    /**
     * Initializes a new instance of the {@link WheeleniumBaseGameScreen} class.
     *
     * @param layoutId         layout identifier
     * @param model            {@link ScreenModel}
     * @param renderer         {@link IRenderer}
     * @param viewManager      {@link IViewManager}
     * @param animationFactory {@link IAnimationFactory}
     * @param logger           {@link ILogger}
     * @param eventBus         {@link IEventBus}
     */
    @Inject
    public WheeleniumBaseGameScreen(@Named(LAYOUT_ID_PROPERTY) String layoutId, ScreenModel model, IRenderer renderer, IViewManager viewManager, IAnimationFactory animationFactory,
                                    ILogger logger, IEventBus eventBus, IReelGameStateHolder reelGameStateHolder, BottomPanelScreenModel bottomPanelScreenModel, ICreditsFormatter creditsFormatter) {
        super(layoutId, model, renderer, viewManager, animationFactory, logger, eventBus, reelGameStateHolder);
        this.bottomPanelScreenModel = bottomPanelScreenModel;
        this.eventBus = eventBus;
        this.creditsFormatter = creditsFormatter;
    }


    @Override
    @Subscribe
    public void handleStopReelsCommand(StopReelsCommand stopReelsCommand) {
        super.handleStopReelsCommand(stopReelsCommand);
    }

    @Override
    protected void afterActivated() {
        super.afterActivated();
        this.stopReelButtons = Arrays.asList(sr1, sr2, sr3, sr4, sr5);

        resolution = GameEngine.current().getGameConfiguration().getResolution().name();
        getEventBus().post(new ReelSpeedChangeEvent(0, speed));
        getEventBus().post(new ApplyReelsRotationTimeEvent(new ArrayList() {{
            add(500);
            add(800);
            add(1100);
            add(1400);
            add(1700);
        }}));


        setSounds();
        if (GameEngine.current().getGameConfiguration().getResolution().name().equals("HD_MOBILE")) {
            reelGroupView.setMasking(new RectangleViewMask(-84, -5, 1570, 846));
        } else if (GameEngine.current().getGameConfiguration().getResolution().name().equals("HD")) {
            reelGroupView.setMasking(new RectangleViewMask(-84, 0, 1570, 861));
        } else {
            reelGroupView.setMasking(new RectangleViewMask(-84, 0, 1570, 846));
        }

        for (int i = 0; i < reelGroupView.getChildCount(); i++) {
            reelStateObservable[i] = ((ReelView) reelGroupView.getReel(i)).getReelStateObservable();
            reelStateObservable[i].subscribe(new ReelsNextObserver(i));
        }

        TextView collectText = new TextView();
        collectText.setFontName("big_win_font");
        getEventBus().register(new FindLongSpinObserver(), FindLongSpinEvent.class);
        getEventBus().register(new SkipButtonClickedObserver(), SkipCommand.class);
        getEventBus().register(new TurboObserver(), Turbo.class);
        getEventBus().register(new SetTurboModeOnObserver(), SetTurboModeOnEvent.class);
        getEventBus().register(new SetTurboModeOffObserver(), SetTurboModeOffEvent.class);
        getEventBus().register(new StopAutoPlayCommandObserver(), StopAutoPlayCommand.class);
        getEventBus().register(new ShowStopLinesObserver(), ShowStopEvent.class);
        getEventBus().register(new HideButtonsObserver(), HideButtonsEvent.class);
        getEventBus().register(new ShowWinSumForHtmlObserver(), ShowWinSumForHtml.class);
        getEventBus().register(new ShowCardRiskPresentation(), CardRiskPresentation.class);
        getEventBus().register(new ResetGameCommandHandler(), ResetGameCommand.class);


/*        if (GameEngine.current().getGameConfiguration().getResolution().name().equals("HD_MOBILE")) {
            ButtonView spinMobile = GameEngine.current().getViewManager().findViewById("controlPanelScreen", "spinMobile");
            spinMobile.addEventListener(new ButtonViewReleasedListener());

            ButtonView skinMobile = GameEngine.current().getViewManager().findViewById("controlPanelScreen", "skipButton");
            skinMobile.addEventListener(new ButtonViewReleasedSkipListener());
        }else if(GameEngine.current().getGameConfiguration().getResolution().name().equals("HD")){
            ButtonView spinMobile = GameEngine.current().getViewManager().findViewById("controlPanelScreen", "spinButton");
            spinMobile.addEventListener(new ButtonViewReleasedListener());

            ButtonView skinMobile = GameEngine.current().getViewManager().findViewById("controlPanelScreen", "skipButton");
            skinMobile.addEventListener(new ButtonViewReleasedSkipListener());
        }*/


    }


    @Reflect_Full
    public class ButtonViewReleasedListener implements IEventListener<InputEvent> {

        @Override
        public void onEvent(InputEvent event) {
            getLogger().error("ButtonViewReleasedListener");
            System.out.println(event.getType().name());
            if (InputEventType.TOUCH_START.name().equals(event.getType().name())) {
                GameEngine.current().getSoundManager().play("spin");

            }
        }
    }

    @Reflect_Full
    public class ButtonViewReleasedSkipListener implements IEventListener<InputEvent> {

        @Override
        public void onEvent(InputEvent event) {
            getLogger().error("ButtonViewReleasedListener");
            System.out.println(event.getType().name());
            if (InputEventType.TOUCH_START.name().equals(event.getType().name())) {
                GameEngine.current().getSoundManager().play("skip");

            }
        }
    }

    private void setSounds() {
        if (resolution.equals("FHD_TWO_MONITORS")) {
            if (autoPlayer == null) {
                HashMap<Integer, String> autoSpinSounds = new HashMap<>();
                autoSpinSounds.put(1, "Spin1_auto");
                autoSpinSounds.put(2, "Spin2_auto");
                autoSpinSounds.put(3, "Spin3_auto");
                autoSpinSounds.put(4, "Spin4_auto");
                autoSpinSounds.put(5, "Spin5_auto");
                autoSpinSounds.put(6, "Spin6_auto");
                autoSpinSounds.put(7, "Spin7_auto");
                autoSpinSounds.put(8, "Spin8_auto");
                autoSpinSounds.put(9, "Spin9_auto");
                autoSpinSounds.put(10, "Spin10_auto");
                autoSpinSounds.put(11, "Spin11_auto");
                autoSpinSounds.put(12, "Spin12_auto");
                autoSpinSounds.put(13, "Spin13_auto");
                autoSpinSounds.put(14, "Spin14_auto");
                autoSpinSounds.put(15, "Spin15_auto");
                autoSpinSounds.put(16, "Spin16_auto");

                HashMap<Integer, String> singleSpinSounds = new HashMap<>();
                singleSpinSounds.put(1, "Spin1");
                singleSpinSounds.put(2, "Spin2");
                singleSpinSounds.put(3, "Spin3");
                singleSpinSounds.put(4, "Spin4");
                singleSpinSounds.put(5, "Spin5");
                singleSpinSounds.put(6, "Spin6");
                singleSpinSounds.put(7, "Spin7");
                singleSpinSounds.put(8, "Spin8");
                singleSpinSounds.put(9, "Spin9");
                singleSpinSounds.put(10, "Spin10");
                singleSpinSounds.put(11, "Spin11");
                singleSpinSounds.put(12, "Spin12");
                singleSpinSounds.put(13, "Spin13");
                singleSpinSounds.put(14, "Spin14");
                singleSpinSounds.put(15, "Spin15");
                singleSpinSounds.put(16, "Spin16");
                autoPlayer = new AutoPlayer(getEventBus(), getViewManager(), GameEngine.current().getSoundManager(), singleSpinSounds, autoSpinSounds);
            }
        }
    }


    private IReelAnimation configReels() {
        IReelAnimation reelAnimation = new ReelAnimation();
        ((ReelAnimation) reelAnimation).setSpeedIncrease(8f);
        return reelAnimation;
    }

    @Override
    protected void registerEvents() {
        super.registerEvents();
    }


    //   @Subscribe
    public void handleLinesModelChangedEvent(LinesModelChangedEvent event) {
        switch (event.getChangeType()) {
            case LinesModelChangedEvent.CURRENT_WINNING_LINE:
                if (event.getLinesModel().getCurrentWinningLine().isPresent() && event.getLinesModel().getCurrentWinningLine().get().getLineNumber() != 0) {
                    this.hideSymbolsUnderWinLine(event.getLinesModel().getCurrentWinningLine().get());
                } else {
                    this.hideSymbolsUnderWinLine(null);
                }
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void handleHideAnimationUnderWinLineCommand(HideAnimationUnderWinLineCommand event) {
        this.hideSymbolsUnderWinLine(event.getWinLineInfo());
    }

    @Subscribe
    public void handleShowAllHiddenSymbolsCommand(ShowAllHiddenSymbolsCommand command) {
        reelGroupView.getReels().forEach(abstractReel -> abstractReel.getDisplayedSymbols().forEach(abstractSymbol -> {
                    if (!abstractSymbol.isVisible()) {
                        abstractSymbol.setVisible(true);
                    }
                })
        );
    }

    @Subscribe
    public void handlePresentationStateChangedEvent(PresentationStateChangedEvent event) {
        if (!event.getStateName().equals(ReelsPresentationStates.IDLE)) {
            bottomPanelScreenModel.setProperty("spinVisible", false);
            bottomPanelScreenModel.setProperty("spinEnable", false);
        }

        if (event.getStateName().equals(ReelsPresentationStates.RUNNING_REELS)) {
            bottomPanelScreenModel.setProperty("skipVisible", false);

        }

        if (spinReelsArea != null && spinReelsArea.isVisible()) {
            spinReelsArea.setVisible(false);
        }
        if (!stopReelButtons.isEmpty()) {
            stopReelButtons.forEach(rectangleShapeView -> {
                if (rectangleShapeView.isVisible()) {
                    rectangleShapeView.setVisible(false);
                }
            });
        }


        switch (event.getStateName()) {
            case ReelsPresentationStates.IDLE:
                if (spinReelsArea != null && !spinReelsArea.isVisible()) {
                    spinReelsArea.setVisible(true);
                }
                break;
            case ReelsPresentationStates.RUNNING_REELS:
                bonusCount = 0;
                skip = false;
                reel4Skip = false;
                bonusPosition = new HashMap<>();
                firstReelBonus = false;
                if (!resolution.equals("FHD_TWO_MONITORS")) {
                    bottomPanelScreenModel.setProperty("formattedWin2", "");
                    bottomPanelScreenModel.setProperty("winVisible2", false);
                }
                break;
            default:
                break;
        }
    }

    private void hideSymbolsUnderWinLine(IWinLineInfo iWinLineInfo) {
        if (iWinLineInfo == null) {
            reelGroupView.getReels().forEach(abstractReel -> abstractReel.getDisplayedSymbols().forEach(abstractSymbol -> abstractSymbol.setVisible(true)));
            return;
        }
        final Iterator<Integer> positions = iWinLineInfo.getPositions().iterator();
        for (AbstractReel abstractReel : reelGroupView.getReels()) {
            int position = positions.next();
            for (int i = 0; i < abstractReel.getDisplayedSymbols().size(); i++) {
                final AbstractSymbol abstractSymbol = abstractReel.getDisplayedSymbols().get(i);
            }
        }
    }

    /**
     * Called when spin should be invoked.
     */
    @ExposeMethod
    public void spin() {
        getEventBus().post(new SpinCommand());
    }

    @ExposeMethod
    public void stopReel(int index) {
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

    @HasReflect
    private void setReelTouchButtonVisible(int reelNumber, boolean visible) {
        if (visible) {
            setModelProperty("reel" + reelNumber + "Visible", Boolean.TRUE);
        } else {
            setModelProperty("reel" + reelNumber + "Visible", Boolean.FALSE);
        }

    }

    @ExposeMethod
    public void fast() {
        speed += 0.1;
        if (speed > 1.7) {
            speed = 0.2f;
        }
        getEventBus().post(new ReelSpeedChangeEvent(0, speed));
        getEventBus().post(new ReelSpeedChangeEvent(1, speed));
        getEventBus().post(new ReelSpeedChangeEvent(2, speed));
        getEventBus().post(new ReelSpeedChangeEvent(3, speed));
        getEventBus().post(new ReelSpeedChangeEvent(4, speed));
        double roundOff = (double) Math.round(speed * 100) / 100;
        fast.setLabel("Speed: " + roundOff);
    }

    @ExposeMethod
    public void debug() {
        GameEngine.current().getActionManager().processQueue("ShowDebugScreen");
    }

    private class FindLongSpinObserver extends NextObserver<FindLongSpinEvent> {

        @Override
        public void onNext(FindLongSpinEvent findLongSpinEvent) {
            bonusCount = 0;
            firstReelBonus = false;
            List<Iterable<String>> stopListSymbols = ((AbstractReelGame) GameEngine.current().getGame()).getReelGameStateHolder().getStoppedSymbols();

            /**
             * find bonuses
             */
            {
                reelIndex = 0;
                for (int i = 0; i < stopListSymbols.size(); i++) {
                    reelIndex = i;
                    rowIndex = 0;
                    stopListSymbols.get(i).forEach(stopSymbolName -> {
                        if (stopSymbolName.equals("BONUS")) {
                            bonusCount++;
                            bonusPosition.put(reelIndex, rowIndex);
                        }
                        rowIndex++;
                    });
                }
            }


            AbstractReelGame game = ((AbstractReelGame) GameEngine.current().getGame());
            if (bonusCount >= 2 && bonusPosition.get(0) != null && bonusPosition.get(2) != null) {
                if (!turbo || !game.getReelGameStateHolder().isAutoPlay()) {
                    getEventBus().post(new ApplyReelsRotationTimeEvent(new ArrayList() {{
                        add(500);
                        add(800);
                        add(1100);
                        add(1400);
                        add(5000);
                    }}));
                } else if (turbo && game.getReelGameStateHolder().isAutoPlay()) {
                    getEventBus().post(new ApplyReelsRotationTimeEvent(new ArrayList() {{
                        add(0);
                        add(0);
                        add(0);
                        add(0);
                        add(1900);
                    }}));
                }
            } else {
                if (!turbo || !game.getReelGameStateHolder().isAutoPlay()) {
                    getEventBus().post(new ApplyReelsRotationTimeEvent(new ArrayList() {{
                        add(500);
                        add(800);
                        add(1100);
                        add(1400);
                        add(1700);
                    }}));
                } else if (turbo && game.getReelGameStateHolder().isAutoPlay()) {
                    getEventBus().post(new ApplyReelsRotationTimeEvent(new ArrayList() {{
                        add(0);
                        add(0);
                        add(0);
                        add(0);
                        add(0);
                    }}));
                }
            }
        }
    }

    boolean longSpin = false;

    class ReelsNextObserver extends NextObserver<ReelState> {
        int reelNumber;


        ReelsNextObserver(int rellNumber) {
            this.reelNumber = rellNumber;

        }

        @Override
        public void onNext(ReelState reelState) {

            if (reelState.name().equals("STOPPING")) {


                if (!resolution.equals("FHD_TWO_MONITORS")) {
                    GameEngine.current().getSoundManager().play("ReelStop");
                }
            }


            if (reelState.equals(ReelState.SPINNING)) {
                ViewGroup bonus = GameEngine.current().getViewManager().findViewById("symbolsAnimationScreen", "BONUS");
                bonus.getChildren().forEach(view -> {
                    try {
                        String id = view.getId().substring(0, 1);
                        if (Integer.parseInt(id) == reelNumber && view.isVisible()) {
                            view.setVisible(false);
                            ((KeyframeAnimationView) view).stop();
                        }
                    } catch (Exception e) {

                    }
                });
            }

            if (reelState.equals(ReelState.IDLE)) {

                //Long spin
                if (!skip && !reel4Skip) {
                    //begin long spin when 2 or 3 bonus present
                    if (reelNumber == 3 && bonusCount >= 2 && bonusPosition.get(0) != null && bonusPosition.get(2) != null) {
                        longSpin = true;
                        GameEngine.current().getActionManager().processAction(new WaitForBonus());
                        eventBus.post(new PlayLongAnimation());
                    }

                    if (reelNumber == 0 && bonusPosition.get(0) != null) {
                        eventBus.post(new JumpBonus("0" + bonusPosition.get(0) + "ShortBONUS", 0));
                    }

                    if (reelNumber == 2 && bonusPosition.get(0) != null && bonusPosition.get(2) != null) {
                        eventBus.post(new JumpBonus("2" + bonusPosition.get(2) + "ShortBONUS", 2));
                    }

                    if (reelNumber == 4 && bonusPosition.get(0) != null && bonusPosition.get(2) != null && bonusPosition.get(4) != null) {
                        eventBus.post(new JumpBonus("4" + bonusPosition.get(4) + "ShortBONUS", 4));
                    }

                    if (reelNumber == 4 && bonusPosition.get(4) == null && bonusPosition.get(0) != null && bonusPosition.get(2) != null) {
                        eventBus.post(new Skip4BonusJump());
                    }

                }

                if (reel4Skip && reelNumber == 4 && bonusCount >= 2 && bonusPosition.get(0) != null && bonusPosition.get(2) != null) {

                    eventBus.post(new Skip4BonusJump());
                }

            }
            if (reelState.equals(ReelState.SPINNING)) {
                longSpin = false;
            }
        }
    }


    //   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ANIMATION BONUS JUMP !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    Map<Integer, Integer> bonusPosition = new HashMap<>();
    int reelIndex;
    int rowIndex;

    boolean skip = false;
    public static boolean reel4Skip = false;

    class SkipButtonClickedObserver extends NextObserver<SkipCommand> {

        @Override
        public void onNext(SkipCommand skipCommand) {
            skip = true;
        }
    }

    class TurboObserver extends NextObserver<Turbo> {

        @Override
        public void onNext(Turbo turbo1) {
            AbstractReelGame game = ((AbstractReelGame) GameEngine.current().getGame());
            if (turbo) {
                turbo = false;
            } else if (!turbo && game.getReelGameStateHolder().isAutoPlay()) {
                turbo = true;
            }
        }
    }


    class SetTurboModeOnObserver extends NextObserver<SetTurboModeOnEvent>{

        @Override
        public void onNext(SetTurboModeOnEvent setTurboModeOnEvent) {
            AbstractReelGame game = ((AbstractReelGame) GameEngine.current().getGame());
            if(game.getReelGameStateHolder().isAutoPlay()){
                turbo=true;
            }
        }
    }

    class SetTurboModeOffObserver extends NextObserver<SetTurboModeOffEvent>{

        @Override
        public void onNext(SetTurboModeOffEvent setTurboModeOffEvent) {
            turbo=false;
        }
    }

    class StopAutoPlayCommandObserver extends NextObserver<StopAutoPlayCommand> {

        @Override
        public void onNext(StopAutoPlayCommand stopAutoPlayCommand) {
            turbo = false;
        }
    }

    boolean turbo = false;

    private class ShowStopLinesObserver extends NextObserver<ShowStopEvent> {

        @Override
        public void onNext(ShowStopEvent showStopLines) {
            stopReelButtons.forEach(buttonView -> {
                buttonView.setVisible(true);
            });

        }
    }

    class ShowWinSumForHtmlObserver extends NextObserver<ShowWinSumForHtml> {

        @Override
        public void onNext(ShowWinSumForHtml showWinSumForHtml) {
            if (!resolution.equals("FHD_TWO_MONITORS")) {
                bottomPanelScreenModel.setProperty("formattedWin2", creditsFormatter.formatWithCurrency(showWinSumForHtml.getWinSum()));
                bottomPanelScreenModel.setProperty("winVisible2", showWinSumForHtml.getVisible());
            }
        }
    }

    class ShowCardRiskPresentation extends NextObserver<CardRiskPresentation> {


        @Override
        public void onNext(CardRiskPresentation cardRiskPresentation) {
            cardRiskPresentation.getWinAmount();
        }
    }


    class HideButtonsObserver extends NextObserver<HideButtonsEvent> {

        @Override
        public void onNext(HideButtonsEvent hideButtonsEvent) {
            bottomPanelScreenModel.setProperty("spinEnable", Boolean.FALSE);
            bottomPanelScreenModel.setProperty("mobileSpinPanelOpened", Boolean.FALSE);
            bottomPanelScreenModel.setProperty("mobileBetsLinesButtonVisible", Boolean.FALSE);
            bottomPanelScreenModel.setProperty("autoStartEnable", Boolean.FALSE);
            bottomPanelScreenModel.setProperty("skipVisible", Boolean.FALSE);
            bottomPanelScreenModel.setProperty("mobileStateThree", Boolean.FALSE);
            bottomPanelScreenModel.setProperty("mobileStateTwo", Boolean.FALSE);
            bottomPanelScreenModel.setProperty("stateTwo", Boolean.FALSE);
            bottomPanelScreenModel.setProperty("takeWin", Boolean.FALSE);
            bottomPanelScreenModel.setProperty("mobileStateOne", Boolean.FALSE);
        }
    }

    class ResetGameCommandHandler extends NextObserver<ResetGameCommand>{

        @Override
        public void onNext(ResetGameCommand resetGameCommand) {
            getEventBus().post(new HideScreenCommand("errorScreen", null));
        }
    }

}