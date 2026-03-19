<!--
Sync Impact Report
- Version change: template-placeholder -> 1.0.0
- Modified principles:
	- PRINCIPLE_1_NAME -> I. Spring Boot 3 + Java 17 Baseline
	- PRINCIPLE_2_NAME -> II. Basic Authentication by Default
	- PRINCIPLE_3_NAME -> III. PostgreSQL + Flyway Schema Control
	- PRINCIPLE_4_NAME -> IV. Dockerized Local Runtime
	- PRINCIPLE_5_NAME -> V. OpenAPI/Swagger Contract Transparency
- Added sections:
	- Engineering Standards
	- Delivery Workflow and Quality Gates
- Removed sections:
	- None
- Templates requiring updates:
	- ✅ updated: .specify/templates/plan-template.md
	- ✅ updated: .specify/templates/spec-template.md
	- ✅ updated: .specify/templates/tasks-template.md
	- ⚠ pending (directory not present): .specify/templates/commands/*.md
- Deferred TODOs:
	- TODO(COMMAND_TEMPLATES_DIR): Initialize .specify/templates/commands/ to enable command-level constitutional checks.
-->

# CRUD_Empleados_2 Constitution

## Core Principles

### I. Spring Boot 3 + Java 17 Baseline
All backend features MUST run on Spring Boot 3.x and Java 17.
New code MUST use Spring idioms compatible with this baseline and MUST NOT introduce
dependencies or language features that require a different Java/Spring major version.
Rationale: a single runtime baseline reduces operational drift and keeps builds,
security updates, and deployment tooling predictable.

### II. Basic Authentication by Default
All exposed HTTP endpoints MUST be protected with Spring Security Basic Authentication,
except explicitly documented public endpoints (for example health probes or Swagger
assets when approved). Authentication and authorization rules MUST be tested with
positive and negative scenarios.
Rationale: secure-by-default behavior is mandatory for employee data and prevents
accidental exposure during early development and deployment stages.

### III. PostgreSQL + Flyway Schema Control
Persistent data MUST be stored in PostgreSQL. Any schema change MUST be delivered
through versioned Flyway migrations and MUST be reproducible from an empty database.
Direct manual schema edits in shared environments are prohibited.
Rationale: migration-as-code guarantees deterministic schema evolution and minimizes
environment inconsistencies.

### IV. Dockerized Local Runtime
Local development and integration verification MUST run through Dockerized
infrastructure (at minimum PostgreSQL through Docker Compose). Application
configuration MUST support container-based execution using environment variables.
Rationale: containerized parity shortens onboarding time and reveals integration issues
before deployment.

### V. OpenAPI/Swagger Contract Transparency
Every REST API change MUST be reflected in generated OpenAPI documentation and be
visible in Swagger UI. Pull requests MUST include verification that documentation stays
in sync with controller contracts, request/response models, and auth requirements.
Rationale: up-to-date API contracts reduce integration failures and improve maintainability.

## Engineering Standards

- Backend architecture MUST follow layered boundaries: controller, service,
	repository, and domain model separation.
- Configuration MUST use environment-specific profiles and externalized secrets;
	credentials MUST NOT be hardcoded.
- Observability SHOULD include structured logs and Spring Boot Actuator health checks.
	When omitted, the plan MUST justify why and how operability is preserved.
- Any dependency added to support a feature MUST be justified in the implementation
	plan and reviewed for security and maintenance impact.

## Delivery Workflow and Quality Gates

- Every feature spec and plan MUST include a constitutional compliance section
	confirming stack, auth, data, container, and documentation alignment.
- Before merge, CI or manual verification MUST prove:
	- project builds with Java 17,
	- migrations apply successfully with Flyway,
	- authenticated API paths enforce Basic Auth,
	- Dockerized PostgreSQL startup is functional,
	- Swagger/OpenAPI output is available and current.
- Pull requests MUST document changed endpoints, migration files, and security impacts.

## Governance

This constitution is the highest-priority engineering policy for this repository.
Amendments require a pull request that includes:
- a clear rationale,
- impact analysis on templates and workflows,
- migration steps for in-flight work.

Versioning policy:
- MAJOR for backward-incompatible governance changes or principle removals/redefinitions.
- MINOR for new principles/sections or materially expanded mandatory guidance.
- PATCH for clarifications, wording improvements, and non-semantic refinements.

Compliance review expectations:
- Reviewers MUST validate constitutional compliance for every plan/spec/tasks artifact.
- Any approved exception MUST be explicitly documented with scope, owner, and expiry.
- Periodic compliance audits SHOULD be executed at least once per release cycle.

**Version**: 1.0.0 | **Ratified**: 2026-03-17 | **Last Amended**: 2026-03-17
