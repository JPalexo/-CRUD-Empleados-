package com.crudempleados.performance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.support.AbstractApiIntegrationTest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class CrudLatencyIT extends AbstractApiIntegrationTest {

    @Test
    void shouldKeepP95BelowTwoSecondsForNormalLoadSample() throws Exception {
        for (int i = 0; i < 10; i++) {
            seedEmpleado("Perf " + i, "Direccion " + i, "Telefono " + i);
        }

        int requests = 120;
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Long>> futures = new ArrayList<>();

        for (int i = 0; i < requests; i++) {
            Callable<Long> task = () -> {
                startGate.await();
                long start = System.nanoTime();
                mockMvc.perform(get("/api/v1/empleados")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization", basicAuthHeaderValue()))
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
            durationsMs.add(future.get(5, TimeUnit.SECONDS));
        }

        Collections.sort(durationsMs);
        int p95Index = (int) Math.ceil(durationsMs.size() * 0.95) - 1;
        long p95 = durationsMs.get(Math.max(0, p95Index));
        assertThat(p95).isLessThan(2000L);
    }
}
