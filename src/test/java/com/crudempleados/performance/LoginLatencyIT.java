package com.crudempleados.performance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import com.crudempleados.support.AbstractApiIntegrationTest;
import com.crudempleados.support.EmpleadoTestDataFactory;
import org.junit.jupiter.api.Test;

class LoginLatencyIT extends AbstractApiIntegrationTest {

    @Test
    void shouldKeepLoginP95BelowTwoSecondsUnderNormalLoadSample() throws Exception {
        Map<String, Object> createPayload = EmpleadoTestDataFactory.createRequestWithCredentials(
            "PERF1", "empleado.perf1@empresa.com", "abc12345");

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", basicAuthHeaderValue())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPayload)))
            .andExpect(status().isCreated());

        int requests = 100;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Long>> futures = new ArrayList<>();

        for (int i = 0; i < requests; i++) {
            Callable<Long> task = () -> {
                startGate.await();
                long start = System.nanoTime();
                mockMvc.perform(post("/api/v1/empleados/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                            EmpleadoTestDataFactory.loginRequest("empleado.perf1@empresa.com", "abc12345"))))
                    .andExpect(status().isOk());
                return Duration.ofNanos(System.nanoTime() - start).toMillis();
            };
            futures.add(executor.submit(task));
        }

        startGate.countDown();
        executor.shutdown();
        boolean completed = executor.awaitTermination(2, TimeUnit.MINUTES);
        assertThat(completed).isTrue();

        List<Long> durationsMs = new ArrayList<>();
        for (Future<Long> future : futures) {
            durationsMs.add(future.get(10, TimeUnit.SECONDS));
        }

        Collections.sort(durationsMs);
        int p95Index = (int) Math.ceil(durationsMs.size() * 0.95) - 1;
        long p95 = durationsMs.get(Math.max(0, p95Index));
        assertThat(p95).isLessThan(2000L);
    }
}