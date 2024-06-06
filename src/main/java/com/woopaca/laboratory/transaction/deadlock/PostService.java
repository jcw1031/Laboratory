package com.woopaca.laboratory.transaction.deadlock;

import com.woopaca.laboratory.transaction.deadlock.entity.Post;
import com.woopaca.laboratory.transaction.deadlock.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void updatePostCommentsCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("post not found for post id [%d]", postId)));
        post.increaseCommentsCount();
    }
}
