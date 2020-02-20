package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.action.ActionData;
import com.atsisa.gox.framework.serialization.annotation.Attribute;
import com.atsisa.gox.framework.serialization.annotation.Element;
import com.gwtent.reflection.client.annotations.Reflect_Full;

@Reflect_Full
@Element
public class GetWinSumData extends ActionData {
    @Attribute
    Boolean reset = false;

    public Boolean getReset() {
        return reset;
    }

    public void setReset(Boolean reset) {
        this.reset = reset;
    }
}
