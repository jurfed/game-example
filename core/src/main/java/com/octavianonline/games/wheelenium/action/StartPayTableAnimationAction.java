package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.reels.AbstractReelGame;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.StartPayTableAnimationEvent;

import java.util.List;


public class StartPayTableAnimationAction extends Action {
    @Override
    protected void execute() {
        List<Iterable<String>> stopListSymbols = ((AbstractReelGame) GameEngine.current().getGame()).getReelGameStateHolder().getStoppedSymbols();
        eventBus.post(new StartPayTableAnimationEvent(stopListSymbols));
        finish();
    }
}
