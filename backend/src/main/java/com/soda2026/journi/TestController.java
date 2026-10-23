package com.soda2026.journi;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final JdbcTemplate jdbcTemplate;
    public TestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public TestResponse testConnection(){

        Integer userCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users",
                Integer.class
        );
        return new TestResponse(
                "Frontend, backend och databas fungerar. Users: " + userCount
        );
    }

    public record TestResponse(String message){}
}
