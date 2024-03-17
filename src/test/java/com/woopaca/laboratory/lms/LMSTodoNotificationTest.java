package com.woopaca.laboratory.lms;

import com.woopaca.laboratory.lms.dto.Course;
import com.woopaca.laboratory.lms.dto.Todo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
class LMSTodoNotificationTest {

    @Autowired
    private LMSTodoNotification lmsTodoNotification;

    @Test
    void test() {
        List<Course> courses = lmsTodoNotification.getCourses();
        List<List<Todo>> todosList = courses.stream()
                .map(lmsTodoNotification::getTodo)
                .filter(todos -> !todos.isEmpty())
                .map(todos -> todos.)
                .toList();
        log.info("todosList = {}", todosList);
    }
}