package com.octavianonline.games.wheelenium.screen;

import com.atsisa.gox.framework.animation.IAnimationFactory;
import com.atsisa.gox.framework.eventbus.IEventBus;
import com.atsisa.gox.framework.infrastructure.IViewManager;
import com.atsisa.gox.framework.rendering.IRenderer;
import com.atsisa.gox.framework.resource.IResourceManager;
import com.atsisa.gox.framework.utility.logger.ILogger;
import com.atsisa.gox.reels.ICreditsFormatter;
import com.atsisa.gox.reels.command.ShowSelectedGamblerCardCommand;
import com.atsisa.gox.reels.model.GamblerCardType;
import com.atsisa.gox.reels.screen.model.GamblerScreenModel;
import com.atsisa.gox.reels.screen.GamblerScreen;
import com.gwtent.reflection.client.annotations.Reflect_Full;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Represents the gambler screen.
 */
@Reflect_Full
public class WheeleniumGamblerScreen extends GamblerScreen {

    /**
     * Red button enabled.
     */
    private static final String RED_BUTTON_ENABLED = "redButtonEnabled";

    /**
     * Black button enabled.
     */
    private static final String BLACK_BUTTON_ENABLED = "blackButtonEnabled";

    /**
     * Black button enabled.
     */
    private static final String BLACK_BUTTON_SELECTED = "blackButtonSelected";

    /**
     * Red button enabled.
     */
    private static final String RED_BUTTON_SELECTED = "redButtonSelected";

    /**
     * Main card visible.
     */
    private static final String MAIN_CARD_VISIBLE = "mainCardVisible";

    /**
     * Animation card visible.
     */
    private static final String ANIMATION_CARD_VISIBLE = "animationCardVisible";

    /**
     * Initializes a new instance of the {@link GamblerScreen} class.
     *
     * @param layoutId         layout identifier
     * @param model            {@link GamblerScreenModel}
     * @param renderer         {@link IRenderer}
     * @param viewManager      {@link IViewManager}
     * @param animationFactory {@link IAnimationFactory}
     * @param logger           {@link ILogger}
     * @param eventBus         {@link IEventBus}
     * @param resourceManager  {@link IResourceManager}
     * @param creditsFormatter
     */
    @Inject
    public WheeleniumGamblerScreen(@Named(LAYOUT_ID_PROPERTY) String layoutId,
                                   GamblerScreenModel model, IRenderer renderer,
                                   IViewManager viewManager, IAnimationFactory animationFactory, ILogger logger, IEventBus eventBus, IResourceManager resourceManager,
                                   ICreditsFormatter creditsFormatter) {
        super(layoutId, model, renderer, viewManager, animationFactory, logger, eventBus, resourceManager, creditsFormatter);
    }


    @Override
    protected void stateChanged() {
        setModelProperty(RED_BUTTON_ENABLED, Boolean.FALSE);
        setModelProperty(BLACK_BUTTON_ENABLED, Boolean.FALSE);
        setModelProperty(BLACK_BUTTON_SELECTED, Boolean.FALSE);
        setModelProperty(RED_BUTTON_SELECTED, Boolean.FALSE);
        setModelProperty(MAIN_CARD_VISIBLE, Boolean.FALSE);

        switch (getCurrentState()) {
            case SELECT_CARD:
                setModelProperty(RED_BUTTON_ENABLED, Boolean.TRUE);
                setModelProperty(BLACK_BUTTON_ENABLED, Boolean.TRUE);
                break;
            case SELECTED_BLACK_CARD:
                setModelProperty(BLACK_BUTTON_SELECTED, Boolean.TRUE);
                break;
            case SELECTED_RED_CARD:
                setModelProperty(RED_BUTTON_SELECTED, Boolean.TRUE);
                break;
            case LOSE:
                showLatestSelectedButton(false);
                break;
            case HISTORY_LOSE:
                showLatestSelectedButton(true);
                setModelProperty(MAIN_CARD_VISIBLE, Boolean.TRUE);
                break;
            case HISTORY_WIN:
                showLatestSelectedButton(false);
                setModelProperty(MAIN_CARD_VISIBLE, Boolean.TRUE);
                break;
            default:
                getLogger().warn("Unsupported gambler state: %s", getCurrentState());
                break;
        }
    }

    @Override
    public void handleShowSelectedGamblerCardCommand(ShowSelectedGamblerCardCommand showSelectedGamblerCardCommand) {
        super.handleShowSelectedGamblerCardCommand(showSelectedGamblerCardCommand);
        setModelProperty(ANIMATION_CARD_VISIBLE, true);
        setModelProperty(MAIN_CARD_VISIBLE, Boolean.TRUE);
    }

    /**
     * Shows recently selected gambler button, based on history.
     * @param oppositeColor a boolean value that indicates whether this color should be opposite or not
     */
    private void showLatestSelectedButton(boolean oppositeColor) {
        String lastSelectedCard = getLastSelectedCard();
        boolean redButtonSelected = true;
        if (GamblerCardType.SPADES.equals(lastSelectedCard) || GamblerCardType.CLUBS.equals(lastSelectedCard)) {
            redButtonSelected = oppositeColor;
        } else if (GamblerCardType.DIAMONDS.equals(lastSelectedCard) || GamblerCardType.HEARTS.equals(lastSelectedCard)) {
            redButtonSelected = !oppositeColor;
        }
        setModelProperty(BLACK_BUTTON_SELECTED, redButtonSelected ? Boolean.FALSE : Boolean.TRUE);
        setModelProperty(RED_BUTTON_SELECTED, redButtonSelected ? Boolean.TRUE : Boolean.FALSE);
    }
}
