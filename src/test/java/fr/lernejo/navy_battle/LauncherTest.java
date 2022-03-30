package fr.lernejo.navy_battle;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LauncherTest {
    static final Launcher serv = new Launcher();

    @Test
    public void launching(){
        Launcher.main(new String[]{"8080"});
        Launcher.main(new String[]{"9096", "http://localhost:8080"});
    }
}
