package com.octavianonline.games.wheelenium.action.wheelenium;

import com.atsisa.gox.framework.GameEngine;
import com.atsisa.gox.framework.action.Action;
import com.gwtent.reflection.client.annotations.Reflect_Full;

@Reflect_Full
public class InitBigWinScreens extends Action {
  @Override
   protected void execute() {

   GameEngine.current().getViewManager().getLayout("universalBigWinScreen").getRootView().setDepth(4);
   GameEngine.current().getViewManager().getLayout("particlesScreen").getRootView().setDepth(5);
   GameEngine.current().getViewManager().redrawAll();
   finish();
}}