package com.octavianonline.games.wheelenium.action;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.AbstractActionModule;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.games.wheelenium.action.html.*;
import com.octavianonline.games.wheelenium.action.restore.HideWinLinesAction;
import com.octavianonline.games.wheelenium.action.restore.RestoreBetForWheelAction;
import com.octavianonline.games.wheelenium.action.restore.RestoreEnterWheelAction;
import com.octavianonline.games.wheelenium.action.restored.RestoredStartSpeenWheel;
import com.octavianonline.games.wheelenium.action.symbols.FindLongSpinAction;
import com.octavianonline.games.wheelenium.action.symbols.SetIdleState;
import com.octavianonline.games.wheelenium.action.symbols.StartWinAnimationAction;
import com.octavianonline.games.wheelenium.action.wheelenium.*;
import com.octavianonline.games.wheelenium.action.wheelfeature.*;

@Reflect_Full
public class WheeleniumCoreActionModule extends AbstractActionModule {
    @Override
    public String getXmlNamespace() {
        return "http://www.atsisa.com/gox/wheelenium/action";
    }

    @Override
    protected void register() {
        String resolution = GameEngine.current().getGameConfiguration().getResolution().name();
//        registerAction("ShowInactiveViewSymbols", ShowInactiveViewSymbolsAction.class);
        registerAction("WinLineFrameAnimation", WinLineFrameAnimationAction.class);
        registerAction("HideSymbolsUnderWinLine", HideSymbolsUnderWinLineAction.class);
        registerAction("PushSymbolAnimationsInWinningLines", PushSymbolAnimationsInWinningLinesAction.class);
        registerAction("StartPayTableAnimation", StartPayTableAnimationAction.class);
        registerAction("PlayWinLineSound", PlayWinLineSoundAction.class);
        registerAction("InitWinCollect", InitWinCollectAction.class);
        registerAction("PlayWinLinesSound", PlayWinLinesSoundAction.class);
        registerAction("PlayWinScatterSound", PlayWinScatterSoundAction.class);
        registerAction("ShowAllHiddenSymbols", ShowAllHiddenSymbolsAction.class);
        registerAction("ExecuteNextDependingOnBigWin", ExecuteNextDependingOnBigWinAction.class);
        registerAction("StartWinAnimationAction", StartWinAnimationAction.class);
        registerAction("FindLongSpinAction", FindLongSpinAction.class);
        registerAction("Test", TestAction.class);
        registerAction("InitBigWinScreens", InitBigWinScreens.class);
        registerAction("WaitForBonus", WaitForBonus.class);

        registerAction("StartBonusAnimationTemp", StartBonusAnimationTemp.class);
        registerAction("StopBonusAnimationTemp", StopBonusAnimationTemp.class);
        registerAction("ShowWheelFeature", ShowWheelFeature.class);
        registerAction("SendWheelBonusRequest", SendWheelBonusRequest.class);
        registerAction("WaitWheelfeatureAction", WaitWheelfeatureAction.class);
        registerAction("InitCollectAction", InitCollectAction.class);
        registerAction("WinCountStopCustom", WinCountStopCustom.class);
        registerAction("TransferWinCustom", TransferWinCustom.class);
        registerAction("StartBigWin", StartBigWinAction.class);
        registerAction("ShowStopLines", ShowStopLines.class);
        registerAction("SetIdleState", SetIdleState.class);
        registerAction("GetWinSum", GetWinSum.class);
        registerAction("RestoredStartSpeenWheel", RestoredStartSpeenWheel.class);
        registerAction("HideButtons", HideButtons.class);
        registerAction("ResetLongSpinAction", ResetLongSpinAction.class);
        registerAction("HideWinLinesAction", HideWinLinesAction.class);
        registerAction("RestoreEnterWheelAction", RestoreEnterWheelAction.class);
        registerAction("RestoreBetForWheelAction", RestoreBetForWheelAction.class);

        if (resolution.equals("HD_MOBILE") || resolution.equals("HD") ) {
            registerAction("InitHtmlCollectAction", InitHtmlCollectAction.class);
            registerAction("FinishMobileCollect", FinishMobileCollect.class);
            registerAction("WheelCollectInit", WheelCollectInit.class);
            registerAction("StartBitwiseCollectWaitAction", StartBitwiseCollectWaitAction.class);
            registerAction("ExecuteNextDependingOnAutoPlayFixAction", ExecuteNextDependingOnAutoPlayFixAction.class);
            registerAction("ActionUpdate", ActionUpdate.class);
            registerAction("ShowWheelCollectRestore", ShowWheelCollectRestore.class);

        }

    }
}
