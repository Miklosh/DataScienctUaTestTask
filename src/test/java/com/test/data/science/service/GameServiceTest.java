package com.test.data.science.service;


import com.test.data.science.model.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
public class GameServiceTest {

    @Mock
    private GameFieldService gameField;
    @InjectMocks
    private GameService sut;

    @Test
    public void blackHoleQuantityBiggerThenTiles_SetBlackHoleValueToThreshold() {
        int gameFieldWidth = 8;
        int gameFieldHeight = 8;
        int blackHoleQuantity = 100;
        int thresholdBlackHoleQuantity = (gameFieldWidth * gameFieldHeight) - 9;
        when(sut.initGameField(gameFieldWidth, gameFieldHeight, blackHoleQuantity)).thenCallRealMethod();
        Tile[][] gameField = sut.initGameField(gameFieldWidth, gameFieldHeight, blackHoleQuantity);

        long actualBlackHoleQuantity = Arrays.stream(gameField).flatMap(Arrays::stream).filter(Tile::isBlackHole).count();
        Assertions.assertEquals(thresholdBlackHoleQuantity, actualBlackHoleQuantity);
    }

    /**
     * Use random generated field
     * Black holes positions are random
     * Inspect bottom far right tile
     * Expect to open 1 tile that I choose to inspect
     Expected array state (0 - black hole, {1} - black hole counter, * - visible tile without counter, '-' - invisible tile):
     * */
    @Test
    public void clickBh_allBhVisible_onlyBhVisible() {
        int gameFieldWidth = 8;
        int gameFieldHeight = 9;
        int blackHoleQuantity = 16;
        when(sut.initGameField(gameFieldWidth, gameFieldHeight, blackHoleQuantity)).thenCallRealMethod();
        when(gameField.getBlackHoles()).thenCallRealMethod();
        Tile[][] gameField = sut.initGameField(gameFieldWidth, gameFieldHeight, blackHoleQuantity);
        long visibleBlackHoles = Arrays.stream(gameField).flatMap(Arrays::stream)
                .filter(Tile::isBlackHole)
                .filter(Tile::isVisible)
                .count();
        Assertions.assertEquals(0, visibleBlackHoles);
        Optional<Tile> oBlackHoleTile = Arrays.stream(gameField).flatMap(Arrays::stream)
                .filter(Tile::isBlackHole)
                .findFirst();

        oBlackHoleTile.ifPresent(bhTile -> sut.inspectTile(bhTile.getRow(), bhTile.getColumn()));

        visibleBlackHoles = Arrays.stream(gameField).flatMap(Arrays::stream)
                .filter(Tile::isBlackHole)
                .filter(Tile::isVisible)
                .count();
        Assertions.assertEquals(blackHoleQuantity, visibleBlackHoles);

        long otherTilesVisibleCount = Arrays.stream(gameField).flatMap(Arrays::stream)
                .filter(tile -> !tile.isBlackHole())
                .filter(Tile::isVisible)
                .count();
        Assertions.assertEquals(0, otherTilesVisibleCount);
    }

    /**
     * 4*4 test field.
     * Black holes: top far left, top far right, bottom far left.
     * Inspect bottom far right tile
     * Expect to open 1 tile that I choose to inspect
     Expected array state (0 - black hole, {1} - black hole counter, * - visible tile without counter, '-' - invisible tile):
     0 1 - 0
     - - - -
     - - - -
     0 - - -

     * */
    @Test
    public void clickTileWithNonZeroBhCount_onlyOneTileVisible() {
        int gameFieldWidth = 8;
        int gameFieldHeight = 9;
        int blackHoleQuantity = 5;

        Tile[][] testField = new Tile[][]{
                {getTile(0, 0, true), getTile(0, 1, false), getTile(0, 2, false), getTile(0, 3, true)},
                {getTile(1, 0, false), getTile(1, 1, false), getTile(1, 2, false), getTile(1, 3, false)},
                {getTile(2, 0, false), getTile(2, 1, false), getTile(2, 2, false), getTile(2, 3, false)},
                {getTile(3, 0, true), getTile(3, 1, false), getTile(3, 2, false), getTile(3, 3, false)}
        };

        when(sut.initGameField(gameFieldWidth, gameFieldHeight, blackHoleQuantity)).thenReturn(testField);
        Tile[][] gameField = sut.initGameField(gameFieldWidth, gameFieldHeight, blackHoleQuantity);
        long visibleTiles = Arrays.stream(gameField).flatMap(Arrays::stream)
                .filter(Tile::isVisible)
                .count();
        Assertions.assertEquals(0, visibleTiles);

        sut.inspectTile(0, 1);

        long otherTilesVisibleCount = Arrays.stream(gameField).flatMap(Arrays::stream)
                .filter(tile -> !tile.isBlackHole())
                .filter(Tile::isVisible)
                .count();
        Assertions.assertEquals(1, otherTilesVisibleCount);
        Assertions.assertTrue(gameField[0][1].isVisible());
    }

