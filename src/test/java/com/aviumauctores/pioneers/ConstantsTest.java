package com.aviumauctores.pioneers;

import org.junit.jupiter.api.Test;

import static com.aviumauctores.pioneers.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {
    @Test
    void testConstants() {
        assertEquals(SCREEN_WIDTH, 640);
        assertEquals(SCREEN_HEIGHT, 480);
        assertEquals(GAME_TITLE, "Pioneers");
        assertEquals(BASE_URL, "http://192.168.178.161:3000/api/v3/");
    }
}