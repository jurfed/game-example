package com.octavianonline.games.wheelenium.action.wheelfeature;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.reels.AbstractReelGame;
import com.atsisa.gox.reels.command.ChangeBalanceCommand;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.framework.reels.event.FinishControlPanelWinCollectCommand;
import com.octavianonline.games.wheelenium.action.GetWinSum;
import com.octavianonline.games.wheelenium.logic.WheeleniumSepartedNetLogic;
import com.octavianonline.games.wheelenium.screen.WheelScreen;

@Reflect_Full
public class TransferWinCustom extends Action {
    @Override
    protected synchronized void execute() {
        Long totalWinAmount = 0L;
        Long winSumBeforeRestoreStart = WheeleniumSepartedNetLogic.getWinGameBeforeRestore();
        Long wheelSum = WheeleniumSepartedNetLogic.getWeelAmount();
        Long winAmount = ((AbstractReelGame) GameEngine.current().getGame()).getLinesModelProvider().getTotalWinAmount();

        if(winAmount == 0){
            totalWinAmount = winSumBeforeRestoreStart + wheelSum;
        }else{
            totalWinAmount = winAmount + wheelSum;
        }

        if (totalWinAmount > 0) {
            this.eventBus.post(new ChangeBalanceCommand(totalWinAmount, null));
        }

        this.finish();
    }
}
