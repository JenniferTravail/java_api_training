package fr.lernejo.navy_battle.utilEnum;

public enum CellStatus {
    EMPTY("."),
    MISSED_FIRE("-"),
    SUCCESSFUL_FIRE("X"),
    BOAT("B");

    private final String letter;

    CellStatus(String letter) {
        this.letter = letter;
    }
}
