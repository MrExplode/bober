package me.sunstorm.bober.http;

import io.javalin.Javalin;
import io.javalin.plugin.json.JsonMapper;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.bober.Constants;
import me.sunstorm.bober.routing.RouteManager;
import org.jetbrains.annotations.NotNull;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Slf4j
@Getter
public class JavalinHandler {
    private final Javalin javalin;

    public JavalinHandler() {
        javalin = Javalin.create(config -> {
            config.jsonMapper(new JsonMapper() {
                @NotNull
                @Override
                public String toJsonString(@NotNull Object obj) {
                    return Constants.GSON.toJson(obj);
                }

                @NotNull
                @Override
                public <T> T fromJsonString(@NotNull String json, @NotNull Class<T> targetClass) {
                    return Constants.GSON.fromJson(json, targetClass);
                }
            });
            config.requestLogger((ctx, ms) -> log.debug("Request from {} to {} took {} ms", ctx.ip(), ctx.path(), ms));
        });
        TemplateEngine engine = new TemplateEngine();
        var resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setCacheable(true);
        engine.setTemplateResolver(resolver);
        JavalinThymeleaf.configure(engine);
        JavalinRenderer.register(JavalinThymeleaf.INSTANCE);
        var router = new RouteManager();

        javalin.start(8080);
    }
}
