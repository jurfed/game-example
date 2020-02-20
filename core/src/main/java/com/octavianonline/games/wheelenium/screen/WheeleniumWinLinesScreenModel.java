package com.octavianonline.games.wheelenium.screen;

import com.atsisa.gox.framework.screen.model.ScreenModel;
import com.atsisa.gox.framework.utility.localization.ITranslator;
import com.gwtent.reflection.client.annotations.Reflect_Full;

import javax.inject.Inject;

@Reflect_Full
public class WheeleniumWinLinesScreenModel extends ScreenModel {

    @Inject
    public WheeleniumWinLinesScreenModel(ITranslator translator) {
        super(translator);
    }
}
