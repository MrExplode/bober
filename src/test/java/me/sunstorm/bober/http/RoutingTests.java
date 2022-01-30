package me.sunstorm.bober.http;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import kong.unirest.Unirest;
import me.sunstorm.bober.http.impl.InjectionDummy;
import me.sunstorm.bober.http.impl.PrefixDummy;
import me.sunstorm.bober.http.impl.RouteDummy;
import me.sunstorm.bober.routing.RouteManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoutingTests {
    private Javalin javalinMock;
    private RouteManager routeManager;

    @BeforeAll
    void setup() {
        javalinMock = mock(Javalin.class);
        routeManager = new RouteManager();
        routeManager.createRouting(javalinMock, RouteDummy.class);
    }

    @Test
    void testBefore() {
        var pathCaptor = ArgumentCaptor.forClass(String.class);
        var handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(javalinMock).before(pathCaptor.capture(), handlerCaptor.capture());
        assertThat(pathCaptor.getValue()).isEqualTo("/before/scoped");
        verify(javalinMock).before(handlerCaptor.capture());
    }

    @Test
    void testAfter() {
        var pathCaptor = ArgumentCaptor.forClass(String.class);
        var handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(javalinMock).after(pathCaptor.capture(), handlerCaptor.capture());
        assertThat(pathCaptor.getValue()).isEqualTo("/after/scoped");
        verify(javalinMock).after(handlerCaptor.capture());
    }

    @Test
    void testGet() {
        var pathCaptor = ArgumentCaptor.forClass(String.class);
        var handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(javalinMock).get(pathCaptor.capture(), handlerCaptor.capture());
        assertThat(pathCaptor.getValue()).isEqualTo("/get");
    }

    @Test
    void testPost() {
        var pathCaptor = ArgumentCaptor.forClass(String.class);
        var handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(javalinMock).post(pathCaptor.capture(), handlerCaptor.capture());
        assertThat(pathCaptor.getValue()).isEqualTo("/post");
    }

    @Test
    void testPut() {
        var pathCaptor = ArgumentCaptor.forClass(String.class);
        var handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(javalinMock).put(pathCaptor.capture(), handlerCaptor.capture());
        assertThat(pathCaptor.getValue()).isEqualTo("/put");
    }

    @Test
    void testPatch() {
        var pathCaptor = ArgumentCaptor.forClass(String.class);
        var handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(javalinMock).patch(pathCaptor.capture(), handlerCaptor.capture());
        assertThat(pathCaptor.getValue()).isEqualTo("/patch");
    }

    @Test
    void testDelete() {
        var pathCaptor = ArgumentCaptor.forClass(String.class);
        var handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(javalinMock).delete(pathCaptor.capture(), handlerCaptor.capture());
        assertThat(pathCaptor.getValue()).isEqualTo("/delete");
    }

    @Test
    void testPathPrefix() {
        Javalin javalin = mock(Javalin.class);
        routeManager.createRouting(javalin, PrefixDummy.class);
        var pathCaptor = ArgumentCaptor.forClass(String.class);
        var handlerCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(javalin).get(pathCaptor.capture(), handlerCaptor.capture());
        assertThat(pathCaptor.getValue()).isEqualTo("/prefix/test/path");
    }

    @Test
    void testInjection() {
        Javalin javalin = Javalin.create(c -> c.showJavalinBanner = false);
        javalin.start(7000);
        routeManager.registerProvider(String.class, () -> "This object is dependency injected");
        routeManager.createRouting(javalin, InjectionDummy.class);
        var response = Unirest.get("http://localhost:7000/inject/test").asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("This object is dependency injected");
        javalin.close();
    }
}
