package com.octavianonline.games.wheelenium.action.html;

import com.atsisa.gox.framework.action.WaitForEventAction;
import com.octavianonline.framework.collect.events.CollectEndEvent;

public class StartBitwiseCollectWaitAction extends WaitForEventAction<CollectEndEvent> {

    public StartBitwiseCollectWaitAction() {
        super();
    }

    @Override
    protected void execute() {
        super.execute();


    }

    @Override
    protected Class<CollectEndEvent> getEventClass() {
        return CollectEndEvent.class;
    }
}