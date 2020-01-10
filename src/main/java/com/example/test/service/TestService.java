package com.example.test.service;

import com.example.test.logging.ServiceLogging;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @ServiceLogging
    public void test() throws Exception {

        System.out.println("test123");

        throw new Exception("test123");
    }
}
