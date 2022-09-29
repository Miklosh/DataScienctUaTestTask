package com.test.data.science.service;

import com.test.data.science.model.Tile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * I've separated field creation logic to separate application logic responsibilities
 * and to gain ability for more granular testing .
 * */
@Slf4j
@Service
public class GameFieldService {

    private static final int MIN_DIMENSION_SIZE = 5;
    private static final int MAX_DIMENSION_SIZE = 40;
    private static final int MIN_BLACK_HOLE_QUANTITY = 1;
    public static final int BLACK_HOLE_THRESHOLD = 9;

    private List<Tile> blackHoleList = null;

    /**
     * I've used for this task simple 2d array because this instrument is best suited for the task.
     * In order to create 2d structure with uniformly distributed elements first I've created an array of all elements
     * updated it with black hole objects and then used standard Java Collection API method to shuffle the collection.
     * This approach provides us with desired result without inventing the wheel.
     * After collection elements shuffle I transform one dimension collection into two dimension array of objects.
     * */
    public Tile[][] getGameField(final int rowQuantity, final int columnQuantity, int blackHolesQuantity) {
        inputParameterValidation(rowQuantity, columnQuantity, blackHolesQuantity);

        int tilesQuantity = rowQuantity * columnQuantity;
        int correctBlackHolesQuantity = adjustBlackHolesQuantity(blackHolesQuantity, tilesQuantity);
        blackHoleList = new ArrayList<>(correctBlackHolesQuantity);
        LinkedList<Tile> tiles = Stream.generate(Tile::new)
                .limit(tilesQuantity)
                .collect(Collectors.toCollection(LinkedList::new));
        for (int i = 0; i < correctBlackHolesQuantity; i++) {
            Tile tile = tiles.get(i);
            tile.setBlackHole(true);
        }

        Collections.shuffle(tiles);

        return createGameField(rowQuantity, columnQuantity, tiles);
    }

    private int adjustBlackHolesQuantity(int blackHolesQuantity, int tilesQuantity) {
        return Math.min(blackHolesQuantity, tilesQuantity - BLACK_HOLE_THRESHOLD);
    }

    private void inputParameterValidation(int rowQuantity, int columnQuantity, int blackHolesQuantity) {
        if (rowQuantity < MIN_DIMENSION_SIZE || columnQuantity < MIN_DIMENSION_SIZE) {
            throw new IllegalArgumentException("Value must be greater than or equals 5");
        } else if (rowQuantity > MAX_DIMENSION_SIZE || columnQuantity > MAX_DIMENSION_SIZE) {
            throw new IllegalArgumentException("Value must be less than or equals 40");
        } else if (blackHolesQuantity < MIN_BLACK_HOLE_QUANTITY) {
            throw new IllegalArgumentException("Value must be greater than or equal to 1");
        }
    }

    /**
     * Map collection value to 2d array cell
     * */
    private Tile[][] createGameField(int rowQuantity, int columnQuantity, LinkedList<Tile> tiles) {
        Tile[][] result = new Tile[rowQuantity][columnQuantity];
        for (int row = 0; row < rowQuantity; row++) {
            for (int col = 0; col < columnQuantity; col++) {
                Optional<Tile> oTile = Optional.ofNullable(tiles.pollFirst());
                if (oTile.isPresent()) {
                    Tile tile = oTile.get();
                    tile.setRow(row);
                    tile.setColumn(col);
                    if (tile.isBlackHole()) {
                        blackHoleList.add(tile);
                    }
                    result[row][col] = tile;
                } else {
                    log.info("No tile was found for cell row {} column {}", row, col);
                    Tile tile = new Tile();
                    tile.setRow(row);
                    tile.setColumn(col);
                    result[row][col] = tile;
                }
            }
        }
        return result;
    }

    /**
     * Contains all black holes with their address in array
     * */
    public List<Tile> getBlackHoles() {
        return blackHoleList;
    }

}
