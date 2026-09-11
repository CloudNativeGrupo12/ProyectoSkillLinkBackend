package SkillLinkBackend.SkillLink;

import SkillLinkBackend.SkillLink.config.AuthoritiesConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.function.Consumer;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Matriz de evidencia EV1: 200 publico / 401 sin token / 401 token invalido /
 * 200 token valido / 403 sin scope / 2xx con scope / 403 sin rol / 200 con rol.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SeguridadMatrizTests {

    @Autowired
    private MockMvc mockMvc;

    /** Se reemplaza el decodificador real: los tests no dependen de la red ni del tenant. */
    @MockitoBean
    private JwtDecoder jwtDecoder;

    /** Token simulado que pasa por el mismo AuthoritiesConverter que usa la aplicacion (scp/roles). */
    private static JwtRequestPostProcessor token(Consumer<Jwt.Builder> claims) {
        return jwt().authorities(new AuthoritiesConverter()).jwt(claims);
    }

    @Test
    @DisplayName("Recurso publico sin token -> 200")
    void publicoSinToken() throws Exception {
        mockMvc.perform(get("/api/v1/public/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
        mockMvc.perform(get("/api/v1/catalogo/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Fitness"));
        mockMvc.perform(get("/api/v1/catalogo/publicaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trabajadorNombre").isNotEmpty());
    }

    @Test
    @DisplayName("Recurso protegido sin token -> 401 (problem+json)")
    void protegidoSinToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists("WWW-Authenticate"))
                .andExpect(jsonPath("$.status").value(401));
        mockMvc.perform(get("/api/v1/usuarios/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Token invalido / no verificable -> 401")
    void tokenInvalido() throws Exception {
        when(jwtDecoder.decode(anyString())).thenThrow(new BadJwtException("firma invalida"));
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Token valido -> 200 y onboarding de la persona")
    void tokenValido() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me").with(token(j -> j
                        .subject("sub-1").claim("preferred_username", "nuevo.usuario@duocuc.cl")
                        .claim("scp", "skilllink.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.authorities", hasItem("SCOPE_skilllink.read")));

        mockMvc.perform(get("/api/v1/usuarios/me").with(token(j -> j
                        .subject("sub-1").claim("preferred_username", "nuevo.usuario@duocuc.cl")
                        .claim("given_name", "Nuevo").claim("family_name", "Usuario")
                        .claim("scp", "skilllink.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("nuevo.usuario@duocuc.cl"))
                .andExpect(jsonPath("$.nombre").value("Nuevo"))
                .andExpect(jsonPath("$.perfilId").isNumber());
    }

    @Test
    @DisplayName("Token valido sin scope de escritura -> 403; con scope -> 2xx")
    void scopeEscritura() throws Exception {
        mockMvc.perform(post("/api/v1/auth/write-check").with(token(j -> j.claim("scp", "skilllink.read"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        mockMvc.perform(post("/api/v1/auth/write-check").with(token(j -> j.claim("scp", "skilllink.read skilllink.write"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requiredAuthority").value("SCOPE_skilllink.write"));

        String cuerpo = """
                {"titulo":"Clases de guitarra","descripcion":"Clases para principiantes a domicilio.",
                 "categoriaId":9,"precio":15000,"nombreAutor":"Ana Prueba"}
                """;
        mockMvc.perform(post("/api/v1/catalogo/publicaciones").contentType(MediaType.APPLICATION_JSON).content(cuerpo)
                        .with(token(j -> j.claim("preferred_username", "ana.prueba@duocuc.cl").claim("scp", "skilllink.read"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/catalogo/publicaciones").contentType(MediaType.APPLICATION_JSON).content(cuerpo)
                        .with(token(j -> j.claim("preferred_username", "ana.prueba@duocuc.cl").claim("scp", "skilllink.write"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Clases de guitarra"))
                .andExpect(jsonPath("$.trabajadorNombre").value("Ana Prueba"));
    }

    @Test
    @DisplayName("Rol ADMIN: 403 sin rol, 200 con rol")
    void rolAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/ping").with(token(j -> j.claim("scp", "skilllink.write"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/admin/ping").with(token(j -> j.claim("roles", java.util.List.of("ADMIN")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requiredAuthority").value("ROLE_ADMIN"));

        // Escritura sobre catalogos maestros: solo ADMIN
        mockMvc.perform(post("/api/v1/categorias-servicio").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Jardineria\"}")
                        .with(token(j -> j.claim("scp", "skilllink.write"))))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/v1/categorias-servicio").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Jardineria\"}")
                        .with(token(j -> j.claim("roles", java.util.List.of("ADMIN")))))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Propiedad del recurso: otro usuario con scope no puede borrar una publicacion ajena")
    void propiedadDelRecurso() throws Exception {
        mockMvc.perform(delete("/api/v1/catalogo/publicaciones/1")
                        .with(token(j -> j.claim("preferred_username", "otro@duocuc.cl").claim("scp", "skilllink.write"))))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/v1/catalogo/publicaciones/1")
                        .with(token(j -> j.claim("preferred_username", "carlos.mendoza@skilllink.cl").claim("scp", "skilllink.write"))))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("CORS controlado: origen permitido recibe cabeceras; origen desconocido no")
    void cors() throws Exception {
        mockMvc.perform(get("/api/v1/public/health").header("Origin", "http://localhost:4200"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
        mockMvc.perform(get("/api/v1/public/health").header("Origin", "http://malicioso.example"))
                .andExpect(status().isForbidden());
    }
}
