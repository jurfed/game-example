package com.octavianonline.games.wheelenium.action.html;

import com.atsisa.gox.framework.action.Action;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.framework.collect.events.FinishCollectEvent;

@Reflect_Full
public class FinishMobileCollect extends Action {
    @Override
    protected void execute() {

            eventBus.post(new FinishCollectEvent("octavianHdMoblieCollect"));
            eventBus.post(new FinishCollectEvent("octavianHdCollect"));

        finish();
    }
}
