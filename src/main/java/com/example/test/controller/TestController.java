package com.example.test.controller;

import com.example.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/test")
public class TestController {

    @Autowired
    TestService testService;

    @PostMapping
    public void post() throws Exception {
        testService.test();

    }
}
