package com.octavianonline.games.wheelenium.screen;

import aurelienribon.tweenengine.TweenCallback;
import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.animation.AnimationState;
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
import com.atsisa.gox.reels.AbstractReelGame;
import com.atsisa.gox.reels.ICreditsFormatter;
import com.atsisa.gox.reels.command.*;
import com.atsisa.gox.reels.event.BetModelChangedEvent;
import com.atsisa.gox.reels.event.LinesModelChangedEvent;
import com.atsisa.gox.reels.model.ILinesModelProvider;
import com.gwtent.reflection.client.HasReflect;
import com.gwtent.reflection.client.Reflectable;
import com.octavianonline.framework.reels.event.CreditsPresentationChangeEvent;
import com.octavianonline.framework.reels.logic.SpinWheelPresentation;
import com.octavianonline.games.wheelenium.action.SendWheelBonusRequest;
import com.octavianonline.games.wheelenium.event.FreegamesEndPlateEvent;
import com.octavianonline.games.wheelenium.event.bonusanimate.ResetBonusValuesEvent;
import com.octavianonline.games.wheelenium.event.restore.RestoreEnterWheelEvent;
import com.octavianonline.games.wheelenium.event.restored.RestoredStartSpeenWheelEvent;
import com.octavianonline.games.wheelenium.event.wheelfeature.GoToStopPositionEvent;
import com.octavianonline.games.wheelenium.event.wheelfeature.ShowWheelFeatureEvent;
import com.octavianonline.games.wheelenium.logic.WheeleniumSepartedNetLogic;
import com.octavianonline.games.wheelenium.screen.model.WheelModel;
import rx.Subscription;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;
import java.util.Random;

@Reflectable(superClasses = true, fields = false)
public class WheelScreen extends Screen<WheelModel> {

    public static final String LAYOUT_ID_PROPERTY = "WheeleScreenLayoutId";
    private static final int BEGIN_WHEEL_SPEED = 2200;
    private static final int BEGIN_CURSOR_SPEED = 400;
    private final static float SECTOR_ANGLE = 22.5f;
    private final static float BEGIN_WHEEL_OFFSET = -4.5f;
    private final static float FIRST_SECTOR_ROTATION = 18f;
    private final static float SECTOR_ROTATION_ANGLE = -47f;
    private final static float SECTOR_ROTATION_ANGLE_END = -37f;
    private final static int SECTOR_NUMBERS = 16;
    private final static int SECTOR_ROTATION_TIME = 50;
    static long winAmount = 0;
    @InjectView
    @HasReflect
    ViewGroup wheelScreen;
    @InjectView
    @HasReflect
    ViewGroup wheel;
    @InjectView
    @HasReflect
    ImageView cursor;
    IEventBus eventBus;
    ICreditsFormatter creditsFormatter;
    Long totalBet;
    ILinesModelProvider linesModelProvider;
    TweenViewAnimation bigRotationAnimation;
    @InjectView
    @HasReflect
    ImageView centerSmall;
    @InjectView
    @HasReflect
    ButtonView buttonStart;
    boolean canbuttonFlash = false;
    @InjectView
    @HasReflect
    ImageView imageButton;
    @InjectView
    @HasReflect
    ViewGroup bgr;
    @InjectView
    @HasReflect
    ButtonView realButton;
    //!!!!!!!!!!!!!!!!!!!!!!!!!! SHOW WHEEL ANIMATION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    TweenViewAnimation wheelShowAnimation;
    TweenViewAnimation buttonShowAnimation;
    //!!!!!!!!!!!!! SHOW CURSOR ANIMATION !!!!!!!!!!!!!
    TweenViewAnimation cursorShowAnimation;
    TweenViewAnimation hideButton;
    boolean canStop = false;
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!! FOR SLOWING WHEEL ROTATION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    boolean slowReel = false;
    int stopSteps = 0;

