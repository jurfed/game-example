package com.octavianonline.games.wheelenium.action.symbols;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.framework.utility.Timeout;
import com.atsisa.gox.reels.view.ReelGroupView;
import com.gwtent.reflection.client.annotations.Reflect_Full;

@Reflect_Full
public class SetIdleState extends Action {
    @Override
    protected void execute() {
        ReelGroupView reelGroupView = GameEngine.current().getViewManager().findViewById("baseGameScreen","reelGroupView");

        new Timeout(200, ()->{            reelGroupView.getReels().forEach(abstractReel -> {
            abstractReel.getDisplayedSymbols().forEach(abstractSymbol -> {
                if(abstractSymbol.getName().equals("BONUS")){
                    abstractSymbol.setVisible(false);
                }
            });
        });},true);

        finish();
    }
}
