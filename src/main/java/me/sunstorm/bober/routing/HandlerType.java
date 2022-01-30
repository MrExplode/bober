package me.sunstorm.bober.routing;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.bober.routing.annotate.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Getter
@AllArgsConstructor
public enum HandlerType {
    BEFORE(Before.class),
    AFTER(After.class),
    GET(Get.class),
    POST(Post.class),
    PUT(Put.class),
    PATCH(Patch.class),
    DELETE(Delete.class);

    private final Class<? extends Annotation> annotationClass;

    @Nullable
    public static HandlerType getType(@NotNull Method m) {
        for (HandlerType type : values()) {
            if (m.isAnnotationPresent(type.getAnnotationClass()))
                return type;
        }
        return null;
    }

    public void apply(Javalin javalin, Method target, String prefix, Handler handler) throws ReflectiveOperationException {
        String name = this.toString().toLowerCase();
        String path = prefix + getPathValue(target);
        if (this == BEFORE || this == AFTER) {
            if (path.isEmpty()) {
                var method = javalin.getClass().getMethod(name, Handler.class);
                method.invoke(javalin, handler);
            } else {
                var method = javalin.getClass().getMethod(name, String.class, Handler.class);
                method.invoke(javalin, path, handler);
            }
        } else {
            var method = javalin.getClass().getMethod(name, String.class, Handler.class);
            method.invoke(javalin, path, handler);
        }
    }

    private String getPathValue(Method m) throws ReflectiveOperationException {
        var annotation = m.getAnnotation(annotationClass);
        return (String) annotationClass.getMethod("value").invoke(annotation);
    }
}
