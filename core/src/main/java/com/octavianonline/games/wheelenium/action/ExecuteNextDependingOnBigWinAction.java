package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.ExecuteNextAction;
import com.atsisa.gox.reels.AbstractReelGame;
import com.gwtent.reflection.client.annotations.Reflect_Full;


public class ExecuteNextDependingOnBigWinAction extends ExecuteNextAction {

    @Override
 protected void execute() {
        allowFurtherProcessing();
        AbstractReelGame game = (AbstractReelGame) GameEngine.current().getGame();
        long totalWinAmount = game.getLinesModelProvider().getTotalWinAmount();
        long totalBet = game.getBetModelProvider().getBetModel().getTotalBet();
        long winSum = totalWinAmount / totalBet;

        if (winSum >= 20d) {
            cancelFurtherProcessing();
            super.execute();
        } else {
            finish();
        }
    }
}