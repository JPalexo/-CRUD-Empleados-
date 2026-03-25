<!--
Sync Impact Report
- Version change: 1.0.0 -> 1.1.0
- Modified principles:
	- None renamed
	- Added VI. Frontend Angular 22 LTS Baseline
- Added sections:
	- None
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

### VI. Frontend Angular 22 LTS Baseline
All new UI features MUST be implemented with Angular 22 LTS. Frontend code MUST use
strict TypeScript and a feature-modular architecture with, at minimum, pages,
components, services, models, and guards/interceptors separation. HTTP errors and
loading states MUST be handled centrally. Environment variables MUST be profile-based
(dev/test/prod), and secrets MUST NOT be hardcoded.
Rationale: a single frontend baseline preserves maintainability, reduces integration
drift, and enforces predictable quality for user-facing features.

## Engineering Standards

- Backend architecture MUST follow layered boundaries: controller, service,
	repository, and domain model separation.
- Frontend API consumption MUST be implemented through typed service layers and MUST
	consume versioned endpoints under /api/v1.
- Frontend styling MUST reuse design tokens for colors, typography, and spacing.
- Frontend views MUST provide responsive behavior for desktop and mobile breakpoints.
- Frontend forms MUST enforce validation rules aligned with backend constraints.
- Frontend accessibility baseline MUST include explicit labels, visible focus states,
	and sufficient color contrast.
- Configuration MUST use environment-specific profiles and externalized secrets;
	credentials MUST NOT be hardcoded.
- Observability SHOULD include structured logs and Spring Boot Actuator health checks.
	When omitted, the plan MUST justify why and how operability is preserved.
- Any dependency added to support a feature MUST be justified in the implementation
	plan and reviewed for security and maintenance impact.

## Delivery Workflow and Quality Gates

- Every feature spec and plan MUST include a constitutional compliance section
	confirming stack, auth, data, container, frontend, and documentation alignment.
- Before merge, CI or manual verification MUST prove:
	- project builds with Java 17,
	- frontend builds successfully on Angular 22 LTS,
	- frontend lint passes with no blocking errors,
	- migrations apply successfully with Flyway,
	- authenticated API paths enforce Basic Auth,
	- Dockerized PostgreSQL startup is functional,
	- frontend-backend integration works in local Docker environment,
	- frontend consumes versioned API endpoints under /api/v1,
	- Swagger/OpenAPI output is available and current.
- Pull requests MUST document changed endpoints, migration files, frontend impacts,
	and security impacts.

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

**Version**: 1.1.0 | **Ratified**: 2026-03-17 | **Last Amended**: 2026-03-25
