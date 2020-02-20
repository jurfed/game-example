package com.octavianonline.games.wheelenium.screen.model;

import com.atsisa.gox.framework.screen.model.ScreenModel;
import com.atsisa.gox.framework.utility.localization.ITranslator;
import com.gwtent.reflection.client.annotations.Reflect_Full;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Reflect_Full
public class WheelModel extends ScreenModel {
    private static final Map<String, Integer> mapValues = new HashMap() {{
        put("green_400", 400);
        put("orange_50", 50);
        put("pink_80", 80);
        put("orange_40", 40);
        put("blue_100", 100);
        put("pink_60", 60);
        put("yellow_20", 20);
        put("red_1000", 1000);
        put("yellow_30", 30);
        put("pink_80", 80);
        put("blue_200", 200);
        put("orage_40", 40);
        put("pink_60", 60);
        put("blue_100", 100);
        put("orange_50", 50);
        put("yellow_30", 30);
    }};
    private static final Map<Integer, String> stepValues = new HashMap() {{
        put(12, "green_400");
        put(11, "orange_50");
        put(10, "pink_80");
        put(9, "orange_40");
        put(8, "blue_100");
        put(7, "pink_60");
        put(6, "yellow_20");
        put(5, "red_1000");
        put(4, "yellow_30");
        put(3, "pink_80");
        put(2, "blue_200");
        put(1, "orage_40");
        put(16, "pink_60");
        put(15, "blue_100");
        put(14, "orange_50");
        put(13, "yellow_30");
    }};

    private static final Map<Integer, Integer> valuesForStop = new HashMap() {{
        put(12, 1);
        put(11, 16);
        put(10, 15);
        put(9, 14);
        put(8, 13);
        put(7, 12);
        put(6, 11);
        put(5, 10);
        put(4, 9);
        put(3, 8);
        put(2, 7);
        put(1, 6);
        put(16, 5);
        put(15, 4);
        put(14, 3);
        put(13, 2);
    }};

    private static final Map<Integer, List<Integer>> convertWinSumToSector = new HashMap() {{
        put(400, new ArrayList(){{add(12);}});
        put(50, new ArrayList(){{add(11);add(14);}});
        put(80, new ArrayList(){{add(10);add(3);}});
        put(40, new ArrayList(){{add(9);add(1);}});
        put(100, new ArrayList(){{add(8);add(15);}});
        put(60, new ArrayList(){{add(7);add(16);}});
        put(20, new ArrayList(){{add(6);}});
        put(1000, new ArrayList(){{add(5);}});
        put(30, new ArrayList(){{add(4);add(13);}});
        put(200, new ArrayList(){{add(2);}});

    }};

    public static Map<Integer, List<Integer>> getConvertWinSumToSector() {
        return convertWinSumToSector;
    }

    public static Map<Integer, Integer> getValuesForStop() {
        return valuesForStop;
    }

    ITranslator translator;


    @Inject
    public WheelModel(ITranslator translator) {
        super(translator);
        this.translator = translator;
    }

    public static Map<String, Integer> getMapValues() {
        return mapValues;
    }

    public ITranslator getTranslator() {
        return translator;
    }
}