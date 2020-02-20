package com.octavianonline.games.wheelenium.action.wheelfeature;


import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.reels.AbstractReelGame;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.framework.reels.event.WinCountStopEvent;
import com.octavianonline.games.wheelenium.action.GetWinSum;
import com.octavianonline.games.wheelenium.screen.WheelScreen;

@Reflect_Full
public class WinCountStopCustom extends Action {
    @Override
    protected void execute() {
        Long winAmount = 0L;
        if(!GetWinSum.reset){
            winAmount = ((AbstractReelGame) GameEngine.current().getGame()).getLinesModelProvider().getTotalWinAmount();
        }

        eventBus.post(new WinCountStopEvent(winAmount + WheelScreen.getWinAmount()));
        finish();
    }
}
