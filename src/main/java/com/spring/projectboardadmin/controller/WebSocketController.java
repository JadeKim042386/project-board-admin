package com.spring.projectboardadmin.controller;

import com.spring.projectboardadmin.dto.websocket.WebSocketMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketController {
    @MessageMapping("/hello")
    @SendTo("/topic/chat")
    public WebSocketMessage chat(WebSocketMessage message, Principal principal) throws InterruptedException {
        Thread.sleep(1000);
        return WebSocketMessage.of("hi" + principal.getName() + "! " + message.content());
    }
}