    // !!!!!!!!!!!!!!!!!!!!!!!!!!! BET & TEXT CHANGE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    KeyframeAnimationView firestart;
    KeyframeAnimationView fireLopp;
    KeyframeAnimationView burst;
    Subscription keyFrameSubscr;
    // !!!!!!! text jump animation !!!!!!!!!!!!!
    TweenViewAnimation textAnimation;
    // !!!!!!! win lamp animation !!!!!!!!!!!!!
    Timeout lampTimeout;
    private float currentWheelSpeed = 2200;
    private float rotationAngle = SECTOR_ANGLE;
    private int rotationStep = 1;
    private int winningSector;
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! SLOW WHEEL !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private long startStoppingTime;
    private Subscription wheelShowUnsub;
    private Subscription cursorShowUnsub;
    //     !!!!!!!!!!!!!!!!!!!!!!!!!!!! BIG ROTATION ANIMATION !!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private Subscription bigRotationStepUnsub;
    private Subscription cursorUnsub;
    private Subscription jumpTextUnsub;

    @Inject
    protected WheelScreen(@Named(LAYOUT_ID_PROPERTY) String layoutId, WheelModel model, IRenderer renderer, IViewManager viewManager, IAnimationFactory animationFactory, ILogger logger, IEventBus eventBus, ISoundManager soundManager, ICreditsFormatter creditsFormatter, ILinesModelProvider linesModelProvider) {
        super(layoutId, model, renderer, viewManager, animationFactory, logger, eventBus);
        this.creditsFormatter = creditsFormatter;
        this.eventBus = eventBus;
        this.linesModelProvider = linesModelProvider;
    }

    public static long getWinAmount() {
        return winAmount;
    }

    public static void setWinAmount(int sum) {
        winAmount = sum;
    }

    public void setCurrentWheelSpeed(int currentWheelSpeed) {
        this.currentWheelSpeed = currentWheelSpeed;
    }

    @Override
    protected void afterActivated() {
        super.afterActivated();
        getEventBus().register(new ShowWheelFeatureObserver(), ShowWheelFeatureEvent.class);
        getEventBus().register(new SetBetCommandObserver(), BetModelChangedEvent.class);
        getEventBus().register(new CreditsFormatterChangedObserver(), CreditsPresentationChangeEvent.class);
        getEventBus().register(new GoToStopPositionObserver(), GoToStopPositionEvent.class);
        getEventBus().register(new SpinWheelPresentationObserver(), SpinWheelPresentation.class);
        getEventBus().register(new ResetBonusValuesObserver(), ResetBonusValuesEvent.class);
        getEventBus().register(new RestoredStartSpeenWheelObserver(), RestoredStartSpeenWheelEvent.class);
        getEventBus().register(new RestoreEnterWheelEventObserver(), RestoreEnterWheelEvent.class);
        totalBet = WheeleniumSepartedNetLogic.getBet();
        setTextValues(Math.toIntExact(totalBet));
        new Timeout(300, new ButtonTimeOut(), true);
    }

    public class RestoreEnterWheelEventObserver extends NextObserver<RestoreEnterWheelEvent> {

        @Override
        public void onNext(RestoreEnterWheelEvent restoreEnterWheelEvent) {
            resetValues();
            imageButton.setVisible(false);
            winningSector = 0;
            stopSteps = 0;
            canStop = false;

            if (bigRotationStepUnsub != null && !bigRotationStepUnsub.isUnsubscribed()) {
                bigRotationStepUnsub.unsubscribe();
            }
            if (bigRotationAnimation != null && bigRotationAnimation.isPlaying()) {
                bigRotationAnimation.stop();
            }
            if(slowTimeout!=null && !slowTimeout.isCleaned()){
                slowTimeout.clear();
            }

        }
    }


    void setTextValues(int totalbetValue) {
        getModel().getMapValues().forEach((s, integer) -> {
            String textVallue = creditsFormatter.format(integer * totalbetValue);
            getModel().setProperty(s, textVallue);
        });
    }

