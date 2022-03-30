package fr.lernejo.navy_battle.utilEnum;

import java.util.Arrays;

public enum Consequence {
    MISS("miss"),
    HIT("hit"),
    SUNK("sunk");

    private final String apiString;

    Consequence(String res) {
        this.apiString = res;
    }


    public static Consequence fromAPI(String value) {
        var res = Arrays.stream(Consequence.values()).filter(f -> f.apiString.equals(value)).findFirst();

        if (res.isEmpty())
            throw new RuntimeException("Invalid value!");

        return res.get();
    }

    public String toAPI() {
        return apiString;
    }
}
