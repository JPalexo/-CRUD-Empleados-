package com.crudempleados.performance;

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
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.model.Departamento;
import com.crudempleados.model.Empleado;
import com.crudempleados.support.AbstractApiIntegrationTest;

class DepartamentoQueryPerformanceTest extends AbstractApiIntegrationTest {

    @Test
    void shouldMeetFr024Sc014ThresholdsUnderConfiguredLoad() throws Exception {
        Departamento d1 = seedDepartamento("Perf Ventas");
        Departamento d2 = seedDepartamento("Perf Operaciones");
        Empleado e1 = seedEmpleado("P1", "D1", "T1");
        Empleado e2 = seedEmpleado("P2", "D2", "T2");
        assignEmpleadoToDepartamento(e1, d1);
        assignEmpleadoToDepartamento(e2, d2);

        String detailClave = "DEP-" + d1.getClaveNumero();

        int users = Integer.parseInt(System.getProperty("perf.users", "10"));
        long durationMs = Long.parseLong(System.getProperty("perf.duration.ms", String.valueOf(TimeUnit.MINUTES.toMillis(1))));
        long p95ThresholdMs = Long.parseLong(System.getProperty("perf.p95.max.ms", "450"));
        long p99ThresholdMs = Long.parseLong(System.getProperty("perf.p99.max.ms", "700"));
        double maxErrorRate = Double.parseDouble(System.getProperty("perf.max.error.rate", "0.02"));

        ExecutorService pool = Executors.newFixedThreadPool(users);
        CountDownLatch startGate = new CountDownLatch(1);
        long endAtNanos = System.nanoTime() + Duration.ofMillis(durationMs).toNanos();

        List<Future<List<Long>>> futures = new ArrayList<>();
        AtomicInteger totalRequests = new AtomicInteger(0);
        AtomicInteger failedRequests = new AtomicInteger(0);

        for (int i = 0; i < users; i++) {
            Callable<List<Long>> task = () -> {
                List<Long> durations = new ArrayList<>();
                startGate.await();
                while (System.nanoTime() < endAtNanos) {
                    long listStart = System.nanoTime();
                    totalRequests.incrementAndGet();
                    try {
                        mockMvc.perform(get("/api/v1/departamentos")
                                .param("page", "0")
                                .param("size", "20")
                                .header("Authorization", basicAuthHeaderValue()))
                            .andExpect(status().isOk());
                        durations.add(Duration.ofNanos(System.nanoTime() - listStart).toMillis());
                    } catch (Exception ex) {
                        failedRequests.incrementAndGet();
                    }

                    long detailStart = System.nanoTime();
                    totalRequests.incrementAndGet();
                    try {
                        mockMvc.perform(get("/api/v1/departamentos/{clave}", detailClave)
                                .header("Authorization", basicAuthHeaderValue()))
                            .andExpect(status().isOk());
                        durations.add(Duration.ofNanos(System.nanoTime() - detailStart).toMillis());
                    } catch (Exception ex) {
                        failedRequests.incrementAndGet();
                    }
                }
                return durations;
            };
            futures.add(pool.submit(task));
        }

        startGate.countDown();
        pool.shutdown();
        boolean finished = pool.awaitTermination(durationMs + TimeUnit.MINUTES.toMillis(2), TimeUnit.MILLISECONDS);
        assertThat(finished).isTrue();

        List<Long> allDurations = new ArrayList<>();
        for (Future<List<Long>> future : futures) {
            allDurations.addAll(future.get(30, TimeUnit.SECONDS));
        }

        assertThat(allDurations).isNotEmpty();
        Collections.sort(allDurations);

        long p95 = percentile(allDurations, 95);
        long p99 = percentile(allDurations, 99);
        double errorRate = totalRequests.get() == 0 ? 0.0 : ((double) failedRequests.get() / (double) totalRequests.get());

        assertThat(p95).isLessThanOrEqualTo(p95ThresholdMs);
        assertThat(p99).isLessThanOrEqualTo(p99ThresholdMs);
        assertThat(errorRate).isLessThan(maxErrorRate);
    }

    private long percentile(List<Long> values, int percentile) {
        int index = (int) Math.ceil(values.size() * (percentile / 100.0)) - 1;
        return values.get(Math.max(0, index));
    }
}
