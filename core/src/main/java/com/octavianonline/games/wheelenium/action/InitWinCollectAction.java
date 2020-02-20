package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.framework.infrastructure.ISoundManager;
import com.atsisa.gox.reels.AbstractReelGame;
import com.atsisa.gox.reels.ILinesModel;
import com.atsisa.gox.reels.IWinLineInfo;
import com.atsisa.gox.reels.model.ILinesModelProvider;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.framework.reels.event.StartControlPanelWinCollectCommand;
import com.octavianonline.framework.reels.utils.GameHelper;
import com.octavianonline.games.wheelenium.event.html.ShowWinSumForHtml;

import java.util.List;
import java.util.stream.Collectors;

@Reflect_Full
public class InitWinCollectAction extends Action {


    @Override
    protected void execute() {
        final ILinesModelProvider linesModelProvider = ((AbstractReelGame) GameEngine.current().getGame()).getLinesModelProvider();
        final long totalWinAmount = linesModelProvider.getTotalWinAmount();
        final ILinesModel linesModel = linesModelProvider.getLinesModel();
        final int linesAmount = GameHelper.toList(linesModel.getWinningLines()).size();

        int time = 0;
        ISoundManager soundManager = GameEngine.current().getSoundManager();

        final List<? extends IWinLineInfo> winlines = GameHelper.toList(linesModel.getWinningLines());
        final List<? extends IWinLineInfo> scatterLines = winlines.stream().filter(o -> o.getLineNumber() == 0).collect(Collectors.toList());
        if (scatterLines.size() != 0 && winlines.size() < 2) {
            final IWinLineInfo scatterLine = scatterLines.get(0);
            int count = 0;
            for (Integer integer : scatterLine.getPositions()) {
                if (integer >= 0) {
                    count++;
                }
            }
//            String soundId = count == 3 ? "win_oneadmi" : count == 4 ? "win_twoadmi" : "win_threeadmi";
//            time = soundManager.length(soundId);
            time = 0;

        } else {
            int x = 0;
            for (int i = 0; i < linesAmount; i++) {
                x = (int) ((-0.00278f * i + 0.21647f) * 1000);
                time = time + x;
            }
//            int ff = ((579 * time) / 6329) - (199098 / 6329);
//            time += (ff + x);
        }

        eventBus.post(new StartControlPanelWinCollectCommand(time,0, totalWinAmount));
        eventBus.post(new ShowWinSumForHtml(totalWinAmount, true));
        finish();
    }
}
