package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.framework.action.ActionData;
import com.gwtent.reflection.client.annotations.Reflect_Full;

//@Reflect_Full
public class TestAction extends Action<TestActionData> {

    private String message;

    @Override
    protected void grabData() {
        message = this.actionData.getMessage();
    }

    @Override
    protected void execute() {
        System.err.println("!!!!!!!!!!!!!!!!!!!!" + message);
        finish();
    }

    @Override
    public Class<? extends ActionData> getActionDataType() {
        return TestActionData.class;
    }
}
