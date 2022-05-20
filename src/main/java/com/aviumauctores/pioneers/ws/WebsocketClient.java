package com.aviumauctores.pioneers.ws;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@ClientEndpoint
public class WebsocketClient {
    private Session session;

    private final List<Consumer<String>> messageHandlers = Collections.synchronizedList(new ArrayList<>());

    public WebsocketClient(URI endpointPath) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointPath);
        } catch (DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        this.session = null;
    }

    @OnMessage
    public void onMessage(String message) {
        synchronized (messageHandlers) {
            for (final Consumer<String> handler : messageHandlers) {
                handler.accept(message);
            }
        }
    }

    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    public void addMessageHandler(Consumer<String> messageHandler) {
        messageHandlers.add(messageHandler);
    }

    public void removeMessageHandler(Consumer<String> messageHandler) {
        messageHandlers.remove(messageHandler);
    }

    public void sendMessage(String message) {
        if (session == null) {
            return;
        }
        session.getAsyncRemote().sendText(message);
    }

    public void close() {
        if (session == null) {
            return;
        }

        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasMessageHandlers() {
        return !messageHandlers.isEmpty();
    }
}
