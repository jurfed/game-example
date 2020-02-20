package com.octavianonline.games.wheelenium.action.html;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.ExecuteNextAction;
import com.atsisa.gox.reels.AbstractReelGame;
import com.atsisa.gox.reels.command.StopAutoPlayCommand;
import com.gwtent.reflection.client.annotations.Reflect_Mini;

@Reflect_Mini
public class ExecuteNextDependingOnAutoPlayFixAction extends ExecuteNextAction {

    @Override
    protected void execute() {
        allowFurtherProcessing();

        AbstractReelGame game = ((AbstractReelGame) GameEngine.current().getGame());
        long bet = game.getBetModelProvider().getBetModel().getTotalBet();
        long balance = game.getAccountModelProvider().getAccountModel().getBalance().getValue() + game.getAccountModelProvider().getAccountModel()
                .getPendingLatestWinnings().getValue();
        if (bet > balance) {
            eventBus.post(new StopAutoPlayCommand(true)); //'true' so it will play the deactivation sound
            if(!getState().equals("TERMINATED")){
                finish();
            }
        } else if (game.getReelGameStateHolder().isAutoPlay()) {
            cancelFurtherProcessing();
            super.execute();
        } else {
            if(!getState().equals("TERMINATED")){
                finish();
            }
        }
    }
}
