package com.octavianonline.games.wheelenium.logic;

import com.atsisa.gox.logic.calculator.HitCheckResult;
import com.atsisa.gox.logic.calculator.IHitChecker;
import com.atsisa.gox.logic.model.Symbol;

public class WildHitChecker implements IHitChecker {

    private static final String WILD_SYMBOL = "WildSymbol";
    private static final String SCATTER_SYMBOL = "ScatterSymbol";

    @Override
    public HitCheckResult check(Symbol sequenceSymbol, Symbol stopSymbol) {
        return new HitCheckResult(false, (stopSymbol.getType().equals(WILD_SYMBOL) && !sequenceSymbol.getType().equals(SCATTER_SYMBOL)));
    }
}