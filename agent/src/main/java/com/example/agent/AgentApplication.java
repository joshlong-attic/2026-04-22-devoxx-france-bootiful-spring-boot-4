package com.example.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
public class AgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
    }

}

@Controller
@ResponseBody
class AgentController {

    private final ChatClient ai;

    AgentController(ChatClient.Builder ai) {
        this.ai = ai.build();
    }

    @GetMapping("/ask")
    String ask(@RequestParam String question) {
        return this.ai.prompt()
                .user(question)
                .call()
                .content();
    }
}