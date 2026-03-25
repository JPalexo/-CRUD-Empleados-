package com.crudempleados.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crudempleados.model.Departamento;
import com.crudempleados.model.Empleado;
import com.crudempleados.support.AbstractApiIntegrationTest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class DepartamentoConcurrencyIT extends AbstractApiIntegrationTest {

    @Test
    void shouldKeepConsistentMetricsUnderConcurrentReadsAtCapacityLimit() throws Exception {
        Departamento departamento = seedDepartamento("Concurrencia");
        Empleado e1 = seedEmpleado("C1", "D1", "T1");
        Empleado e2 = seedEmpleado("C2", "D2", "T2");
        Empleado e3 = seedEmpleado("C3", "D3", "T3");
        assignEmpleadoToDepartamento(e1, departamento);
        assignEmpleadoToDepartamento(e2, departamento);
        assignEmpleadoToDepartamento(e3, departamento);

        String clave = "DEP-" + departamento.getClaveNumero();
        int threads = 20;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            Callable<Boolean> task = () -> {
                startGate.await();
                mockMvc.perform(get("/api/v1/departamentos/{clave}", clave)
                        .header("Authorization", basicAuthHeaderValue()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.ocupacionActual").value(3))
                    .andExpect(jsonPath("$.capacidadMaxima").value(3))
                    .andExpect(jsonPath("$.estaLleno").value(true));
                return true;
            };
            futures.add(pool.submit(task));
        }

        startGate.countDown();
        pool.shutdown();
        boolean completed = pool.awaitTermination(2, TimeUnit.MINUTES);

        assertThat(completed).isTrue();
        for (Future<Boolean> future : futures) {
            assertThat(future.get(5, TimeUnit.SECONDS)).isTrue();
        }
    }
}
