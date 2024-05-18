package com.woopaca.laboratory.mockito;

import com.google.errorprone.annotations.DoNotMock;
import org.springframework.stereotype.Service;

@DoNotMock
@Service
public class NotToMock {

    public void printMethodName() {
        System.out.println("NotToMock.printMethodName");
    }
}
