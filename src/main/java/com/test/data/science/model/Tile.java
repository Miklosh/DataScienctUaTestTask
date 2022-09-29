package com.test.data.science.model;

import lombok.Builder;
import lombok.Data;

@Data
public class Tile {
    private int row;
    private int column;
    private boolean isBlackHole;
    private boolean isVisible;
    private int blackHoleCount;
}
