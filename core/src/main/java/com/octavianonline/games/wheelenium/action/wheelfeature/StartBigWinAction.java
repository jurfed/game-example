package com.octavianonline.games.wheelenium.action.wheelfeature;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.atsisa.gox.framework.action.ActionData;
import com.atsisa.gox.framework.action.IActionManager;
import com.atsisa.gox.framework.animation.TweenViewAnimationData;
import com.atsisa.gox.framework.command.ShowScreenCommand;
import com.atsisa.gox.framework.utility.Timeout;

import com.atsisa.gox.reels.AbstractReelGame;
import com.atsisa.gox.reels.feature.bigwin.BigWinModelProvider;
import com.atsisa.gox.reels.feature.bigwin.IBigWinModel;
import com.gwtent.reflection.client.annotations.Reflect_Full;
import com.octavianonline.framework.reels.action.StartBigWinActionData;
import com.octavianonline.framework.reels.event.StartControlPanelWinCollectCommand;

import java.util.Map;

@Reflect_Full
public class StartBigWinAction extends Action<StartBigWinActionData> {


    /**
     * An action manager reference.
     */
    private final IActionManager actionManager;
    private String queueName;
    private IBigWinModel bigWinModel;

    public StartBigWinAction() {
        actionManager = GameEngine.current().getActionManager();
    }

    @Override
    protected void grabData() {
        BigWinModelProvider bigWinModelProvider = ((AbstractReelGame) GameEngine.current().getGame()).findReelGameComponent(BigWinModelProvider.class);
        Map<String, IBigWinModel> bigWinModels = bigWinModelProvider.getBigWinModels();

        if (bigWinModels != null && bigWinModels.containsKey(actionData.getBigWinType())) {
            IBigWinModel bigWinModel = bigWinModels.get(actionData.getBigWinType());
            this.queueName = bigWinModel.getBigWinQueueName();
            this.bigWinModel = bigWinModel;
        }else {
            this.queueName = actionData.getOrQueueName();
            this.bigWinModel = null;
            //throw new RuntimeException("Unknown key for BigWinModel: " + actionData.getBigWinType());
        }
    }

    @Override
    protected void execute() {
        if (bigWinModel != null) {
            new Timeout(1000,()->{
                eventBus.post(new ShowScreenCommand("universalBigWinScreen", new TweenViewAnimationData()));
                eventBus.post(new ShowScreenCommand("particlesScreen", new TweenViewAnimationData()));
                eventBus.post(new StartControlPanelWinCollectCommand(bigWinModel.getTime(), bigWinModel.getMainIndicatorStartValue(), bigWinModel.getMainIndicatorFinishValue()));
                actionManager.processQueue(queueName);
            },true);
        }else{
            actionManager.processQueue(queueName);
        }

    }

    @Override
    public Class<? extends ActionData> getActionDataType() {
        return StartBigWinActionData.class;
    }
}
