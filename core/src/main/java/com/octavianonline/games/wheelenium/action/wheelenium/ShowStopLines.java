package com.octavianonline.games.wheelenium.action.wheelenium;

import com.atsisa.gox.framework.action.Action;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.wheelfeature.ShowStopEvent;

@Reflect_Full
public class ShowStopLines extends Action {
    @Override
    protected void execute() {
        eventBus.post(new ShowStopEvent());
        finish();
    }
}
