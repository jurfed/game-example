package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.framework.eventbus.UIDispatcherOnSubscribe;
import com.atsisa.gox.framework.exception.GameException;
import com.atsisa.gox.framework.exception.GeneralSystemException;
import com.atsisa.gox.framework.exception.NotEnoughFundsException;
import com.atsisa.gox.framework.exception.UnableToConnectException;
import com.atsisa.gox.framework.utility.StringUtility;
import com.atsisa.gox.reels.AbstractReelGame;
import com.atsisa.gox.reels.event.LogicRequestCompleteEvent;
import com.atsisa.gox.reels.event.LogicRequestSentEvent;
import com.atsisa.gox.reels.exception.NoHistoryException;
import com.atsisa.gox.reels.logic.GameLogicException;
import com.atsisa.gox.reels.logic.IReelGameLogic;
import com.gwtent.reflection.client.annotations.Reflect_Full;

import com.octavianonline.framework.logic.IWheelBonusFeatureGameLogic;
import com.octavianonline.framework.logic.WheelSpinRequest;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

@Reflect_Full
public class SendWheelBonusRequest extends Action {
    private AbstractReelGame reelGame;
    private IWheelBonusFeatureGameLogic wheelBonusFeatureGameLogic;
    private List<Object> logicResults = new ArrayList<>();

    @Override
    protected void grabData()  {
        this.reelGame = (AbstractReelGame) GameEngine.current().getGame();
        IReelGameLogic reelGameLogic = reelGame.getReelGameLogic();
        if (!(reelGameLogic instanceof IWheelBonusFeatureGameLogic)) {
            throw new RuntimeException(StringUtility.format("You need implement IWheelBonusFeatureGameLogic to %s", reelGameLogic.getClass().getName()));
        }
        this.wheelBonusFeatureGameLogic = (IWheelBonusFeatureGameLogic) reelGameLogic;
    }

    @Override
    protected void execute() {

        subscribeForResult(this.wheelBonusFeatureGameLogic.spinWheel((new WheelSpinRequest())));
    }

    protected void subscribeForResult(Observable<? extends Object> observable) {
        eventBus.post(new LogicRequestSentEvent());
        observable
               // .flatMap(this::createObservable ) //todo remove this line after update to 1.17.0 version
                .subscribe(result -> {
            logicResults.add(result);
            onReceiveLogicResult(result);
            eventBus.post(result);
        }, this::handleLogicError, () -> {
            eventBus.post(new LogicRequestCompleteEvent(logicResults));
            this.onLogicComplete();
        });
    }

    protected <T> Observable<T> createObservable(Object presentations) {
        return Observable.create(new UIDispatcherOnSubscribe<>(subscriber -> {
            try {
                ((List<T>)presentations).forEach(subscriber::onNext);
            } catch (Exception ex) {
                subscriber.onError(ex);
            }
            subscriber.onCompleted();
        }));
    }

    protected void onLogicComplete() {
        finish();
    }

    protected void handleLogicError(Throwable throwable) {
        logger.debug("SendInitRequestAction | responseFailure");
        cancelFurtherProcessing();
        if (throwable instanceof GameLogicException) {
            fail(translateGameServerException((GameLogicException) throwable));
        } else {
            fail(new UnableToConnectException(throwable));
        }
    }

    private GameException translateGameServerException(GameLogicException cause) {
        int errorCode = cause.getErrorCode();
        if (errorCode == NOT_ENOUGH_FUNDS_ERROR_CODE) {
            return new NotEnoughFundsException(cause);
        } else if (errorCode == NO_HISTORY_ERROR_CODE) {
            return new NoHistoryException(cause);
        }
        return new GeneralSystemException(cause);
    }

    /**
     * Not enough funds error code.
     */
    private static final int NOT_ENOUGH_FUNDS_ERROR_CODE = 2;
    /**
     * No history error code.
     */
    private static final int NO_HISTORY_ERROR_CODE = 29;

    protected void onReceiveLogicResult(Object logicResult) {
    }

}
