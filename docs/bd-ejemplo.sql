-- 1. Membresías (tabla plural)
CREATE TABLE IF NOT EXISTS membresias (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL
);

-- 2. Personas (tabla plural) con email único
CREATE TABLE IF NOT EXISTS personas (
  id SERIAL PRIMARY KEY,
  p_nombre VARCHAR(50) NOT NULL,
  s_nombre VARCHAR(50),
  ap_paterno VARCHAR(50) NOT NULL,
  ap_materno VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  telefono VARCHAR(25),
  fecha_nacimiento DATE NOT NULL,
  membresia_id INTEGER NOT NULL REFERENCES membresias(id)
);

-- Índice recomendado para búsquedas por email
CREATE INDEX IF NOT EXISTS idx_personas_email ON personas (email);

-- 3. Clientes (asociados a personas y membresías)
CREATE TABLE IF NOT EXISTS clientes (
  id SERIAL PRIMARY KEY,
  persona_id INTEGER NOT NULL REFERENCES personas(id),
  membresia_id INTEGER NOT NULL REFERENCES membresias(id),
  UNIQUE (persona_id, membresia_id)
);

-- 4. Trabajadores
CREATE TABLE IF NOT EXISTS trabajadores (
  id SERIAL PRIMARY KEY,
  persona_id INTEGER NOT NULL REFERENCES personas(id),
  membresia_id INTEGER NOT NULL REFERENCES membresias(id),
  UNIQUE (persona_id, membresia_id)
);

-- 5. Países
CREATE TABLE IF NOT EXISTS paises (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL
);

-- 6. Regiones
CREATE TABLE IF NOT EXISTS regiones (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  pais_id INTEGER NOT NULL REFERENCES paises(id)
);

-- 7. Ciudades
CREATE TABLE IF NOT EXISTS ciudades (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  region_id INTEGER NOT NULL REFERENCES regiones(id)
);

-- 8. Comunas
CREATE TABLE IF NOT EXISTS comunas (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  ciudad_id INTEGER NOT NULL REFERENCES ciudades(id)
);

-- 9. Categorías de servicio
CREATE TABLE IF NOT EXISTS categorias_servicio (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL
);

-- 10. Servicios con boolean para estado【765255497063228†L50-L57】
CREATE TABLE IF NOT EXISTS servicios (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT,
  is_activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- 11. Tabla de asignación entre servicios y categorías
CREATE TABLE IF NOT EXISTS servicios_categorias (
  servicio_id INTEGER NOT NULL REFERENCES servicios(id) ON DELETE CASCADE,
  categoria_servicio_id INTEGER NOT NULL REFERENCES categorias_servicio(id) ON DELETE CASCADE,
  PRIMARY KEY (servicio_id, categoria_servicio_id)
);

-- 12. Perfiles (usuario ofertante) con username único
CREATE TABLE IF NOT EXISTS perfiles (
  id SERIAL PRIMARY KEY,
  descripcion TEXT,
  username VARCHAR(50) NOT NULL UNIQUE,
  cliente_id INTEGER NOT NULL REFERENCES clientes(id),
  trabajador_id INTEGER NOT NULL REFERENCES trabajadores(id),
  categoria_servicio_id INTEGER REFERENCES categorias_servicio(id),
  tipo_ofrecimiento_id INTEGER
);

-- 13. Certificaciones asociadas a perfiles
CREATE TABLE IF NOT EXISTS certificaciones (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  entidad_emisora VARCHAR(100) NOT NULL,
  fecha_emision DATE NOT NULL,
  url_verificacion VARCHAR(255) NOT NULL,
  perfil_id INTEGER NOT NULL REFERENCES perfiles(id) ON DELETE CASCADE
);

-- 14. Tabla de relación trabajador-certificación
CREATE TABLE IF NOT EXISTS trabajadores_certificaciones (
  trabajador_id INTEGER NOT NULL REFERENCES trabajadores(id) ON DELETE CASCADE,
  certificacion_id INTEGER NOT NULL REFERENCES certificaciones(id) ON DELETE CASCADE,
  PRIMARY KEY (trabajador_id, certificacion_id)
);

-- 15. Tabla de relación trabajador-servicio
CREATE TABLE IF NOT EXISTS trabajadores_servicios (
  trabajador_id INTEGER NOT NULL REFERENCES trabajadores(id) ON DELETE CASCADE,
  servicio_id INTEGER NOT NULL REFERENCES servicios(id) ON DELETE CASCADE,
  PRIMARY KEY (trabajador_id, servicio_id)
);

-- 16. Tabla de relación trabajador-comuna
CREATE TABLE IF NOT EXISTS trabajadores_comunas (
  trabajador_id INTEGER NOT NULL REFERENCES trabajadores(id) ON DELETE CASCADE,
  comuna_id INTEGER NOT NULL REFERENCES comunas(id) ON DELETE CASCADE,
  PRIMARY KEY (trabajador_id, comuna_id)
);

-- 17. Curriculums
CREATE TABLE IF NOT EXISTS curriculums (
  id SERIAL PRIMARY KEY,
  resumen TEXT NOT NULL,
  experiencia_json JSONB NOT NULL,
  trabajador_id INTEGER NOT NULL REFERENCES trabajadores(id) ON DELETE CASCADE,
  perfil_id INTEGER NOT NULL REFERENCES perfiles(id) ON DELETE CASCADE,
  UNIQUE (perfil_id)
);

-- 18. Tabla de relación comuna-cliente
CREATE TABLE IF NOT EXISTS comunas_clientes (
  comuna_id INTEGER NOT NULL REFERENCES comunas(id) ON DELETE CASCADE,
  cliente_id INTEGER NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
  PRIMARY KEY (comuna_id, cliente_id)
);

-- 19. Tabla de relación cliente-servicio
CREATE TABLE IF NOT EXISTS clientes_servicios (
  cliente_id INTEGER NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
  servicio_id INTEGER NOT NULL REFERENCES servicios(id) ON DELETE CASCADE,
  PRIMARY KEY (cliente_id, servicio_id)
);

-- 20. Publicaciones
CREATE TABLE IF NOT EXISTS publicaciones (
  id SERIAL PRIMARY KEY,
  tipo_publicacion VARCHAR(50) NOT NULL,
  creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  perfil_id INTEGER NOT NULL REFERENCES perfiles(id) ON DELETE CASCADE,
  curriculum_id INTEGER NOT NULL REFERENCES curriculums(id) ON DELETE CASCADE,
  precio_min NUMERIC(10,2) NOT NULL,
  precio_max NUMERIC(10,2) NOT NULL,
  tipo_precio NUMERIC(10,2) NOT NULL,
  moneda VARCHAR(25) NOT NULL,
  duracion_estimada VARCHAR(50) NOT NULL
);

-- Índices adicionales para consultas frecuentes
CREATE INDEX IF NOT EXISTS idx_servicios_nombre ON servicios (nombre);
CREATE INDEX IF NOT EXISTS idx_categorias_servicio_nombre ON categorias_servicio (nombre);
CREATE INDEX IF NOT EXISTS idx_publicaciones_tipo ON publicaciones (tipo_publicacion);

