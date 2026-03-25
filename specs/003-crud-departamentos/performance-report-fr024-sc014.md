# Performance Report FR-024 / SC-014

## Scope

- Endpoint 1: GET /api/v1/departamentos?page=0&size=20
- Endpoint 2: GET /api/v1/departamentos/{clave}
- Virtual users: 20
- Duration: 5 minutes

## Thresholds

- p95 <= 300 ms
- p99 <= 500 ms
- error rate < 1%

## Execution Command

```powershell
.\mvnw.cmd -Dtest=DepartamentoQueryPerformanceTest -Dperf.users=20 -Dperf.duration.ms=300000 -Dperf.p95.max.ms=300 -Dperf.p99.max.ms=500 -Dperf.max.error.rate=0.01 test
```

## Results

- Status: Blocked in this environment
- p95: N/A (test skipped)
- p99: N/A (test skipped)
- Error rate: N/A (test skipped)
- Total requests: N/A (test skipped)
- Failed requests: N/A (test skipped)

Execution evidence from this run:

- `mvn -Dtest=DepartamentoQueryPerformanceTest -Dperf.users=20 -Dperf.duration.ms=300000 -Dperf.p95.max.ms=300 -Dperf.p99.max.ms=500 -Dperf.max.error.rate=0.01 test`
- Build result: SUCCESS
- Test result: `DepartamentoQueryPerformanceTest` skipped by Testcontainers (`Could not find a valid Docker environment`)
- Docker CLI check in the same session: `docker version` returned client/server versions successfully

## Conclusion

The performance scenario command is defined and reproducible, but threshold validation is blocked in this session because Testcontainers cannot attach to a valid Docker environment despite Docker CLI availability.

Next required step to close FR-024/SC-014 evidence:

1. Resolve Testcontainers-Docker connectivity in the workstation/CI runner.
2. Re-run the performance command above.
3. Replace N/A values with measured p95, p99, error rate and final pass/fail conclusion.
