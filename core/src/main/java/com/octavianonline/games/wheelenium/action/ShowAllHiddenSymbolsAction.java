package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.action.Action;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.event.ShowAllHiddenSymbolsCommand;

@Reflect_Full
public class ShowAllHiddenSymbolsAction extends Action {
    @Override
    protected void execute() {
        eventBus.post(new ShowAllHiddenSymbolsCommand());
        finish();
    }
}
