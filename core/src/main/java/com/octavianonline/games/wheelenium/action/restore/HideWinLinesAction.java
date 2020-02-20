package com.octavianonline.games.wheelenium.action.restore;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.framework.view.KeyframeAnimationView;
import com.atsisa.gox.framework.view.View;
import com.atsisa.gox.framework.view.ViewGroup;
import com.atsisa.gox.reels.view.ReelGroupView;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.restore.HideWinLinesEvent;

@Reflect_Full
public class HideWinLinesAction extends Action {
    @Override
    protected void execute() {
        eventBus.post(new HideWinLinesEvent());
        ReelGroupView reels = GameEngine.current().getViewManager().findViewById("baseGameScreen", "reelGroupView");
        reels.getReels().forEach(abstractReel -> {
            abstractReel.getDisplayedSymbols().forEach(abstractSymbol -> {
                abstractSymbol.setVisible(true);
                abstractSymbol.setState("IDLE");
            });
        });


        ViewGroup symbolsAnimation = GameEngine.current().getViewManager().findViewById("symbolsAnimationScreen","allSymbols");
        symbolsAnimation.getChildren().forEach(view -> {
            ((ViewGroup)view).getChildren().forEach(view1 -> {
                ((KeyframeAnimationView)view1).stop();
                ((KeyframeAnimationView)view1).setVisible(false);
            });
        });

        finish();
    }
}
