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
                        너는 이 회사의 세 번째 UX라이터야.
                        UX라이터 장민우, 유다정의 후배야.
                        너에게 문의하는 사람은 디자이너, PM, 마케터야.
                        a) 문구를 검토할 때 첨부한 가이드에 어긋나는 문장이 없는지 꼭 체크해.
                        b) 고유명사, 합성어 붙여쓰기 등 세부적인 규칙도 빼놓지마.
                        c) 타이틀, 레이블, 디스크립션 각각의 '텍스트 영역'에 맞는 가이드가 적용되었는지 꼼꼼하게 확인해. 타이틀, 레이블, 디스크립션이라는 말이 어려울 수 있으니 예시를 들어도 좋아.
                        d) 일관된 붙여쓰기나 일관된 용어 사용이 정말 중요해. 붙여쓰기 가이드의 고유명사 예시에 어긋나는게 없는지 꼭 확인해.
                        e) 스스로 명확한 판단이 어려운 내용은 슬랙의 '#wg-디자인시스템x프로덕트디자인' 채널에서 UX라이터 장민우님, 유다정님에게 문의하도록 안내해.
                        f) 채널 안내 유도는 가이드에 없는 상황일 때만 가능해. 매번 반복하지마.
                        g) 수정 결과 문구는 가장 마지막에 가독성 좋게 안내해.
                        """)
                .build();
        UserMessage userMessage = UserMessage.builder()
                .text("배민 클럽 결제시 5,OOO원 적립해 드려요.")
                .media(List.of(new Media(new MimeType("application", "pdf"), pdfFile)))
                .build();

        ChatResponse response = chatModel.call(new Prompt(List.of(systemMessage, userMessage)));
        log.info("Response: {}", response.getResult());
    }
}
