package com.octavianonline.games.wheelenium.screen;

/*@Reflect_Full
public class FinestBlend40StandbyScreen extends OctavianStandbyScreen {

    public static final String LAYOUT_ID_PROPERTY = "standbyScreenLayoutId";
    private static final String SCREEN_ID = "standbyScreen";

    @Inject
    public FinestBlend40StandbyScreen(@Named(FinestBlend40StandbyScreen.LAYOUT_ID_PROPERTY) String layoutId,
                                    ScreenModel model, IRenderer renderer, IViewManager viewManager,
                                    IAnimationFactory animationFactory, ILogger logger, IEventBus eventBus) {
        super(layoutId, model, renderer, viewManager, animationFactory, logger, eventBus);
//        setStartingDelay(2000);
    }

    @Override
    public void constructScenes() {
        try {
            if (scenes == null || scenes.size() == 0) {
                this.scenes = new ArrayList<>();
                scenes.add(new OctavianSingleMovieScene(getViewManager(), getEventBus(),
                        scenes.size(), "logo", SCREEN_ID));
                scenes.add(new FinestBlend40FieldOfWildsScene(getViewManager(), getEventBus(),
                        scenes.size(), "fieldOfWilds", SCREEN_ID));
                scenes.add(new OctavianSingleMovieScene(getViewManager(), getEventBus(),
                        scenes.size(), "logo", SCREEN_ID));
                scenes.add(new FinestBlend40WildScene(getViewManager(), getEventBus(),
                        scenes.size(), "wild", SCREEN_ID));
                scenes.add(new OctavianSingleMovieScene(getViewManager(), getEventBus(),
                        scenes.size(), "logo", SCREEN_ID));
                scenes.add(new FinestBlend40LuckyWinScene(getViewManager(), getEventBus(),
                        scenes.size(), "luckyWin", SCREEN_ID));
                scenes.add(new OctavianSingleMovieScene(getViewManager(), getEventBus(),
                        scenes.size(), "logo", SCREEN_ID));
                scenes.add(new OctavianEmptyScene(getViewManager(), getEventBus(),
                        scenes.size(), "empty", SCREEN_ID));
            }
        } catch (javax.xml.bind.ValidationException e) {
            e.printStackTrace();
        }
    }
}*/
