package me.sunstorm.bober.http.impl;

import io.javalin.http.Context;
import me.sunstorm.bober.routing.annotate.*;

public class RouteDummy {

    @Before
    public void globalBefore(Context ctx) {

    }

    @Before("/before/scoped")
    public void scopedBefore(Context ctx) {

    }

    @After
    public void globalAfter(Context ctx) {

    }

    @After("/after/scoped")
    public void scopedAfter(Context ctx) {

    }

    @Get("/get")
    public void getHandler(Context ctx) {

    }

    @Post("/post")
    public void postHandler(Context ctx) {

    }

    @Put("/put")
    public void putHandler(Context ctx) {

    }

    @Patch("/patch")
    public void patchHandler(Context ctx) {

    }

    @Delete("/delete")
    public void deleteHandler(Context ctx) {

    }
}
