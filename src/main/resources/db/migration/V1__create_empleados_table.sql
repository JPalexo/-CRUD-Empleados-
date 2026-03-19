CREATE SEQUENCE empleados_clave_numero_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE empleados (
    clave_prefijo VARCHAR(4) NOT NULL,
    clave_numero BIGINT NOT NULL DEFAULT nextval('empleados_clave_numero_seq'),
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(100) NOT NULL,
    telefono VARCHAR(100) NOT NULL,
    CONSTRAINT pk_empleados PRIMARY KEY (clave_prefijo, clave_numero),
    CONSTRAINT chk_empleados_prefijo CHECK (clave_prefijo = 'EMP-'),
    CONSTRAINT chk_empleados_clave_numero_pos CHECK (clave_numero > 0),
    CONSTRAINT chk_empleados_nombre_len CHECK (char_length(nombre) <= 100 AND char_length(trim(nombre)) > 0),
    CONSTRAINT chk_empleados_direccion_len CHECK (char_length(direccion) <= 100 AND char_length(trim(direccion)) > 0),
    CONSTRAINT chk_empleados_telefono_len CHECK (char_length(telefono) <= 100 AND char_length(trim(telefono)) > 0)
);

ALTER SEQUENCE empleados_clave_numero_seq OWNED BY empleados.clave_numero;
