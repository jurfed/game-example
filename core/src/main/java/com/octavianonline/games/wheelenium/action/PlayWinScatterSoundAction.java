package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.framework.infrastructure.ISoundManager;
import com.atsisa.gox.framework.utility.Timeout;
import com.atsisa.gox.reels.AbstractReelGame;
import com.atsisa.gox.reels.IWinLineInfo;
import com.atsisa.gox.reels.model.ILinesModelProvider;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.framework.reels.utils.GameHelper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Reflect_Full
public class PlayWinScatterSoundAction extends Action {

    protected Timeout timeout;

    /**
     * Sound manager reference.
     */
    protected ISoundManager soundManager;

    /**
     * Reference to lines model provider.
     */
    protected ILinesModelProvider linesModelProvider;
    protected String soundId;

    /**
     * Construct action with sound manager given as parameter and line model.
     *
     * @param linesModelProvider {@link ILinesModelProvider}
     * @param soundManager       {@link ISoundManager}
     */
    public PlayWinScatterSoundAction(ILinesModelProvider linesModelProvider, ISoundManager soundManager) {
        this.soundManager = soundManager;
        this.linesModelProvider = linesModelProvider;
    }

    /**
     * Construct action with default sound manager and line model.
     */
    public PlayWinScatterSoundAction() {
        this(((AbstractReelGame) GameEngine.current().getGame()).getLinesModelProvider(), GameEngine.current().getSoundManager());
    }

    @Override
    protected void execute() {
        AbstractReelGame game = (AbstractReelGame) GameEngine.current().getGame();
        final List<? extends IWinLineInfo> winLines = GameHelper.toList(game.getLinesModelProvider().getLinesModel().getWinningLines());
        final Optional<? extends IWinLineInfo> first = winLines.stream().filter(o -> o.getLineNumber() == 0).findFirst();
        if (first.isPresent()) {
            IWinLineInfo iWinLineInfo = first.get();
            List<Integer> collect = StreamSupport.stream(iWinLineInfo.getPositions().spliterator(), false)
                    .filter(integer -> integer >= 0).collect(Collectors.toList());
            int size = collect.size();
            String soundName;
            switch (size) {
                case 3:
                    soundName = "win_oneadmi";
                    break;
                case 4:
                    soundName = "win_twoadmi";
                    break;
                case 5:
                    soundName = "win_threeadmi";
                    break;
                default:
                    throw new RuntimeException("Unknown scatter number");
            }
            playSound(soundName);
        } else {
            finish();
        }
    }

    protected void playSound(String soundId) {
        this.soundId = soundId;
        final int time = this.soundManager.length(soundId);
        soundManager.play(soundId);
        timeout = new Timeout(time, this::finish, true);
    }


    @Override
    protected void terminate() {
        if (timeout != null && !timeout.isCleaned()) {
            timeout.clear();
        }
        if (soundId != null && soundManager.isPlaying(soundId)) {
            soundManager.stop(soundId);
        }
        super.terminate();
    }
}
