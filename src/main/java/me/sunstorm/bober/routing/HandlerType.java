package me.sunstorm.bober.routing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sunstorm.bober.routing.annotate.*;

import java.lang.annotation.Annotation;

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
}
