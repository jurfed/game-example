package com.octavianonline.games.wheelenium.action.restore;

import com.atsisa.gox.framework.action.Action;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.restore.RestoreBetEventForWheelEvent;

@Reflect_Full
public class RestoreBetForWheelAction extends Action {
    @Override
    protected void execute() {
        eventBus.post(new RestoreBetEventForWheelEvent());
        finish();
    }
}
