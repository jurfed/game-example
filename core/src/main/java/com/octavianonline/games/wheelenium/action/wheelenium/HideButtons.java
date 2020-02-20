package com.octavianonline.games.wheelenium.action.wheelenium;

import com.atsisa.gox.framework.action.Action;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.wheelfeature.HideButtonsEvent;

@Reflect_Full
public class HideButtons extends Action {
    @Override
    protected void execute() {
            eventBus.post(new HideButtonsEvent());
        finish();
    }
}
