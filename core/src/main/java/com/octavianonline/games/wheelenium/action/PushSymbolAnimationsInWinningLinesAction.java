package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.SyncAction;
import com.atsisa.gox.reels.AbstractReelGame;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.PushSymbolAnimationsInWinningLinesCommand;

import java.util.List;

@Reflect_Full
public class PushSymbolAnimationsInWinningLinesAction extends SyncAction {

    private List<Iterable<String>> stoppedSymbols;

    @Override
    protected void grabData() {
        super.grabData();
        AbstractReelGame game = (AbstractReelGame) GameEngine.current().getGame();
        this.stoppedSymbols  = game.getReelGameStateHolder().getStoppedSymbols();
    }

    @Override
    protected void execute() {
        eventBus.post(new PushSymbolAnimationsInWinningLinesCommand(stoppedSymbols));
        finish();
    }
}
