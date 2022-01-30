package me.sunstorm.bober.http.impl;

import io.javalin.http.Context;
import me.sunstorm.bober.routing.annotate.Get;
import me.sunstorm.bober.routing.annotate.Inject;

public class InjectionDummy {
    private final String injected;

    @Inject
    public InjectionDummy(String injected) {
        this.injected = injected;
    }

    @Get("/inject/test")
    public void injectTest(Context ctx) {
        ctx.result(injected);
    }
}
