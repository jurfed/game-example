package com.octavianonline.games.wheelenium.screen;

import com.atsisa.gox.framework.eventbus.IEventBus;
import com.atsisa.gox.framework.utility.localization.ITranslator;
import com.atsisa.gox.reels.ICreditsFormatter;
import com.atsisa.gox.reels.model.IPayTableModelItem;
import com.atsisa.gox.reels.model.IPayTableModelProvider;
import com.atsisa.gox.reels.model.PayTableModelItemType;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.framework.reels.screens.OPayTableScreenModel;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Reflect_Full
public class WheeleniumPayTableScreenModel extends OPayTableScreenModel {

    private static final String FULL_SEVEN = "superSeven";
    ITranslator translator;

    @Inject
    public WheeleniumPayTableScreenModel(ITranslator translator, ICreditsFormatter creditsFormatter, IEventBus eventBus, IPayTableModelProvider payTableModelProvider) {
        super(translator, creditsFormatter, eventBus, payTableModelProvider);
        this.translator = translator;
    }


    @Override
    public void updatePayTableValues(List<IPayTableModelItem> payTableItems) {
        final Optional<IPayTableModelItem> seven_x_5 =
                payTableItems.stream().filter(iPayTableModelItem -> iPayTableModelItem.getName().equalsIgnoreCase("SEVEN_X_5")).findFirst();
        if (seven_x_5.isPresent()) {
            final IPayTableModelItem item = seven_x_5.get();
            payTableItems.add(new IPayTableModelItem() {
                @Override
                public String getName() {
                    return FULL_SEVEN;
                }

                @Override
                public long getValue() {
                    return item.getValue() * 40;
                }

                @Override
                public PayTableModelItemType getType() {
                    return item.getType();
                }
            });
        }
        super.updatePayTableValues(payTableItems);
    }

    public ITranslator getTranslator() {
        return translator;
    }
}
