package com.example.test.service;

import com.example.test.logging.ServiceLogging;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    @ServiceLogging(message = "Test message for logger")
    public void test() throws Exception {

        System.out.println("Executing Service method annotated with @ServiceLogging");

        throw new Exception("Expected Exception from TestService.test");
    }
}
