package com.woopaca.laboratory.transaction.deadlock;

import com.woopaca.laboratory.transaction.deadlock.entity.Post;
import com.woopaca.laboratory.transaction.deadlock.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SimpleCommentServiceTest {

    private static final int TOTAL_COUNT = 3;

    private Post post;
    private ExecutorService executorService;
    private CountDownLatch latch;

    @Autowired
    SimpleCommentService commentService;

    @Autowired
    PostRepository postRepository;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(TOTAL_COUNT);
        latch = new CountDownLatch(TOTAL_COUNT);
        post = postRepository.save(new Post("제목", "내용"));
    }

    @Test
    void 댓글_작성_동시성_테스트() throws Exception {
        for (int i = 0; i < TOTAL_COUNT; i++) {
            int commentNumber = i + 1;
            executorService.submit(() -> {
                try {
                    commentService.writeComment(post.getId(), "comment" + commentNumber);
                } catch (Exception e) {
                    log.error("Exception", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
    }
}
