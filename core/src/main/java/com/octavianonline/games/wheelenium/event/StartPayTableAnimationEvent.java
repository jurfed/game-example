package com.octavianonline.games.wheelenium.event;

import com.gwtent.reflection.client.annotations.Reflect_Full;

import java.util.List;

@Reflect_Full
public class StartPayTableAnimationEvent {

    private final List<Iterable<String>> stoppedSymbols;

    public StartPayTableAnimationEvent(List<Iterable<String>> stoppedSymbols) {
        this.stoppedSymbols = stoppedSymbols;
    }

    public List<Iterable<String>> getStoppedSymbols() {
        return stoppedSymbols;
    }
}
