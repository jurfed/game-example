package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.framework.exception.ValidationException;
import com.atsisa.gox.framework.infrastructure.ISoundManager;
import com.atsisa.gox.framework.utility.Timeout;
import com.atsisa.gox.reels.AbstractReelGame;
import com.atsisa.gox.reels.IWinLineInfo;
import com.atsisa.gox.reels.model.ILinesModelProvider;
import com.gwtent.reflection.client.annotations.Reflect_Full;

import java.util.Optional;

@Reflect_Full
public class PlayWinLineSoundAction extends Action {

    /**
     * Sound manager reference.
     */
    private ISoundManager soundManager;

    /**
     * Current win line sound id.
     */
    private String soundId;


    /**
     * Reference to sound timeout.
     */
    private Timeout timeout;

    /**
     * Reference to lines model provider.
     */
    private ILinesModelProvider linesModelProvider;

    /**
     * Construct action with default sound manager and line model.
     */
    public PlayWinLineSoundAction() {
        this(((AbstractReelGame) GameEngine.current().getGame()).getLinesModelProvider(), GameEngine.current().getSoundManager());
    }

    /**
     * Construct action with sound manager given as parameter and line model.
     *
     * @param linesModelProvider {@link ILinesModelProvider}
     * @param soundManager       {@link ISoundManager}
     */
    public PlayWinLineSoundAction(ILinesModelProvider linesModelProvider, ISoundManager soundManager) {
        this.soundManager = soundManager;
        soundId = null;
        this.linesModelProvider = linesModelProvider;
    }


    @Override
    protected void grabData() {
        Optional<IWinLineInfo> currentWinningLine = linesModelProvider.getLinesModel().getCurrentWinningLine();
        currentWinningLine.ifPresent(iWinLineInfo -> {
            soundId = iWinLineInfo.getSoundName();
        });
    }

    @Override
    protected void validate() throws ValidationException {
        if (soundId == null) {
            throw new ValidationException("Can not play winning line sound, because soundId was not set.");
        }
    }

    @Override
    protected void terminate() {
        if (soundId != null && !soundId.isEmpty() && soundManager.isPlaying(soundId)) {
            soundManager.stop(soundId);
        }
        soundManager.stop(soundId);
        timeout.clear();
        super.terminate();
    }

    @Override
    protected void reset() {
        if (soundId != null && !soundId.isEmpty() && soundManager.isPlaying(soundId)) {
            soundManager.stop(soundId);
        }
        if (timeout != null && !timeout.isCleaned()) {
            timeout.clear();
        }
        soundId = null;
        timeout = null;
        super.reset();
    }

    int linesSize = 0;
    @Override
    protected void execute() {
        linesSize = 0;
        linesModelProvider.getLinesModel().getWinningLines().forEach(o -> {
            linesSize++;
        });

        if(linesSize==1){
            finish();
        }else{
            String sound = soundId.replaceAll("[a-zA-Z]", "");
            final int x = Integer.parseInt(sound);
            final int time = (int) ((-0.00278f * x + 0.21647f) * 1000 + 100);
            if (timeout != null && !timeout.isCleaned()) {
                timeout.clear();
            }
            timeout = new Timeout(time, this::finish, true);
            soundManager.play(soundId); //250
        }

    }

    @Override
    protected void finish() {
        if (soundId != null && !soundId.isEmpty() && soundManager.isPlaying(soundId)) {
            //soundManager.stop(soundId);
        }
        super.finish();
    }
}
