package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.framework.infrastructure.ISoundManager;
import com.atsisa.gox.framework.utility.Timeout;
import com.atsisa.gox.reels.model.ILinesModelProvider;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.screen.SymbolsAnimationScreen;

@Reflect_Full
public class PlayWinLinesSoundAction extends Action {

    protected Timeout timeout;

    /**
     * Sound manager reference.
     */
    protected ISoundManager soundManager;

    /**
     * Reference to lines model provider.
     */
    protected ILinesModelProvider linesModelProvider;

    /**
     * Construct action with sound manager given as parameter and line model.
     * @param linesModelProvider {@link ILinesModelProvider}
     * @param soundManager       {@link ISoundManager}
     */
    public PlayWinLinesSoundAction(ILinesModelProvider linesModelProvider, ISoundManager soundManager) {
        this.soundManager = soundManager;
        this.linesModelProvider = linesModelProvider;
    }

    /**
     * Construct action with default sound manager and line model.
     */
    public PlayWinLinesSoundAction() {
//        this(((AbstractReelGame) GameEngine.current().getGame()).getLinesModelProvider(), GameEngine.current().getSoundManager());
    }


    @Override
    protected void execute() {
        playSound(SymbolsAnimationScreen.getBiggetWinLineSound());
    }

    protected String soundId;


    protected void playSound(String soundId) {
        this.soundManager = GameEngine.current().getSoundManager();
        this.soundId = soundId;
        final int time = this.soundManager.length(soundId);
        soundManager.play(soundId);
        timeout = new Timeout(time, this::finish, true);
    }

    @Override
    protected void finish() {
        if (soundId != null && !soundId.isEmpty() && soundManager.isPlaying(soundId)) {
            soundManager.stop(soundId);
        }
        super.finish();
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
