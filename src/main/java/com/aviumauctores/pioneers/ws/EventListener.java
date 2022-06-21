package com.aviumauctores.pioneers.ws;

import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.service.TokenStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static com.aviumauctores.pioneers.Constants.*;

@Singleton
public class EventListener {
    private final TokenStorage tokenStorage;
    private final ObjectMapper mapper;
    private WebsocketClient endpoint;

    @Inject
    public EventListener(TokenStorage tokenStorage, ObjectMapper mapper) {
        this.tokenStorage = tokenStorage;
        this.mapper = mapper;
    }

    private void ensureOpen() {
        if (endpoint != null) {
            return;
        }

        try {
            endpoint = new WebsocketClient(
                    new URI(WS_EVENTS_URL + WS_QUERY_AUTH_TOKEN + tokenStorage.getToken())
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Observable<EventDto<T>> listen(String pattern, Class<T> payloadType) {
        return Observable.create(emitter -> {
            ensureOpen();

            send(Map.of(JSON_EVENT, JSON_EVENT_SUBSCRIBE, JSON_DATA, pattern));

            final Pattern regex = Pattern.compile(pattern
                    .replace(".", "\\.")
                    .replace("*", "[^.]*"));
            final Consumer<String> handler = eventStr -> {
                try {
                    final JsonNode node = mapper.readTree(eventStr);
                    final String event = node.get(JSON_EVENT).asText();
                    if (!regex.matcher(event).matches()) {
                        return;
                    }

                    final T data = mapper.treeToValue(node.get(JSON_DATA), payloadType);
                    emitter.onNext(new EventDto<>(event, data));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            };
            endpoint.addMessageHandler(handler);
            emitter.setCancellable(() -> removeEventHandler(pattern, handler));
        });
    }

    private void removeEventHandler(String pattern, Consumer<String> handler) {
        if (endpoint == null) {
            return;
        }

        send(Map.of(JSON_EVENT, JSON_EVENT_UNSUBSCRIBE, JSON_DATA, pattern));
        endpoint.removeMessageHandler(handler);
        if (!endpoint.hasMessageHandlers()) {
            close();
        }
    }

    public void send(Object message) {
        ensureOpen();

        try {
            final String messageStr = mapper.writeValueAsString(message);
            endpoint.sendMessage(messageStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        if (endpoint != null) {
            endpoint.close();
            endpoint = null;
        }
    }
}
