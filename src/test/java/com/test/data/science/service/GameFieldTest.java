package com.test.data.science.service;

import com.test.data.science.model.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameFieldTest {
    
    GameFieldService sut = new GameFieldService();
    
    @Test
    public void checkPropertiesTilesAndBlackHolesAmount() {
        int gameFieldWidth = 8;
        int gameFieldHeight = 9;
        int blackHoleQuantity = 16;

        Tile[][] gameField = sut.getGameField(gameFieldWidth, gameFieldHeight, blackHoleQuantity);

        Assertions.assertEquals(gameFieldWidth, gameField.length);
        Assertions.assertEquals(gameFieldHeight, gameField[0].length);

        long actualBlackHoleQuantity = sut.getBlackHoles().size();
        Assertions.assertEquals(blackHoleQuantity, actualBlackHoleQuantity);
        Assertions.assertEquals(blackHoleQuantity, actualBlackHoleQuantity);
    }

    @Test
    public void widthTooSmall_IllegalArgumentException() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> sut.getGameField(0, 8, 16));
        Assertions.assertEquals("Value must be greater than or equals 5", ex.getMessage());
    }

    @Test
    public void heightTooSmall_IllegalArgumentException() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> sut.getGameField(8, 0, 16));
        Assertions.assertEquals("Value must be greater than or equals 5", ex.getMessage());
    }

    @Test
    public void widthTooBig_IllegalArgumentException() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> sut.getGameField(100, 8, 16));
        Assertions.assertEquals("Value must be less than or equals 40", ex.getMessage());
    }

    @Test
    public void heightTooBig_IllegalArgumentException() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> sut.getGameField(8, 100, 16));
        Assertions.assertEquals("Value must be less than or equals 40", ex.getMessage());
    }

    @Test
    public void blackHoleQuantityTooSmall_IllegalArgumentException() {
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> sut.getGameField(8, 9, 0));
        Assertions.assertEquals("Value must be greater than or equal to 1", ex.getMessage());
    }
}
