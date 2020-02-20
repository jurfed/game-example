package com.octavianonline.games.wheelenium.screen;

import com.atsisa.gox.framework.animation.IAnimationFactory;
import com.atsisa.gox.framework.animation.IViewAnimation;
import com.atsisa.gox.framework.view.View;
import com.atsisa.gox.reels.screen.InfoScreen;
import com.atsisa.gox.reels.screen.transition.InfoScreenTransition;
import com.gwtent.reflection.client.annotations.Reflect_Full;

@Reflect_Full
public abstract class WheeleniumScreenTransition extends InfoScreenTransition {
    public static final float DEFAULT_TRANSITION_TIMESPAN = 400.0F;
    private InfoScreen infoScreen;
    private IAnimationFactory animationFactory;

    public WheeleniumScreenTransition() {
    }

    public abstract void beforeTransition(int var1, View var2, int var3, View var4);

    public abstract IViewAnimation getTransition(int var1, View var2, int var3, View var4);

    public abstract void afterTransition(int var1, View var2, int var3, View var4);

    public InfoScreen getInfoScreen() {
        return this.infoScreen;
    }

    public void setInfoScreen(InfoScreen infoScreen) {
        this.infoScreen = infoScreen;
    }

    public IAnimationFactory getAnimationFactory() {
        return this.animationFactory;
    }

    public void setAnimationFactory(IAnimationFactory animationFactory) {
        this.animationFactory = animationFactory;
    }

    protected float getHeight() {
        return this.infoScreen.getLayout().getRootView().getHeight();
    }

    protected float getWidth() {
        return this.infoScreen.getLayout().getRootView().getWidth();
    }
}