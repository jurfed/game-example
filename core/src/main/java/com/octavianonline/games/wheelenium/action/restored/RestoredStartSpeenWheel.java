package com.octavianonline.games.wheelenium.action.restored;

import com.atsisa.gox.framework.action.Action;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.restored.RestoredStartSpeenWheelEvent;

@Reflect_Full
public class RestoredStartSpeenWheel extends Action {
    @Override
    protected void execute() {
        eventBus.post(new RestoredStartSpeenWheelEvent());
        finish();
    }
}
