CREATE SEQUENCE departamentos_clave_numero_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE departamentos (
    clave_prefijo VARCHAR(4) NOT NULL,
    clave_numero BIGINT NOT NULL DEFAULT nextval('departamentos_clave_numero_seq'),
    nombre VARCHAR(100) NOT NULL,
    nombre_normalizado VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_departamentos PRIMARY KEY (clave_prefijo, clave_numero),
    CONSTRAINT uq_departamentos_nombre_normalizado UNIQUE (nombre_normalizado),
    CONSTRAINT chk_departamentos_prefijo CHECK (clave_prefijo = 'DEP-'),
    CONSTRAINT chk_departamentos_clave_numero_pos CHECK (clave_numero > 0),
    CONSTRAINT chk_departamentos_nombre_len CHECK (char_length(nombre) <= 100 AND char_length(trim(nombre)) > 0),
    CONSTRAINT chk_departamentos_nombre_normalizado_len CHECK (char_length(nombre_normalizado) <= 100 AND char_length(trim(nombre_normalizado)) > 0)
);

ALTER SEQUENCE departamentos_clave_numero_seq OWNED BY departamentos.clave_numero;

ALTER TABLE empleados
    ADD COLUMN departamento_clave_prefijo VARCHAR(4),
    ADD COLUMN departamento_clave_numero BIGINT;

ALTER TABLE empleados
    ADD CONSTRAINT fk_empleados_departamento
    FOREIGN KEY (departamento_clave_prefijo, departamento_clave_numero)
    REFERENCES departamentos (clave_prefijo, clave_numero)
    ON DELETE SET NULL;

CREATE INDEX idx_empleados_departamento
    ON empleados (departamento_clave_prefijo, departamento_clave_numero);