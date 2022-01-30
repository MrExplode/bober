package me.sunstorm.bober;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.bober.http.JavalinHandler;

@Slf4j
@Getter
public class Bober {
    private final JavalinHandler javalinHandler;

    public Bober() {
        javalinHandler = new JavalinHandler();
    }

    public static void main(String[] args) {
        log.info("Starting Bober CDN...");
        new Bober();
    }
}
