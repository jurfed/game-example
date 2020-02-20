package com.octavianonline.games.wheelenium.action.html;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.reels.AbstractReelGame;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.framework.collect.events.InitBitwiseCollectEvent;
import com.octavianonline.games.wheelenium.action.GetWinSum;
import com.octavianonline.games.wheelenium.screen.WheelScreen;

@Reflect_Full
public class InitHtmlCollectAction extends Action {
    @Override
    protected void execute() {
        Long winAmount = 0L;
        WheelScreen.getWinAmount();
//        Long winAmount = ((AbstractReelGame) GameEngine.current().getGame()).getAccountModelProvider().getAccountModel().getLatestWinnings();
        if(!GetWinSum.reset){
            winAmount = ((AbstractReelGame) GameEngine.current().getGame()).getLinesModelProvider().getTotalWinAmount();
        }

        GameEngine.current().getEventBus().post(
                new InitBitwiseCollectEvent(
                        0L,
                        winAmount,
                        60,
                        "octavianHdMoblieCollect"
                ));

        GameEngine.current().getEventBus().post(
                new InitBitwiseCollectEvent(
                        0L,
                        winAmount,
                        60,
                        "octavianHdCollect"
                ));
        finish();
    }
}
