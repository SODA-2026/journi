package com.soda2026.journi.invitation;

import com.soda2026.journi.invitation.Dto.InvitationRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "KAN40_MYSQL_TESTS", matches = "true")
class InvitationConcurrencyIntegrationTest {

    private static final int REQUEST_COUNT = 6;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Opt in with KAN40_MYSQL_TESTS=true using a local test database.
    // Real MySQL is necessary to reproduce its default REPEATABLE_READ snapshot behaviour.
    @Test
    void concurrentDuplicateInvitationsReturnConflictInsteadOfServerError() throws Exception {
        String suffix = UUID.randomUUID().toString();
        String ownerEmail = "kan40-owner-" + suffix + "@example.com";
        String inviteeEmail = "kan40-invitee-" + suffix + "@example.com";
        Long tripId = null;

        try {
            Long ownerId = createUser(ownerEmail);
            createUser(inviteeEmail);
            tripId = createTrip(ownerId);

            InvitationRequestDto request = new InvitationRequestDto(tripId, inviteeEmail);
            CyclicBarrier barrier = new CyclicBarrier(REQUEST_COUNT);
            List<Callable<Integer>> requests = new ArrayList<>();

            for (int i = 0; i < REQUEST_COUNT; i++) {
                requests.add(() -> {
                    // Start all requests together to exercise the trip lock.
                    barrier.await(10, TimeUnit.SECONDS);
                    try {
                        invitationService.sendInvitation(request, ownerId);
                        return 201;
                    } catch (ResponseStatusException error) {
                        return error.getStatusCode().value();
                    }
                });
            }

            List<Integer> statuses = new ArrayList<>();
            try (ExecutorService executor = Executors.newFixedThreadPool(REQUEST_COUNT)) {
                List<Future<Integer>> results = executor.invokeAll(requests, 30, TimeUnit.SECONDS);
                for (Future<Integer> result : results) {
                    statuses.add(result.get());
                }
            }

            Collections.sort(statuses);
            assertEquals(List.of(201, 409, 409, 409, 409, 409), statuses);
            Long invitationCount = jdbcTemplate.queryForObject(
                    "select count(*) from invitations where trip_id = ?", Long.class, tripId);
            assertEquals(1L, invitationCount);
        } finally {
            if (tripId != null) {
                jdbcTemplate.update("delete from trips where id = ?", tripId);
            }
            jdbcTemplate.update("delete from users where email in (?, ?)", ownerEmail, inviteeEmail);
        }
    }

    private Long createTrip(Long ownerId) {
        jdbcTemplate.update("""
                insert into trips (owner_id, title, start_date, end_date)
                values (?, 'KAN40 concurrency regression', '2099-12-01', '2099-12-05')
                """, ownerId);
        return jdbcTemplate.queryForObject(
                "select id from trips where owner_id = ?", Long.class, ownerId);
    }

    private Long createUser(String email) {
        jdbcTemplate.update("insert into users (name, email, password_hash) values (?, ?, ?)",
                "KAN40 concurrency test", email, "unused-test-password-hash");
        return jdbcTemplate.queryForObject("select id from users where email = ?", Long.class, email);
    }
}
