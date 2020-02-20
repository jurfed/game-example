package com.octavianonline.games.wheelenium.action.restore;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.bonusanimate.ResetBonusValuesEvent;
import com.octavianonline.games.wheelenium.event.restore.RestoreEnterWheelEvent;

@Reflect_Full
public class RestoreEnterWheelAction extends Action {
    @Override
    protected void execute() {
        GameEngine.current().getViewManager().getLayout("wheelScreen").getRootView().setVisible(false);
        GameEngine.current().getViewManager().redrawAll();
        eventBus.post(new RestoreEnterWheelEvent());
        finish();
    }
}
