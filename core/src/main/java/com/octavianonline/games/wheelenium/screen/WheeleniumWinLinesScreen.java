package com.octavianonline.games.wheelenium.screen;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.animation.IAnimationFactory;
import com.atsisa.gox.framework.eventbus.IEventBus;
import com.atsisa.gox.framework.eventbus.NextObserver;
import com.atsisa.gox.framework.eventbus.Subscription;
import com.atsisa.gox.framework.eventbus.annotation.Subscribe;
import com.atsisa.gox.framework.infrastructure.IViewManager;
import com.atsisa.gox.framework.rendering.IRenderer;
import com.atsisa.gox.framework.screen.Screen;
import com.atsisa.gox.framework.screen.annotation.InjectView;
import com.atsisa.gox.framework.utility.Timeout;
import com.atsisa.gox.framework.utility.TimeoutCallback;
import com.atsisa.gox.framework.utility.logger.ILogger;
import com.atsisa.gox.framework.view.ImageView;
import com.atsisa.gox.framework.view.ViewGroup;
import com.atsisa.gox.logic.provider.ILinesModelProvider;
import com.atsisa.gox.reels.AbstractReelGame;
import com.atsisa.gox.reels.IWinLineInfo;
import com.atsisa.gox.reels.ReelsPresentationStates;
import com.atsisa.gox.reels.command.*;
import com.atsisa.gox.reels.event.BetModelChangedEvent;
import com.atsisa.gox.reels.event.LinesModelChangedEvent;
import com.atsisa.gox.reels.event.PresentationStateChangedEvent;
import com.gwtent.reflection.client.HasReflect;
import com.gwtent.reflection.client.Reflectable;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.framework.reels.components.winlines.WinLineFrame;
import com.octavianonline.framework.reels.components.winlines.WinLinesGroup;
import com.octavianonline.games.wheelenium.event.PushSymbolAnimationsInWinningLinesCommand;
import com.octavianonline.games.wheelenium.event.WinLineFrameAnimationCommand;
import com.octavianonline.games.wheelenium.event.restore.HideWinLinesEvent;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Reflectable(superClasses = true, fields = false)
public class WheeleniumWinLinesScreen extends Screen<WheeleniumWinLinesScreenModel> {

    public static final String LAYOUT_ID_PROPERTY = "WinLinesScreenLayoutId";
    private static String resolution;
    private final List<Subscription> subscriptions = new ArrayList<>();
    private final ILinesModelProvider linesModelProvider;
    @InjectView
    @HasReflect
    public WinLinesGroup winLinesGroup;
    private List<ViewGroup> winLineViews;
    private List<WinLineFrame> winLineFrames;
    private List<WinLineFrame> currentLineFrames = new ArrayList<>();
    private Timeout closeLinesTimeout;


    @Inject
    public WheeleniumWinLinesScreen(@Named(LAYOUT_ID_PROPERTY) String layoutId, WheeleniumWinLinesScreenModel model, IRenderer renderer, IViewManager viewManager, IAnimationFactory animationFactory, ILogger logger, IEventBus eventBus, ILinesModelProvider linesModelProvider) {
        super(layoutId, model, renderer, viewManager, animationFactory, logger, eventBus);
        this.linesModelProvider = linesModelProvider;
    }

    @Override
    protected void beforeActivated() {
        super.beforeActivated();
        this.subscriptions.add(getEventBus().register(new LinesModelChangedEventObserver(), LinesModelChangedEvent.class));
        this.subscriptions.add(getEventBus().register(new HideWinLinesObserver(), HideWinLinesEvent.class));
    }

    private Timeout tempTimeout;

    class TimpTimeoutCaback implements TimeoutCallback {

        @Override
        public void onTimeout() {

            tempTimeout = new Timeout(1000, new TimpTimeoutCaback(), true);

        }
    }

