package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.action.Action;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.ResetLongSpinEvent;

@Reflect_Full
public class ResetLongSpinAction extends Action {
    @Override
    protected void execute() {
        eventBus.post(new ResetLongSpinEvent());
        finish();
    }
}