    /**
     * 4*4 test field.
     * Black holes: top far left, top far right, bottom far left.
     * Inspect bottom far right tile
     * Expect to open 9 tiles
     Expected array state (0 - black hole, {1} - black hole counter, * - visible tile without counter, '-' - invisible tile):
      0 - - 0
      - 1 1 1
      - 1 * *
      0 1 * *

     * */
    @Test
    public void clickTileWithZeroBhCount_setVisibleAllSurroundingTiles_CloseBorder() {
        int gameFieldWidth = 8;
        int gameFieldHeight = 9;
        int blackHoleQuantity = 5;

        Tile[][] testField = new Tile[][]{
                {getTile(0, 0, true), getTile(0, 1, false), getTile(0, 2, false), getTile(0, 3, true)},
                {getTile(1, 0, false), getTile(1, 1, false), getTile(1, 2, false), getTile(1, 3, false)},
                {getTile(2, 0, false), getTile(2, 1, false), getTile(2, 2, false), getTile(2, 3, false)},
                {getTile(3, 0, true), getTile(3, 1, false), getTile(3, 2, false), getTile(3, 3, false)}
        };

        when(sut.initGameField(gameFieldWidth, gameFieldHeight, blackHoleQuantity)).thenReturn(testField);
        Tile[][] gameField = sut.initGameField(gameFieldWidth, gameFieldHeight, blackHoleQuantity);
        long visibleTiles = Arrays.stream(gameField).flatMap(Arrays::stream)
                .filter(Tile::isVisible)
                .count();
        Assertions.assertEquals(0, visibleTiles);

        sut.inspectTile(3, 3);

        long otherTilesVisibleCount = Arrays.stream(gameField).flatMap(Arrays::stream)
                .filter(tile -> !tile.isBlackHole())
                .filter(Tile::isVisible)
                .count();
        Assertions.assertEquals(9, otherTilesVisibleCount);
        Assertions.assertTrue(gameField[3][3].isVisible());
        Assertions.assertTrue(gameField[3][2].isVisible());
        Assertions.assertTrue(gameField[3][1].isVisible());
        Assertions.assertEquals(1, gameField[3][1].getBlackHoleCount());
        Assertions.assertTrue(gameField[2][3].isVisible());
        Assertions.assertTrue(gameField[2][2].isVisible());
        Assertions.assertTrue(gameField[2][1].isVisible());
        Assertions.assertEquals(1, gameField[2][1].getBlackHoleCount());
        Assertions.assertTrue(gameField[1][3].isVisible());
        Assertions.assertEquals(1, gameField[1][3].getBlackHoleCount());
        Assertions.assertTrue(gameField[1][2].isVisible());
        Assertions.assertEquals(1, gameField[1][2].getBlackHoleCount());
        Assertions.assertTrue(gameField[1][1].isVisible());
        Assertions.assertEquals(1, gameField[1][1].getBlackHoleCount());
    }

    private Tile getTile(int row, int column, boolean isBlackHole) {
        Tile tile = new Tile();
        tile.setRow(row);
        tile.setColumn(column);
        tile.setBlackHole(isBlackHole);
        return tile;
    }

    private boolean isPossiblePosition(int tileRow, int tileColumn, int arrRow, int arrColumn) {
        return tileRow >= 0 && tileColumn > 0 && arrRow > 0 && arrColumn >= 0 && tileRow < arrRow && tileColumn < arrColumn;
    }

}
