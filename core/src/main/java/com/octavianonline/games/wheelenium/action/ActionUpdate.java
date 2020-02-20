package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.framework.view.View;
import com.gwtent.reflection.client.annotations.Reflect_Full;

@Reflect_Full
public class ActionUpdate extends Action {
    @Override
    protected void execute() {
        View hdMobilePanel = GameEngine.current().getViewManager().findViewById("controlPanelScreen", "bottomPanelBackground");
        if(hdMobilePanel!=null && !hdMobilePanel.isVisible()){
            hdMobilePanel.setVisible(true);
        }
        finish();
    }
}
