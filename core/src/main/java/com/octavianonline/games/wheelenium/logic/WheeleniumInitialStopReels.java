package com.octavianonline.games.wheelenium.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.atsisa.gox.reels.logic.model.Reel;

public class WheeleniumInitialStopReels {

    private final static String SOUND_NAME = "ReelStop0";

    static final List<Reel> INITIAL_REELS = Arrays.asList(
            new Reel(SOUND_NAME, new ArrayList(){{add(2);add(2);add(6);}}),
            new Reel(SOUND_NAME, new ArrayList(){{add(4);add(1);add(3);}}),
            new Reel(SOUND_NAME, new ArrayList(){{add(4);add(3);add(3);}}),
            new Reel(SOUND_NAME, new ArrayList(){{add(5);add(5);add(5);}}),
            new Reel(SOUND_NAME, new ArrayList(){{add(5);add(5);add(5);}})
    );
}