    @Override
    protected void afterActivated() {
        super.afterActivated();
        resolution = GameEngine.current().getGameConfiguration().getResolution().name();

        this.winLinesGroup.constructWinLines(this.linesModelProvider.getLineDescriptors().get());
        this.winLineViews = this.winLinesGroup.getWinLineViews();
        this.winLineFrames = getViewManager().findViewByType(WinLineFrame.class);
        Collections.reverse(this.winLineFrames);
        this.winLineFrames.forEach(winLineFrame -> {
            winLineFrame.setVisible(false);
        });

        if(!resolution.equals("FHD_TWO_MONITORS")){
            getEventBus().register(new PresentationStateChangedEventObserver(), PresentationStateChangedEvent.class);
            getEventBus().register(new SetBetCommandObserver(), BetModelChangedEvent.class);
        }

//        tempTimeout = new Timeout(1000, new TimpTimeoutCaback(), true);
    }

    private void handleLinesModelChangedEvent(LinesModelChangedEvent event) {
        switch (event.getChangeType()) {
            case LinesModelChangedEvent.CURRENT_WINNING_LINE:
                this.hideWinLines();
                if (event.getLinesModel().getCurrentWinningLine().isPresent()) {
                    this.handleCurrentWinningLineEvent(event.getLinesModel().getCurrentWinningLine().get());
                }
                break;
            case LinesModelChangedEvent.AVAILABLE_LINES:
                break;
            case LinesModelChangedEvent.SELECTED_LINES:
                break;
            case LinesModelChangedEvent.WINNING_LINES:
                break;
            default:
                break;
        }
    }

    private void hideWinLines() {
        this.winLineViews.forEach(view -> {
            if (view.isVisible()) {
                view.setVisible(false);
            }
        });
        this.winLineFrames.forEach(winLineFrame -> {
            if (winLineFrame.isVisible()) {
                winLineFrame.setVisible(false);
            }
        });
    }


    private void showLine(IWinLineInfo currentWinningLine) {
        final int lineNumber = currentWinningLine.getLineNumber() - 1;
        if (lineNumber >= 0) {
            this.winLineViews.get(lineNumber).setVisible(true);
        }
    }


    @Override
    protected void beforeDeactivated() {
        super.beforeDeactivated();
        this.subscriptions.forEach(Subscription::unsubscribe);
    }


    private void handleCurrentWinningLineEvent(IWinLineInfo currentWinningLine) {
        this.showLine(currentWinningLine);

    }

    private class LinesModelChangedEventObserver extends NextObserver<LinesModelChangedEvent> {
        @Override
        public void onNext(LinesModelChangedEvent event) {
            handleLinesModelChangedEvent(event);
        }
    }


    class SetBetCommandObserver extends NextObserver<BetModelChangedEvent> {

        @Override
        public void onNext(BetModelChangedEvent betModelChangedEvent) {
            Object showLinesCommand = betModelChangedEvent.getSourceEvent();
            if (showLinesCommand instanceof SetMaxBetCommand ||showLinesCommand instanceof IncreaseBetCommand || showLinesCommand instanceof DecreaseBetCommand || showLinesCommand instanceof SetBetCommand || (showLinesCommand instanceof LinesModelChangedEvent && (((LinesModelChangedEvent) showLinesCommand).getSourceEvent() instanceof DecreaseLinesCommand || ((LinesModelChangedEvent) showLinesCommand).getSourceEvent() instanceof IncreaseLinesCommand))) {


                Optional.ofNullable(closeLinesTimeout).ifPresent(timeout -> {
                    timeout.clear();
                });
                closeLinesTimeout = new Timeout(15000, new CloseTimeoutCallBack(), true);
                getModel().setProperty("fiveLines", true);
            }
        }
    }

    class CloseTimeoutCallBack implements TimeoutCallback {

        @Override
        public void onTimeout() {
            getModel().setProperty("fiveLines", false);
        }
    }

    private class PresentationStateChangedEventObserver extends NextObserver<PresentationStateChangedEvent> {

        @Override
        public void onNext(PresentationStateChangedEvent event) {
            if (event.getStateName().equals(ReelsPresentationStates.RUNNING_REELS)) {
                Optional.ofNullable(closeLinesTimeout).ifPresent(timeout -> {
                    timeout.clear();
                });
                getModel().setProperty("fiveLines", false);
            }
        }
    }

    private class HideWinLinesObserver extends NextObserver<HideWinLinesEvent>{

        @Override
        public void onNext(HideWinLinesEvent hideWinLinesEvent) {
            hideWinLines();
        }
    }

}

