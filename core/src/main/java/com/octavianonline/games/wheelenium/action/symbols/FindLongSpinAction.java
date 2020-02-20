package com.octavianonline.games.wheelenium.action.symbols;

import com.atsisa.gox.framework.action.Action;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.symbols.FindLongSpinEvent;

@Reflect_Full
public class FindLongSpinAction extends Action {
    @Override
    protected void execute() {
        eventBus.post(new FindLongSpinEvent());
        finish();
    }
}
