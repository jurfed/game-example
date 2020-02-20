package com.octavianonline.games.wheelenium.action.wheelfeature;

import com.atsisa.gox.framework.action.Action;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.wheelfeature.ShowWheelFeatureEvent;

@Reflect_Full
public class ShowWheelFeature extends Action {

    @Override
    protected void execute() {
        eventBus.post(new ShowWheelFeatureEvent());
        finish();
    }

}