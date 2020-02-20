package com.octavianonline.games.wheelenium.event;

import com.gwtent.reflection.client.annotations.Reflect_Full;

@Reflect_Full
public class FreegamesEndPlateEvent {
    public boolean retrigger = false;

    public FreegamesEndPlateEvent() {

    }

    public FreegamesEndPlateEvent(boolean retrigger) {
        this.retrigger = retrigger;
    }

    public boolean isRetrigger() {
        return retrigger;
    }

    public void setRetrigger(boolean retrigger) {
        this.retrigger = retrigger;
    }
}
