package com.soda2026.journi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public TestResponse hello(){
        return new TestResponse("hej från backend");
    }

    public record TestResponse(String message){}
}
