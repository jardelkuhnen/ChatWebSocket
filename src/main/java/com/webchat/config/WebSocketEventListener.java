package com.webchat.config;

import com.webchat.enums.MessageType;
import com.webchat.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
public class WebSocketEventListener {

    @Autowired
    private SimpMessageSendingOperations messageSendingOperations;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a connection -> " + event.getUser());
    }

    @EventListener
    public void handleWebSocketDisconectListener(SessionDisconnectEvent event) {
        log.info("Received a event of disconnection.");

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) accessor.getSessionAttributes().get("username");

        if(username == null) {
            log.info("Cannot possible to get the username to leave the chat! ");
            return;
        }

        log.info("User to be disconected -> " + username.toUpperCase());

        ChatMessage message = new ChatMessage();
        message.setMessageType(MessageType.LEAVE);
        message.setSender(username);
        message.setContent("User " + username + " leave the chat!");

        messageSendingOperations.convertAndSend("/topic/public", message);
    }

}