    void resetValues() {

        if (GameEngine.current().getGameConfiguration().getResolution().name().equals("FHD_TWO_MONITORS")) {
            cursor.setX(2216);
            cursor.setY(-222);
        } else {
            cursor.setX(2286);
            cursor.setY(-292);
        }

        buttonStart.setX(2000);
        centerSmall.setVisible(false);

        currentWheelSpeed = 2200;
        winningSector = 0;
        stopSteps = 0;
        canStop = false;
        wheel.setRotation(BEGIN_WHEEL_OFFSET);
        rotationStep = 1;

        Optional.ofNullable(bigRotationStepUnsub).ifPresent(subscription -> {
            if (!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        });
    }

    void wheelShowStep(int step) {
        wheelShowAnimation = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
        wheelShowUnsub = wheelShowAnimation.getTweenStateObservable().subscribe(new WheelShowObserver(step));
        wheelShowAnimation.setTargetView(wheel);

        buttonShowAnimation = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
        buttonShowAnimation.setTargetView(buttonStart);
        buttonStart.setVisible(true);
        cursorShowAnimation = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
        cursorShowAnimation.setTargetView(cursor);

        switch (step) {
            case 1:
                wheelShowAnimation.setDestinationX(700);
                wheelShowAnimation.setTimeSpan(400);

                buttonShowAnimation.setDestinationX(1668);
                buttonShowAnimation.setTimeSpan(400);
                break;
            case 2:
                wheelShowAnimation.setDestinationX(400);
                wheelShowAnimation.setTimeSpan(200);

                buttonShowAnimation.setDestinationScaleX(2.1f);
                buttonShowAnimation.setDestinationScaleY(2.1f);
                buttonShowAnimation.setDestinationX(1720);
                buttonShowAnimation.setDestinationY(1900);
                buttonShowAnimation.setTimeSpan(400);
                break;
            case 3:
                wheelShowAnimation.setDestinationX(500);
                wheelShowAnimation.setTimeSpan(200);

                buttonShowAnimation.setDestinationScaleX(1.35f);
                buttonShowAnimation.setDestinationScaleY(1.35f);
                buttonShowAnimation.setDestinationX(1668);
                buttonShowAnimation.setDestinationY(1852);
                buttonShowAnimation.setTimeSpan(400);
                if (!((AbstractReelGame) GameEngine.current().getGame()).getReelGameStateHolder().isAutoPlay()) {
                    realButton.setVisible(true);
                }
                break;
        }
        wheelShowAnimation.play();
        buttonShowAnimation.play();
    }

    void cursorShowStep(int step) {
        cursorShowAnimation = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
        cursorShowUnsub = cursorShowAnimation.getTweenStateObservable().subscribe(new cursorShowObserver(step));
        cursorShowAnimation.setTargetView(cursor);
        AbstractReelGame game = ((AbstractReelGame) GameEngine.current().getGame());

        switch (step) {
            case 1:
                GameEngine.current().getSoundManager().play("button_show");

                cursorShowAnimation.setDestinationX(1766);
                cursorShowAnimation.setDestinationY(118);
                cursorShowAnimation.setTimeSpan(300);
                break;
            case 2:
                cursorShowAnimation.setDestinationX(1566);
                cursorShowAnimation.setDestinationY(268);

                cursorShowAnimation.setDestinationScaleX(2.1f);
                cursorShowAnimation.setDestinationScaleY(2.1f);
                cursorShowAnimation.setTimeSpan(300);
                break;
            case 3:
                cursorShowAnimation.setDestinationScaleX(1.35f);
                cursorShowAnimation.setDestinationScaleY(1.35f);
                cursorShowAnimation.setTimeSpan(200);
                if (game.getReelGameStateHolder().isAutoPlay()) {
                    new Timeout(1000, () -> {
                        startSpeenWheel(132.35f);

                    }, true);
                }
                break;
        }

        cursorShowAnimation.play();

    }

    /**
     * speen wheel after restored
     */
    class RestoredStartSpeenWheelObserver extends NextObserver<RestoredStartSpeenWheelEvent> {

        @Override
        public void onNext(RestoredStartSpeenWheelEvent restoredStartSpeenWheelEvent) {
            GameEngine.current().getSoundManager().play("bonusWheel");
            GameEngine.current().getSoundManager().setLooping("bonusWheel", true);

            resetValues();
            //show background
            wheelScreen.setAlpha(1);
            wheelScreen.setVisible(true);
            wheel.setX(500);
            cursor.setScaleX(1.35f);
            cursor.setScaleY(1.35f);
            cursor.setX(1566);
            cursor.setY(268);

            bigRotationStep(BEGIN_WHEEL_SPEED, FIRST_SECTOR_ROTATION);
            imageButton.setX(2100);
            bgr.setAlpha(1);

            GameEngine.current().getSoundManager().stop("bonusWheel");
            GameEngine.current().getSoundManager().play("bonusreel");
            GameEngine.current().getSoundManager().play("wheelbutton");

            realButton.setVisible(false);
            realButton.setEnabled(false);
            canbuttonFlash = false;
            imageButton.setVisible(true);
            hideButton = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
            hideButton.setTargetView(imageButton);
            hideButton.setDestinationAlpha(0);
            hideButton.setTimeSpan(300);
            hideButton.play();

            buttonStart.setEnabled(true);

            buttonStart.setVisible(false);

            winningSector = 0;
            stopSteps = 0;
            canStop = false;
            changeSpeed(132.35f);
            new Timeout(3000, new SlowWheelTimer(), true);

        }
    }

    Timeout slowTimeout;

    @ExposeMethod
    public void startSpeenWheel(float value) {
        GameEngine.current().getActionManager().processAction(new SendWheelBonusRequest());

        GameEngine.current().getSoundManager().stop("bonusWheel");
        GameEngine.current().getSoundManager().play("bonusreel");
        GameEngine.current().getSoundManager().play("wheelbutton");

        realButton.setVisible(false);
        realButton.setEnabled(false);
        canbuttonFlash = false;
        imageButton.setVisible(true);
        hideButton = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
        hideButton.setTargetView(imageButton);
        hideButton.setDestinationAlpha(0);
        hideButton.setTimeSpan(300);
        hideButton.play();

        buttonStart.setEnabled(true);
        buttonStart.setVisible(false);

        winningSector = 0;
        stopSteps = 0;
        canStop = false;
        changeSpeed(value);
        slowTimeout = new Timeout(3000, new SlowWheelTimer(), true);
    }

    @ExposeMethod
    public void changeSpeed(float newSpeed) {

        float currentWheelAngle = wheel.getRotation();
        float needWheelAngle = rotationStep * rotationAngle + BEGIN_WHEEL_OFFSET;
        float tempSpeed = (((float) newSpeed / SECTOR_ANGLE) * (needWheelAngle - currentWheelAngle));
        if (tempSpeed > 0) {
            if (!bigRotationStepUnsub.isUnsubscribed()) {
                bigRotationStepUnsub.unsubscribe();
            }

            bigRotationAnimation.stop();
            currentWheelSpeed = newSpeed;
            bigRotationStep(tempSpeed, needWheelAngle);

        }

    }

    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! MAIN STEP !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    void bigRotationStep(float speed, float rotationAngle) {

        //wheel rotation
        bigRotationAnimation = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
        bigRotationStepUnsub = bigRotationAnimation.getTweenStateObservable().subscribe(new BigAnimationObserver());
        bigRotationAnimation.setTargetView(wheel);
        bigRotationAnimation.setDestinationRotation(rotationAngle);
        bigRotationAnimation.setTimeSpan(speed);// correct for fast
        bigRotationAnimation.play();

    }

    /**
     * logic for slowing wheel rotation
     */
    void slowWheel() {

        long timeSpent = System.currentTimeMillis() - this.startStoppingTime;
        long maxLong = 5000;

        if (maxLong != 0) {
            float timePercent = (timeSpent) / (maxLong / 100F);
            float newSpeed = (((900) * (timePercent)) / 100L) + 132.35f;
            if (newSpeed >= 900) {
                newSpeed = 900;
                canStop = true;
            } else {
                new Timeout(50, () -> {
                    slowWheel();
                }, true);
            }
            if (newSpeed > 0) {
                changeSpeed(newSpeed);
            }
        }
    }


    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! GETTING STOP POSITION FROM SERVER !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    void keyFrameAnimationStart() {
        ViewGroup lamps = findViewById("lamp" + rotationStep);
        lampTimeout = new Timeout(500, new LampAnimationTimer(lamps), true);
        firestart = findViewById("startFire" + rotationStep);
        firestart.setVisible(true);
        firestart.gotoAndPlay(1);
        keyFrameSubscr = firestart.getAnimationStateObservable().subscribe(new AnimationStateObserver());
    }

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! WIN ANIMATION ON THE WIN SECTOR !!!!!!!!!!!!!!!!!!!!!!!!!!!

    void keyFrameAnimationStop() {
        firestart.setVisible(false);
        firestart.stop();
        fireLopp.setVisible(false);
        fireLopp.stop();
        burst.setVisible(false);
    }

    void jumpStep(int step) {
        textAnimation = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
        jumpTextUnsub = textAnimation.getTweenStateObservable().subscribe(new JumpTextObserver(step));
        TextView winText = findViewById(rotationStep + "");
        textAnimation.setTargetView(winText);

        switch (step) {
            case 1:
                textAnimation.setDestinationScaleX(1.2f);
                textAnimation.setDestinationScaleY(1.2f);
                break;
            case 2:
                textAnimation.setDestinationScaleX(1.0f);
                textAnimation.setDestinationScaleY(1.0f);
                break;
            case 3:
                textAnimation.setDestinationScaleX(1.2f);
                textAnimation.setDestinationScaleY(1.2f);
                break;
            case 4:
                textAnimation.setDestinationScaleX(1.0f);
                textAnimation.setDestinationScaleY(1.0f);
                break;
        }
        textAnimation.setTimeSpan(200);// correct for fast
        textAnimation.play();

    }

    class CreditsFormatterChangedObserver extends NextObserver<CreditsPresentationChangeEvent> {

        @Override
        public void onNext(CreditsPresentationChangeEvent formatterUpdatedEvent) {
            setTextValues((int) ((AbstractReelGame) GameEngine.current().getGame()).getBetModelProvider().getBetModel().getTotalBet());
        }
    }

    class SetBetCommandObserver extends NextObserver<BetModelChangedEvent> {

        @Override
        public void onNext(BetModelChangedEvent betModelChangedEvent) {
            totalBet = ((AbstractReelGame) GameEngine.current().getGame()).getBetModelProvider().getBetModel().getTotalBet();
            Object showLinesCommand = betModelChangedEvent.getSourceEvent();
            if (showLinesCommand instanceof SetMaxBetCommand || showLinesCommand instanceof IncreaseBetCommand || showLinesCommand instanceof DecreaseBetCommand || showLinesCommand instanceof SetBetCommand || (showLinesCommand instanceof LinesModelChangedEvent && (((LinesModelChangedEvent) showLinesCommand).getSourceEvent() instanceof DecreaseLinesCommand || ((LinesModelChangedEvent) showLinesCommand).getSourceEvent() instanceof IncreaseLinesCommand))) {
                setTextValues((int) betModelChangedEvent.getBetModel().getTotalBet());
            }
        }
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! BEGIN & SHOW WHEEL !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private class ShowWheelFeatureObserver extends NextObserver<ShowWheelFeatureEvent> {

        @Override
        public void onNext(ShowWheelFeatureEvent showWheelFeatureEvent) {
            GameEngine.current().getSoundManager().play("applause_intro");
            GameEngine.current().getSoundManager().play("bonusWheel");
            GameEngine.current().getSoundManager().setLooping("bonusWheel", true);

            resetValues();
            //show background
            wheelScreen.setAlpha(1);
            wheelScreen.setVisible(true);
            wheelShowStep(1);

            bigRotationStep(BEGIN_WHEEL_SPEED, FIRST_SECTOR_ROTATION);
            imageButton.setAlpha(1);

            TweenViewAnimation bgrAnimation = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
            bgrAnimation.setDestinationAlpha(1);
            bgrAnimation.setTargetView(bgr);
            bgrAnimation.setTimeSpan(500);
            bgrAnimation.play();
        }

    }

    class ButtonTimeOut implements TimeoutCallback {

        @Override
        public void onTimeout() {

            if (buttonStart.isEnabled()) {
                if (canbuttonFlash) {
                    buttonStart.setEnabled(false);
                }
                new Timeout(350, new ButtonTimeOut(), true);
            } else {
                if (canbuttonFlash) {
                    buttonStart.setEnabled(true);
                }
                new Timeout(700, new ButtonTimeOut(), true);
            }

        }
    }

    class WheelShowObserver extends NextObserver<Integer> {
        int step;

        WheelShowObserver(int step) {
            this.step = step;
        }

        @Override
        public void onNext(Integer integer) {
            if (integer == TweenCallback.COMPLETE) {
                wheelShowUnsub.unsubscribe();
                if (step <= 3) {
                    wheelShowStep(++step);
                }
                if (step == 4) {
                    centerSmall.setVisible(true);
                    cursorShowStep(1);
                    canbuttonFlash = true;
                }

            }
        }
    }

    class cursorShowObserver extends NextObserver<Integer> {
        int step;

        cursorShowObserver(int step) {
            this.step = step;
        }

        @Override
        public void onNext(Integer integer) {
            if (integer == TweenCallback.COMPLETE) {
                cursorShowUnsub.unsubscribe();
                if (step <= 3) {
                    cursorShowStep(++step);
                } else {
                    realButton.setEnabled(true);
                }
            }
        }
    }

    /**
     * main logic for steps
     */
    class BigAnimationObserver extends NextObserver<Integer> {

        @Override

        public void onNext(Integer integer) {
            if (integer == TweenCallback.COMPLETE) {
                rotationAngle = SECTOR_ANGLE;
                bigRotationStepUnsub.unsubscribe();

                if (stopSteps == 0) {//if there is no yet win
                    rotationStep++;
                }

                if (rotationStep > SECTOR_NUMBERS) {
                    rotationStep = 1;
                    wheel.setRotation(BEGIN_WHEEL_OFFSET);
                }


                float destination = rotationStep * rotationAngle + BEGIN_WHEEL_OFFSET;

//                if (winningSector > 0 && rotationStep == getModel().getValuesForStop().get(winningSector)  && canStop) {
                if (winningSector > 0 && rotationStep == winningSector && canStop) {

                    switch (stopSteps) {
                        case 0:
                            Random ran = new Random();
                            int angle = ran.nextInt(10) + 11;


//                            bigRotationStep(currentWheelSpeed / 2, wheel.getRotation()+ angle + BEGIN_WHEEL_OFFSET);
                            bigRotationStep(angle * 1000 / 25, wheel.getRotation() + angle + BEGIN_WHEEL_OFFSET);
                            playCursorAnimation();
                            stopSteps++;
                            break;
                        case 1:
                            bigRotationStep(136, wheel.getRotation() + 2.0f);
                            GameEngine.current().getSoundManager().stop("bonusreel");
                            GameEngine.current().getSoundManager().play("bonusreel_end");


                            stopSteps++;
/*                            new Timeout(1000,()->{
                                startSpeenWheel(132.35f);
                            },true);*/
                            break;
                        case 2:
                            bigRotationStep(136, wheel.getRotation() - 3.0f);

                            stopSteps++;
                            break;
                        case 3:
                            bigRotationStep(136, wheel.getRotation() + 1.7f);

                            stopSteps++;
                            break;
                        case 4:
                            bigRotationStep(136, wheel.getRotation() + -0.7f);

                            stopSteps++;
                            break;
                        case 5:
                            keyFrameAnimationStart();
                            break;
                    }

                } else {
                    bigRotationStep(currentWheelSpeed, destination);
                    playCursorAnimation();
                }

                if (slowReel == true && rotationStep == getModel().getValuesForStop().get(winningSector)) {
                    startStoppingTime = System.currentTimeMillis();
                    slowReel = false;
                    slowWheel();
                }

            }

        }

        void playCursorAnimation() {
            TweenViewAnimation cursorAnimation = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
            cursorUnsub = cursorAnimation.getTweenStateObservable().subscribe(new CursorObserver());
            cursorAnimation.setTargetView(cursor);
            cursorAnimation.setDestinationRotation(SECTOR_ROTATION_ANGLE);

            int currentWheelSpeedCoef = (int) (BEGIN_WHEEL_SPEED / currentWheelSpeed);
            int currentCursorSpeed = 0;
            if (currentWheelSpeedCoef == 0) {
                currentCursorSpeed = (int) (currentWheelSpeed / 5);
            } else {
                currentCursorSpeed = BEGIN_CURSOR_SPEED / currentWheelSpeedCoef;
            }
            cursorAnimation.setTimeSpan(currentCursorSpeed);//correct for slow
            cursorAnimation.play();
        }

    }

    /**
     * main logic for cursor animation
     */
    class CursorObserver extends NextObserver<Integer> {

        @Override
        public void onNext(Integer integer) {
            if (integer == TweenCallback.COMPLETE) {
                cursorUnsub.unsubscribe();
                TweenViewAnimation cursorAnimation = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
                cursorAnimation.setTargetView(cursor);
                cursorAnimation.setDestinationRotation(SECTOR_ROTATION_ANGLE_END);
                cursorAnimation.setTimeSpan(SECTOR_ROTATION_TIME);
                cursorAnimation.play();
                GameEngine.current().getSoundManager().play("blip");
            }
        }
    }

    class SlowWheelTimer implements TimeoutCallback {

        @Override
        public void onTimeout() {
/*            Random ran = new Random();
            int pos = ran.nextInt(16) + 1;*/
            if (WheeleniumSepartedNetLogic.getWeelAmount() > 0) {
                getEventBus().post(new GoToStopPositionEvent());
            } else {
                slowTimeout = new Timeout(200, new SlowWheelTimer(), true);
            }
        }
    }


    /**
     * Getting event for go stop position
     */
    class GoToStopPositionObserver extends NextObserver<GoToStopPositionEvent> {

        @Override
        public void onNext(GoToStopPositionEvent goToStopPositionEvent) {

            getModel().getConvertWinSumToSector().forEach((aInt, integers) -> {
                if (winAmount / totalBet == aInt) {
                    Random ran = new Random();
                    int index = ran.nextInt(integers.size());
                    winningSector = integers.get(index);
                }
            });
            if (winningSector == 0) {
                winningSector = 6;
            }

            slowReel = true;

        }
    }

    class AnimationStateObserver extends NextObserver<AnimationState> {

        @Override
        public void onNext(AnimationState animationState) {
            if (animationState.name().equals(AnimationState.STOPPED.name())) {
                keyFrameSubscr.unsubscribe();
                fireLopp = findViewById("fireBgr" + rotationStep);
                fireLopp.gotoAndPlay(1);
                fireLopp.setVisible(true);

                //fire
                burst = findViewById("burst");
                burst.setVisible(true);
                burst.play();
                jumpStep(1);//start test animation
            }
        }
    }

    class JumpTextObserver extends NextObserver<Integer> {
        int step;

        JumpTextObserver(int step) {
            this.step = step;
        }

        @Override
        public void onNext(Integer integer) {
            if (integer == TweenCallback.COMPLETE) {
                jumpTextUnsub.unsubscribe();
                ++step;
                if (step <= 4) {
                    jumpStep(step);
                }
                if (step == 4) {
                    GameEngine.current().getSoundManager().play("outro");
                    GameEngine.current().getSoundManager().play("outro_2");
                    new Timeout(2000, () -> {
                        eventBus.post(new FreegamesEndPlateEvent());
                    }, true);

                }
            }
        }
    }

    class ResetBonusValuesObserver extends NextObserver<ResetBonusValuesEvent> {


        @Override
        public void onNext(ResetBonusValuesEvent resetBonusValuesEvent) {
            if (!lampTimeout.isCleaned()) {
                lampTimeout.clear();
                findViewById("lamp" + rotationStep).setVisible(false);

            }
            new Timeout(500, () -> {
                wheelScreen.setVisible(false);
                wheel.setRotation(BEGIN_WHEEL_OFFSET);
                wheel.setX(-1920);
                bgr.setAlpha(0);
                imageButton.setVisible(false);
                buttonStart.setEnabled(false);
            }, true);
            TweenViewAnimation hideAnimation = ((TweenViewAnimation) getAnimationFactory().createAnimation(TweenViewAnimation.class));
            hideAnimation.setTargetView(wheelScreen);
            hideAnimation.setDestinationAlpha(0);
            hideAnimation.setTimeSpan(500);
            hideAnimation.play();
            resetValues();
            keyFrameAnimationStop();
        }
    }

    class LampAnimationTimer implements TimeoutCallback {
        ViewGroup lamps;

        LampAnimationTimer(ViewGroup lamps) {
            this.lamps = lamps;
        }

        @Override
        public void onTimeout() {
            if (lamps.isVisible()) {
                lamps.setVisible(false);
            } else {
                lamps.setVisible(true);
            }
            if (!lampTimeout.isCleaned()) {
                lampTimeout.clear();
            }
            lampTimeout = new Timeout(200, new LampAnimationTimer(lamps), true);
        }
    }

    class SpinWheelPresentationObserver extends NextObserver<SpinWheelPresentation> {

        @Override
        public void onNext(SpinWheelPresentation spinWheelPresentation) {
            winAmount = spinWheelPresentation.getWinAmount();
        }
    }

}
