package com.octavianonline.games.wheelenium.action.wheelfeature;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.reels.AbstractReelGame;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.framework.reels.event.StartControlPanelWinCollectCommand;
import com.octavianonline.games.wheelenium.action.GetWinSum;
import com.octavianonline.games.wheelenium.event.html.ShowWinSumForHtml;
import com.octavianonline.games.wheelenium.screen.WheelScreen;

@Reflect_Full
public class InitCollectAction extends Action {
    @Override
    protected void execute() {
        Long winAmount = 0L;
        WheelScreen.getWinAmount();
//        Long winAmount = ((AbstractReelGame) GameEngine.current().getGame()).getAccountModelProvider().getAccountModel().getLatestWinnings();
       if(!GetWinSum.reset){
           winAmount = ((AbstractReelGame) GameEngine.current().getGame()).getLinesModelProvider().getTotalWinAmount();
       }

        int collectTime = 1000;
        eventBus.post(new StartControlPanelWinCollectCommand(collectTime, winAmount, WheelScreen.getWinAmount()));
        eventBus.post(new ShowWinSumForHtml(winAmount + WheelScreen.getWinAmount(), true));
        finish();
    }
}
