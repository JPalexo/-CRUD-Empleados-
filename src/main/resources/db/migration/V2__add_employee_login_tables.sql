CREATE TABLE empleado_credenciales (
    clave_prefijo VARCHAR(4) NOT NULL,
    clave_numero BIGINT NOT NULL,
    email_normalizado VARCHAR(254) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    habilitada BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_empleado_credenciales PRIMARY KEY (clave_prefijo, clave_numero),
    CONSTRAINT uq_empleado_credenciales_email UNIQUE (email_normalizado),
    CONSTRAINT fk_empleado_credenciales_empleado
        FOREIGN KEY (clave_prefijo, clave_numero)
        REFERENCES empleados (clave_prefijo, clave_numero)
        ON DELETE CASCADE,
    CONSTRAINT chk_empleado_credenciales_email_nonblank
        CHECK (char_length(trim(email_normalizado)) > 0),
    CONSTRAINT chk_empleado_credenciales_password_hash_nonblank
        CHECK (char_length(trim(password_hash)) > 0)
);

CREATE TABLE empleado_login_eventos (
    id BIGSERIAL PRIMARY KEY,
    email_normalizado_input VARCHAR(254) NOT NULL,
    clave_prefijo VARCHAR(4),
    clave_numero BIGINT,
    resultado VARCHAR(16) NOT NULL,
    motivo VARCHAR(64) NOT NULL,
    ocurrido_en TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_empleado_login_eventos_empleado
        FOREIGN KEY (clave_prefijo, clave_numero)
        REFERENCES empleados (clave_prefijo, clave_numero)
        ON DELETE SET NULL,
    CONSTRAINT chk_empleado_login_eventos_resultado
        CHECK (resultado IN ('SUCCESS', 'FAILURE')),
    CONSTRAINT chk_empleado_login_eventos_email_nonblank
        CHECK (char_length(trim(email_normalizado_input)) > 0)
);

CREATE INDEX idx_empleado_login_eventos_email_ocurrido_en
    ON empleado_login_eventos (email_normalizado_input, ocurrido_en DESC);

CREATE INDEX idx_empleado_login_eventos_ocurrido_en
    ON empleado_login_eventos (ocurrido_en DESC);