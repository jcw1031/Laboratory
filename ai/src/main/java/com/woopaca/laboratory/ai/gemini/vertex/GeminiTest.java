package com.woopaca.laboratory.ai.gemini.vertex;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class GeminiTest {

    private final ResourceLoader resourceLoader;
    private final ChatModel chatModel;

    public GeminiTest(ResourceLoader resourceLoader, ChatModel chatModel) {
        this.resourceLoader = resourceLoader;
        this.chatModel = chatModel;
    }

    @PostConstruct
    public void test() throws IOException {
        Resource pdfFile = resourceLoader.getResource("classpath:gemini/vertex/문장부호 가이드.pdf");

        SystemMessage systemMessage = SystemMessage.builder()
                .text("""
                        You are a helpful assistant that can read and understand PDF files.
                        Please read the provided PDF file and answer questions about its content.
                        """)
                .build();
        UserMessage userMessage = UserMessage.builder()
                .text("이 문서는 어떤 내용이야?")
                .media(List.of(new Media(new MimeType("application", "pdf"), pdfFile)))
                .build();

        ChatResponse response = chatModel.call(new Prompt(List.of(systemMessage, userMessage)));
        log.info("Response: {}", response.getResult());
    }
}
