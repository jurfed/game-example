package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.SyncAction;
import com.atsisa.gox.reels.AbstractReelGame;
import com.atsisa.gox.reels.IWinLineInfo;
import com.atsisa.gox.reels.logic.model.WinLineInfo;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.framework.reels.event.HideAnimationUnderWinLineCommand;

import java.util.Optional;

@Reflect_Full
public class HideSymbolsUnderWinLineAction extends SyncAction {
    @Override
    protected void execute() {
        AbstractReelGame game = (AbstractReelGame) GameEngine.current().getGame();
        Optional<IWinLineInfo> currentWinningLine = game.getLinesModelProvider().getLinesModel().getCurrentWinningLine();
        GameEngine.current().getEventBus().post(new HideAnimationUnderWinLineCommand((WinLineInfo) currentWinningLine.get()));
        finish();
    }
}
