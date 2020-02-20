package com.octavianonline.games.wheelenium.screen;

import com.atsisa.gox.framework.animation.IViewAnimation;
import com.atsisa.gox.framework.animation.TweenViewAnimation;
import com.atsisa.gox.framework.view.View;
import com.gwtent.reflection.client.annotations.Reflect_Full;

@Reflect_Full
public class WheeleniumTopDownInfoScreenTransition extends WheeleniumScreenTransition {

    public WheeleniumTopDownInfoScreenTransition() {
    }

    @Override
    public void beforeTransition(int prevScreenIdx, View prevScreen, int nextScreenIdx, View nextScreen) {
        nextScreen.setY(-this.getHeight());
        nextScreen.setDepth(1);
    }

    @Override
    public IViewAnimation getTransition(int prevScreenIdx, View prevScreen, int nextScreenIdx, View nextScreen) {
        return ((TweenViewAnimation) this.getAnimationFactory().createAnimation(TweenViewAnimation.class)).setTargetView(nextScreen).setDestinationY(0.0F).setTimeSpan(0);

    }

    @Override
    public void afterTransition(int prevIdx, View prevScreen, int nextScreenIdx, View nextScreen) {
        nextScreen.setDepth(0);
    }
}
