package com.octavianonline.games.wheelenium.action.html;

import com.atsisa.gox.framework.action.Action;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.framework.collect.events.ShowCollectEvent;
import com.octavianonline.games.wheelenium.logic.WheeleniumSepartedNetLogic;

@Reflect_Full
public class ShowWheelCollectRestore extends Action {
    @Override
    protected void execute() {
        Long winSumBeforeRestore = WheeleniumSepartedNetLogic.getWinGameBeforeRestore();
        if (winSumBeforeRestore != null && winSumBeforeRestore > 0) {
            eventBus.post(new ShowCollectEvent(winSumBeforeRestore, "octavianHdMoblieCollect"));
            eventBus.post(new ShowCollectEvent(winSumBeforeRestore, "octavianHdCollect"));
        }
        finish();
    }
}
