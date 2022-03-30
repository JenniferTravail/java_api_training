package fr.lernejo.navy_battle.game;
import fr.lernejo.navy_battle.utilEnum.Orientation;
import fr.lernejo.navy_battle.utilEnum.Consequence;
import fr.lernejo.navy_battle.utilEnum.CellStatus;

import java.util.*;
public class MapGame {
    private final Integer[] BOATS = {5, 4, 3, 3, 2};
    private final CellStatus[][] map = new CellStatus[10][10];
    private final List<List<CoordinatesGame>> boats = new ArrayList<>();
    public MapGame(boolean fill) {
        for (CellStatus[] gameCells : map) {
            Arrays.fill(gameCells, CellStatus.EMPTY);
        }
        if (fill) {
            buildMap();
        }
    }
    public int getHeight() {
        return map[0].length;
    }
    public int getWidth() {
        return map.length;
    }
    private void buildMap() {
        var random = new Random();
        var boats = new ArrayList<>(Arrays.asList(BOATS));
        Collections.shuffle(boats);
        while (!boats.isEmpty()) {
            int boat = boats.get(0);
            int x = Math.abs(random.nextInt()) % getWidth();
            int y = Math.abs(random.nextInt()) % getHeight();
            var orientation = random.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            if (!canFit(boat, x, y, orientation))
                continue;
            addBoat(boat, x, y, orientation);
            boats.remove(0);
        }
    }
    private boolean canFit(int length, int x, int y, Orientation orientation) {
        if (x >= getWidth() || y >= getHeight() || getCell(x, y) != CellStatus.EMPTY)
            return false;
        if (length == 0)
            return true;
        return switch (orientation) {
            case HORIZONTAL -> canFit(length - 1, x + 1, y, orientation);
            case VERTICAL -> canFit(length - 1, x, y + 1, orientation);
        };
    }

    public CellStatus getCell(CoordinatesGame coordinates) {
        return getCell(coordinates.getX(), coordinates.getY());
    }
    public CellStatus getCell(int x, int y) {
        if (x >= 10 || y >= 10)
            throw new RuntimeException("Invalidate coordinates!");
        return map[x][y];
    }
    public void setCell(CoordinatesGame coordinates, CellStatus newStatus) {
        map[coordinates.getX()][coordinates.getY()] = newStatus;
    }
    public void addBoat(int length, int x, int y, Orientation orientation) {
        var coordinates = new ArrayList<CoordinatesGame>();
        while (length > 0) {
            map[x][y] = CellStatus.BOAT;
            length--;
            coordinates.add(new CoordinatesGame(x, y));
            switch (orientation) {
                case HORIZONTAL -> x++;
                case VERTICAL -> y++;
            }
        }
        boats.add(coordinates);
    }
    public boolean hasShipLeft() {
        for (var row : map) {
            if (Arrays.stream(row).anyMatch(s -> s == CellStatus.BOAT)) return true;
        }
        return false;
    }
    public CoordinatesGame getNextPlaceToHit() {
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                if (getCell(i, j) == CellStatus.EMPTY)
                    return new CoordinatesGame(i, j);
            }
        }
        throw new RuntimeException("Error");
    }
    public Consequence hit(CoordinatesGame coordinates) {
        if (getCell(coordinates) != CellStatus.BOAT) return Consequence.MISS;
        var first = boats.stream().filter(s -> s.contains(coordinates)).findFirst();
        assert (first.isPresent());
        first.get().remove(coordinates);
        setCell(coordinates, CellStatus.SUCCESSFUL_FIRE);
        return first.get().isEmpty() ? Consequence.SUNK : Consequence.HIT;
    }
}
