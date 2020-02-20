package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.framework.action.ActionData;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.WinLineFrameAnimationCommand;

@Reflect_Full
public class WinLineFrameAnimationAction extends Action<WinLineFrameAnimationData> {


    @Override
    protected void execute() {
        float time = actionData.getTime();
        float scale = actionData.getScale();

        GameEngine.current().getEventBus().post(new WinLineFrameAnimationCommand(time, scale));
        finish();
    }

    @Override
    public Class<? extends ActionData> getActionDataType() {
        return WinLineFrameAnimationData.class;
    }

    @Override
    protected void terminate() {
        super.terminate();
        logger.warn("WinLineFrameAnimationAction.terminate");
    }
}
