package com.octavianonline.games.wheelenium.action.html;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.reels.AbstractReelGame;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.framework.collect.events.InitBitwiseCollectEvent;
import com.octavianonline.framework.reels.event.StartControlPanelWinCollectCommand;
import com.octavianonline.games.wheelenium.action.GetWinSum;
import com.octavianonline.games.wheelenium.event.html.ShowWinSumForHtml;
import com.octavianonline.games.wheelenium.logic.WheeleniumSepartedNetLogic;
import com.octavianonline.games.wheelenium.screen.WheelScreen;

@Reflect_Full
public class WheelCollectInit extends Action {
    @Override
    protected void execute() {
        Long winAmount = 0L;
        WheelScreen.getWinAmount();
//        Long winAmount = ((AbstractReelGame) GameEngine.current().getGame()).getAccountModelProvider().getAccountModel().getLatestWinnings();
        if(!GetWinSum.reset){
            winAmount = ((AbstractReelGame) GameEngine.current().getGame()).getLinesModelProvider().getTotalWinAmount();
        }
        Long winSumBeforeRestoreStart = WheeleniumSepartedNetLogic.getWinGameBeforeRestore();
        Long wheelSum = WheeleniumSepartedNetLogic.getWeelAmount();
        GameEngine.current().getEventBus().post(
                new InitBitwiseCollectEvent(
                        winSumBeforeRestoreStart,
                        winSumBeforeRestoreStart + wheelSum,
                        60,
                        "octavianHdMoblieCollect"
                ));

        GameEngine.current().getEventBus().post(
                new InitBitwiseCollectEvent(
                        winSumBeforeRestoreStart,
                        winSumBeforeRestoreStart + wheelSum,
                        60,
                        "octavianHdCollect"
                ));
        finish();
    }
}
