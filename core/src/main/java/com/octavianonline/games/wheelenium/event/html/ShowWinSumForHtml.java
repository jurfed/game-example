package com.octavianonline.games.wheelenium.event.html;

import com.gwtent.reflection.client.annotations.Reflect_Full;

@Reflect_Full
public class ShowWinSumForHtml {
    Long winSum;
    Boolean visible;

    public ShowWinSumForHtml(Long sum, Boolean visible) {
        this.winSum = sum;
        this.visible = visible;
    }

    public Long getWinSum() {
        return winSum;
    }

    public void setWinSum(Long winSum) {
        this.winSum = winSum;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}
