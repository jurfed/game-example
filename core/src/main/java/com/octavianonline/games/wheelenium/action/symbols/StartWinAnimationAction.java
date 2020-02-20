package com.octavianonline.games.wheelenium.action.symbols;

import com.atsisa.gox.framework.action.Action;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.symbols.StartWinAnimationEvent;

@Reflect_Full
public class StartWinAnimationAction extends Action {
    @Override
    protected void execute() {
        eventBus.post(new StartWinAnimationEvent());
        finish();
    }
}
