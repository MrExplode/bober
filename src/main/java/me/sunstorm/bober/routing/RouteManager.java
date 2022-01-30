package me.sunstorm.bober.routing;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import lombok.extern.slf4j.Slf4j;
import me.sunstorm.bober.routing.annotate.*;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public class RouteManager {
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();
    private final Predicate<Method> methodPredicate = m ->
                    HandlerType.getType(m) != null
                    && m.getReturnType() == void.class
                    && m.getParameterCount() == 1
                    && Context.class.isAssignableFrom(m.getParameterTypes()[0]);
    private final Map<Class<?>, Supplier<?>> providerMap = new HashMap<>();

    public <T> void registerProvider(Class<T> type, Supplier<T> provider) {
        if (providerMap.containsKey(type)) throw new IllegalArgumentException("Provider already registered");
        providerMap.put(type, provider);
    }

    public void createRouting(Javalin javalin, Class<?>... handlers) {
        for (Class<?> handlerClass : handlers) {
            try {
                List<Method> methodCandidates = Arrays.stream(handlerClass.getDeclaredMethods()).filter(methodPredicate).collect(Collectors.toList());
                if (methodCandidates.size() == 0) {
                    log.warn("Class {} does not contain eligible methods for routing", handlerClass.getName());
                    continue;
                }

                Object classInstance = createInstance(handlerClass);
                String pathPrefix = "";
                if (handlerClass.isAnnotationPresent(PathPrefix.class))
                    pathPrefix = handlerClass.getAnnotation(PathPrefix.class).value();

                for (Method method : methodCandidates) {
                    try {
                        MethodHandle methodHandle = lookup.unreflect(method);
                        CallSite referenceSite = LambdaMetafactory.metafactory(
                                lookup,
                                "handle",
                                MethodType.methodType(Handler.class, handlerClass),
                                MethodType.methodType(void.class, Context.class),
                                methodHandle,
                                MethodType.methodType(void.class, Context.class)
                        );

                        Handler handlerInstance = (Handler) referenceSite.getTarget().bindTo(classInstance).invoke();
                        HandlerType.getType(method).apply(javalin, method, pathPrefix, handlerInstance);
                    } catch (Throwable e) {
                        log.error("Failed to create routing for " + method.getName(), e);
                    }
                }
            } catch (ReflectiveOperationException e) {
                log.error("Couldn't access default constructor of class " + handlerClass.getName(), e);
            }
        }
    }

    private Object createInstance(@NotNull Class<?> type) throws ReflectiveOperationException {
        var constructor = Arrays.stream(type.getConstructors()).filter(c -> c.isAnnotationPresent(Inject.class)).findFirst().orElseGet(() -> getDefault(type));
        if (constructor.getParameterCount() == 0) return constructor.newInstance();
        List<Object> parameterInstances = new ArrayList<>();
        for (Class<?> parameter : constructor.getParameterTypes()) {
            if (!providerMap.containsKey(parameter)) throw new IllegalArgumentException("No provider for type " + parameter.getName());
            parameterInstances.add(providerMap.get(parameter).get());
        }
        return constructor.newInstance(parameterInstances.toArray());
    }

    //all my homies hate checked exceptions
    private Constructor<?> getDefault(Class<?> type) {
        try {
            return type.getConstructor();
        } catch (NoSuchMethodException e)  {
            throw new IllegalArgumentException(e);
        }
    }
}
