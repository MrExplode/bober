package me.sunstorm.bober.http.impl;

import io.javalin.http.Context;
import me.sunstorm.bober.routing.annotate.Get;
import me.sunstorm.bober.routing.annotate.PathPrefix;

@PathPrefix("/prefix")
public class PrefixDummy {

    @Get("/test/path")
    public void testPrefix(Context ctx) {

    }
}
