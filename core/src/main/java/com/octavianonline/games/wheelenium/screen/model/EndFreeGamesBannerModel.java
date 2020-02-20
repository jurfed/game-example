package com.octavianonline.games.wheelenium.screen.model;

import com.atsisa.gox.framework.screen.model.ScreenModel;
import com.atsisa.gox.framework.utility.localization.ITranslator;
import com.gwtent.reflection.client.annotations.Reflect_Full;

import javax.inject.Inject;

@Reflect_Full
public class EndFreeGamesBannerModel extends ScreenModel {
    ITranslator translator;

    @Inject
    public EndFreeGamesBannerModel(ITranslator translator) {
        super(translator);
        this.translator = translator;
    }

    public ITranslator getTranslator() {
        return translator;
    }
}
