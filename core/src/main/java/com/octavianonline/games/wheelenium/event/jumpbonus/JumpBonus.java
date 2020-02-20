package com.octavianonline.games.wheelenium.event.jumpbonus;

import com.gwtent.reflection.client.annotations.Reflect_Full;

@Reflect_Full
public class JumpBonus {
    String bunusID;
    Integer reelPosition;

    public JumpBonus(String bunusID, int reelPosition) {
        this.bunusID = bunusID;
        this.reelPosition = reelPosition;
    }

    public String getBunusID() {
        return bunusID;
    }

    public Integer getReelPosition() {
        return reelPosition;
    }
}
