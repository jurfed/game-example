package com.octavianonline.games.wheelenium.action.wheelfeature;

import com.atsisa.gox.framework.action.WaitForEventAction;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.wheelfeature.HideWheelFeatureEvent;

@Reflect_Full
public class WaitWheelfeatureAction extends WaitForEventAction<HideWheelFeatureEvent> {

    public WaitWheelfeatureAction() {
        super();
    }

    @Override
    protected void execute() {
        super.execute();
    }

    @Override
    protected Class<HideWheelFeatureEvent> getEventClass() {
        return HideWheelFeatureEvent.class;
    }
}
