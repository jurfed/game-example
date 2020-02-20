package com.octavianonline.games.wheelenium.action.wheelenium;

import com.atsisa.gox.framework.action.WaitForEventAction;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.jumpbonus.StartAnimation;

@Reflect_Full
public class WaitForBonus extends WaitForEventAction<StartAnimation> {

    public WaitForBonus() {
        super();
    }

    @Override
    protected void execute() {
        super.execute();
    }

    @Override
    protected Class<StartAnimation> getEventClass() {
        return StartAnimation.class;
    }
}
