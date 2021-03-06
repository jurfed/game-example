package com.octavianonline.games.wheelenium.event;

import com.gwtent.reflection.client.annotations.Reflect_Full;

@Reflect_Full
public class VolumeChangedEvent {

    /**
     * Volume value.
     */
    private int volume;

    /**
     * Initializes a new instance of the {@link VolumeChangedEvent} class.
     * @param volume int
     */
    public VolumeChangedEvent(int volume) {
        this.volume = volume;
    }

    /**
     * Gets volume value.
     * @return int
     */
    public int getVolume(){
        return volume;
    }

}
