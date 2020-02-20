package com.octavianonline.games.wheelenium.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import com.atsisa.gox.framework.utility.StringUtility;
import com.atsisa.gox.logic.GameLogicException;
import com.atsisa.gox.logic.model.*;
import com.atsisa.gox.logic.provider.IPayTableProvider;
import com.atsisa.gox.logic.provider.IReelsDescriptorProvider;
import com.atsisa.gox.logic.provider.IReelsGameConfigurationProvider;
import com.atsisa.gox.logic.provider.IScatterConfigurationProvider;
import com.atsisa.gox.reels.logic.model.GameConfiguration;
import com.atsisa.gox.reels.logic.model.PayTableItem;
import com.gwtent.reflection.client.annotations.Reflect_Full;

@Reflect_Full
public class GameConfigurationAdapter {

    private static final int GAMBLER_PLAY_WIN = 5;

    private static final long GAMBLER_WIN_LIMIT = 100000;

//    private static final String CURRENCY_CODE = "USD";
    private static final String CURRENCY_CODE = "\u20AC";

    private static final String YIELD = "92.04";

    private static final String WIN_TYPE = "Line";
    private static final String SCATTER_WIN = "Scatter";

    private final IReelsGameConfigurationProvider reelGameConfigurationProvider;

    private final IPayTableProvider payTableProvider;

    private final IReelsDescriptorProvider reelsDescriptorProvider;

    private final IScatterConfigurationProvider scatterConfigurationProvider;

    @Inject
    public GameConfigurationAdapter(IReelsGameConfigurationProvider reelGameConfigurationProvider, IPayTableProvider payTableProvider,
                                    IReelsDescriptorProvider reelsDescriptorProvider, IScatterConfigurationProvider scatterConfigurationProvider) {
        this.reelGameConfigurationProvider = reelGameConfigurationProvider;
        this.payTableProvider = payTableProvider;
        this.reelsDescriptorProvider = reelsDescriptorProvider;
        this.scatterConfigurationProvider = scatterConfigurationProvider;
    }

    public GameConfiguration getGameConfiguration() throws GameLogicException {
        List<PayTableItem> gamePresentationPaytable = new ArrayList<>();

        if (!reelGameConfigurationProvider.get().isPresent()) {
            throw new GameLogicException("Reels game configuration model is missing");
        }
        ReelsGameConfiguration gameConfiguration = reelGameConfigurationProvider.get().get();

        if (!payTableProvider.get().isPresent()) {
            throw new GameLogicException("Pay table model is missing");
        }
        PayTable payTable = payTableProvider.get().get();


        final List<WinDescriptor> winDescriptors = scatterConfigurationProvider.get().get().getWinDescriptors();

        for (LineWinDescriptor winLineDescriptor : payTable.getWinDescriptors()) {
            gamePresentationPaytable.add(new PayTableItem(StringUtility.format("%s_X_%s", winLineDescriptor.getWinDescriptor().getSymbol().getName().toUpperCase(),
                    winLineDescriptor.getWinDescriptor().getSymbolsAmount()), winLineDescriptor.getWinDescriptor().getScore(), WIN_TYPE));
        }

        for (WinDescriptor winDescriptor : winDescriptors) {
            gamePresentationPaytable.add(new PayTableItem(StringUtility.format("%s_X_%s", winDescriptor.getSymbol().getName().toUpperCase(),
                    winDescriptor.getSymbolsAmount()), winDescriptor.getScore(), SCATTER_WIN));
        }

        BetStepsDescriptor betStepsDescriptor = gameConfiguration.getBetStepsModel().getBetStepsDescriptors().get(0);
        int maxBetIndex = betStepsDescriptor.getBetSteps().size() -1;
        long maxBet = betStepsDescriptor.getBetSteps().get(maxBetIndex);

        return new GameConfiguration(gameConfiguration.getLineSteps(), betStepsDescriptor.getBetSteps(), maxBet, gameConfiguration.getMaxWinLimit(),
                GAMBLER_PLAY_WIN, 5, 2, GAMBLER_WIN_LIMIT, gamePresentationPaytable, CURRENCY_CODE, YIELD, createSymbolsMap());
    }

    private Map<Integer, String> createSymbolsMap() throws GameLogicException {
        if (!reelsDescriptorProvider.getReelsDescriptor().isPresent()) {
            throw new GameLogicException("Reels descriptor is missing");
        }

        ReelsDescriptor reelsDescriptor = reelsDescriptorProvider.getReelsDescriptor().get();
        Map<Integer, String> symbolMap = new HashMap<>();

        for (ReelStrip reelStrip : reelsDescriptor.getReelStrips()) {
            for (Symbol symbol : reelStrip.getSymbols()) {
                symbolMap.putIfAbsent(symbol.getId(), symbol.getName());
            }
        }

        return symbolMap;
    }
}
