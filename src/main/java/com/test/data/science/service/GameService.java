package com.test.data.science.service;

import com.test.data.science.model.Tile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameFieldService gameFieldService;

    private Tile[][] gameField;

    /**
     * This is the first action that should be performed during game start-up
     *
     * */
    public Tile[][] initGameField(final int rowQuantity, final int columnQuantity, int blackHolesQuantity) {
        log.trace("Create game field with rows {}; columns {}; black holes {}", rowQuantity, columnQuantity, blackHolesQuantity);
        gameField = gameFieldService.getGameField(rowQuantity, columnQuantity, blackHolesQuantity);
        return gameField;
    }

    /**
     * this method responds to click on a game tile
     * */
    public Tile[][] inspectTile(int rowIndex, int columnIndex) {
        Tile tileToInspect = gameField[rowIndex][columnIndex];
        // if tile is BH - reveal all BH
        if (tileToInspect.isBlackHole()) {
            gameFieldService.getBlackHoles().forEach(tile -> tile.setVisible(true));
        } else {
            inspectAdjacentTiles(tileToInspect);
        }
        return gameField;
    }

    /**
     * Checks adjacent tiles for black holes, if found sets black hole counter and visible to 'true' on inspected tile and exits.
     * In case tile doesn't border with black holes method uses recursion to traverse all adjacent tiles until it reaches the borders
     * that consist of tiles with black hole counters and game field border
     */
    private void inspectAdjacentTiles(Tile tile) {
        int initialTileRow = tile.getRow();
        int initialTileColumn = tile.getColumn();
        int arrRowQnt = gameField.length;
        int arrColumnQnt = gameField[0].length;
        List<Tile> adjacentTileList = new ArrayList<>();

        if (isPossiblePosition(initialTileRow + 1, initialTileColumn, arrRowQnt, arrColumnQnt)) {
            Tile top = gameField[initialTileRow + 1][initialTileColumn];
            adjacentTileList.add(top);
        }
        if (isPossiblePosition(initialTileRow + 1, initialTileColumn + 1, arrRowQnt, arrColumnQnt)) {
            Tile topRight = gameField[initialTileRow + 1][initialTileColumn + 1];
            adjacentTileList.add(topRight);
        }
        if (isPossiblePosition(initialTileRow + 1, initialTileColumn - 1, arrRowQnt, arrColumnQnt)) {
            Tile topLeft = gameField[initialTileRow + 1][initialTileColumn - 1];
            adjacentTileList.add(topLeft);
        }
        if (isPossiblePosition(initialTileRow - 1, initialTileColumn, arrRowQnt, arrColumnQnt)) {
            Tile bottom = gameField[initialTileRow - 1][initialTileColumn];
            adjacentTileList.add(bottom);
        }
        if (isPossiblePosition(initialTileRow - 1, initialTileColumn + 1, arrRowQnt, arrColumnQnt)) {
            Tile bottomRight = gameField[initialTileRow - 1][initialTileColumn + 1];
            adjacentTileList.add(bottomRight);
        }
        if (isPossiblePosition(initialTileRow - 1, initialTileColumn - 1, arrRowQnt, arrColumnQnt)) {
            Tile bottomLeft = gameField[initialTileRow - 1][initialTileColumn - 1];
            adjacentTileList.add(bottomLeft);
        }
        if (isPossiblePosition(initialTileRow, initialTileColumn + 1, arrRowQnt, arrColumnQnt)) {
            Tile right = gameField[initialTileRow][initialTileColumn + 1];
            adjacentTileList.add(right);
        }
        if (isPossiblePosition(initialTileRow, initialTileColumn - 1, arrRowQnt, arrColumnQnt)) {
            Tile left = gameField[initialTileRow][initialTileColumn - 1];
            adjacentTileList.add(left);
        }

        long blackHoleCount = adjacentTileList.stream().filter(Tile::isBlackHole).count();
        tile.setBlackHoleCount((int) blackHoleCount);
        tile.setVisible(true);

        // RECURSION
        if (blackHoleCount == 0) {
            adjacentTileList.stream()
                    .filter(tile1 -> !tile1.isBlackHole() && !tile1.isVisible())
                    .forEach(this::inspectAdjacentTiles);
        }
    }

    private boolean isPossiblePosition(int tileRow, int tileColumn, int arrRow, int arrColumn) {
        return tileRow >= 0 && tileColumn >= 0 && arrRow > 0 && arrColumn >= 0 && tileRow < arrRow && tileColumn < arrColumn;
    }

}
