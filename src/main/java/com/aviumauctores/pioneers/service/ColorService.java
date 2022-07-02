package com.aviumauctores.pioneers.service;

import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.util.HashMap;

import static com.aviumauctores.pioneers.Constants.*;

public class ColorService {

    public final HashMap<String, String> colors = new HashMap<>();

    @Inject
    public ColorService() {
        colors.put(COLOR_CODE_BLUE, COLOR_BLUE);
        colors.put(COLOR_CODE_RED, COLOR_RED);
        colors.put(COLOR_CODE_GREEN, COLOR_GREEN);
        colors.put(COLOR_CODE_YELLOW, COLOR_YELLOW);
        colors.put(COLOR_CODE_ORANGE, COLOR_ORANGE);
        colors.put(COLOR_CODE_VIOLET, COLOR_VIOLET);
        colors.put(COLOR_CODE_CYAN, COLOR_CYAN);
        colors.put(COLOR_CODE_LIMEGREEN, COLOR_LIMEGREEN);
        colors.put(COLOR_CODE_MAGENTA, COLOR_MAGENTA);
        colors.put(COLOR_CODE_CHOCOLATE, COLOR_CHOCOLATE);
        colors.put(COLOR_CODE_WHITE, COLOR_WHITE);

    }

    public String getColor(String color) {
        return colors.get((color));
    }

    public String getColorCode(String color) {
        String[] keys = colors.keySet().toArray(new String[COLOR_AMOUNT]);
        for (String s : keys) {
            if (colors.get(s).equals(color)) {
                return color;
            }
        }
        return "COLOR NOT FOUND";
    }

    public String getColor(Color color) {
        String colorFormat = String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));

        return colors.get(colorFormat);
    }
}
