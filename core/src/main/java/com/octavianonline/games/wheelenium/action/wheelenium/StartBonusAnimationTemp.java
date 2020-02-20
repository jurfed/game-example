package com.octavianonline.games.wheelenium.action.wheelenium;

import com.atsisa.gox.framework.action.Action;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.bonusanimate.StartBonusAnimateEvent;

@Reflect_Full
public class StartBonusAnimationTemp extends Action {
    @Override
    protected void execute() {
        eventBus.post(new StartBonusAnimateEvent());
        finish();
    }
}
