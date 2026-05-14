package com.crudempleados.support;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.crudempleados.domain.ClaveEmpleadoCodec;
import com.crudempleados.model.Departamento;
import com.crudempleados.model.Empleado;
import com.crudempleados.repository.DepartamentoRepository;
import com.crudempleados.repository.EmpleadoCredencialRepository;
import com.crudempleados.repository.EmpleadoLoginEventoRepository;
import com.crudempleados.repository.EmpleadoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "CRUD_BASIC_USER=admin",
    "CRUD_BASIC_PASSWORD=admin123"
})
public abstract class AbstractApiIntegrationTest {

    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("empleados")
        .withUsername("empleados_app")
        .withPassword("empleados_pass");

    static {
        // Keep one container instance alive for the whole test JVM to avoid
        // stale Spring context datasource ports across test classes.
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("CRUD_DB_HOST", POSTGRES::getHost);
        registry.add("CRUD_DB_PORT", () -> String.valueOf(POSTGRES.getMappedPort(5432)));
        registry.add("CRUD_DB_NAME", POSTGRES::getDatabaseName);
        registry.add("CRUD_DB_USER", POSTGRES::getUsername);
        registry.add("CRUD_DB_PASSWORD", POSTGRES::getPassword);
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected EmpleadoRepository empleadoRepository;

    @Autowired
    protected DepartamentoRepository departamentoRepository;

    @Autowired
    protected EmpleadoCredencialRepository empleadoCredencialRepository;

    @Autowired
    protected EmpleadoLoginEventoRepository empleadoLoginEventoRepository;

    @BeforeEach
    void clearData() {
        empleadoLoginEventoRepository.deleteAll();
        empleadoCredencialRepository.deleteAll();
        empleadoRepository.deleteAll();
        departamentoRepository.deleteAll();
    }

    protected String basicAuthHeaderValue() {
        return basicAuthHeaderValue("admin", "admin123");
    }

    protected String basicAuthHeaderValue(String username, String password) {
        String credentials = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }

    protected Empleado seedEmpleado(String nombre, String direccion, String telefono) {
        Empleado empleado = new Empleado();
        empleado.setClavePrefijo(ClaveEmpleadoCodec.PREFIX);
        empleado.setNombre(nombre);
        empleado.setDireccion(direccion);
        empleado.setTelefono(telefono);
        return empleadoRepository.save(empleado);
    }

    protected Departamento seedDepartamento(String nombre) {
        Departamento departamento = new Departamento();
        departamento.setNombre(nombre.trim());
        departamento.setNombreNormalizado(nombre.trim().toLowerCase());
        return departamentoRepository.save(departamento);
    }

    protected String seedDepartamentoClave(String nombre) {
        Departamento departamento = seedDepartamento(nombre);
        return "DEP-" + departamento.getClaveNumero();
    }

    protected void assignEmpleadoToDepartamento(Empleado empleado, Departamento departamento) {
        empleado.setDepartamentoClavePrefijo(departamento.getClavePrefijo());
        empleado.setDepartamentoClaveNumero(departamento.getClaveNumero());
        empleadoRepository.save(empleado);
    }

    protected long parseNumeroFromClave(String clave) {
        return Long.parseLong(clave.substring(ClaveEmpleadoCodec.PREFIX.length()));
    }
}
