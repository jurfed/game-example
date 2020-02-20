package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.action.ActionData;
import com.atsisa.gox.framework.serialization.annotation.Attribute;
import com.atsisa.gox.framework.serialization.annotation.Element;
import com.gwtent.reflection.client.annotations.Reflect_Full;

@Reflect_Full
@Element
public class WinLineFrameAnimationData extends ActionData {
    @Attribute
    private float scale;
    @Attribute
    private float time;

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
}
