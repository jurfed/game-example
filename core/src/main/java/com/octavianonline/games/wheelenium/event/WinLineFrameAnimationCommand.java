package com.octavianonline.games.wheelenium.event;

import com.gwtent.reflection.client.annotations.Reflect_Full;

@Reflect_Full
public class WinLineFrameAnimationCommand {
    private float time;
    private float scale;

    public WinLineFrameAnimationCommand(float time, float scale) {
        this.time = time;
        this.scale = scale;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
