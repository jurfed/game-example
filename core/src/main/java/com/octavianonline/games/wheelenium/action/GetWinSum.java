package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.action.Action;
import com.gwtent.reflection.client.annotations.Reflect_Full;

@Reflect_Full
public class GetWinSum extends Action<GetWinSumData> {
    public static int sum = 0;

    public static boolean reset = false;

    @Override
    protected void execute() {
        reset = this.actionData.getReset();
        finish();
    }

    @Override
    public Class<GetWinSumData> getActionDataType() {
        return GetWinSumData.class;
    }
}
