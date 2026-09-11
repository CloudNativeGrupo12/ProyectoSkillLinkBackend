package SkillLinkBackend.SkillLink.controller;

import SkillLinkBackend.SkillLink.config.AppSecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoints de diagnostico para demostrar la matriz 200 / 401 / 403:
 *
 * <pre>
 * GET  /api/v1/public/health    -> 200 sin token
 * GET  /api/v1/public/info      -> 200 sin token (configuracion publica del Resource Server)
 * GET  /api/v1/auth/me          -> 401 sin token / 200 con token valido (claims relevantes)
 * POST /api/v1/auth/write-check -> 403 sin scope de escritura / 200 con scope
 * GET  /api/v1/admin/ping       -> 403 sin rol ADMIN / 200 con rol
 * </pre>
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SeguridadController {

    private final AppSecurityProperties props;

    @GetMapping("/public/health")
    public Map<String, Object> health() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "ok");
        body.put("access", "public");
        body.put("app", "SkillLink API");
        body.put("timestamp", Instant.now().toString());
        return body;
    }

    /** Configuracion publica (sin secretos) que la SPA puede usar para explicar la validacion del token. */
    @GetMapping("/public/info")
    public Map<String, Object> info() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("issuer", props.getIssuer());
        body.put("audience", props.audiencias());
        body.put("writeScope", props.getWriteScope());
        body.put("readScope", props.getReadScope());
        body.put("adminRole", props.getAdminRole());
        body.put("writeAuthority", props.writeAuthority());
        body.put("adminAuthority", props.adminAuthority());
        return body;
    }

    @GetMapping("/auth/me")
    public Map<String, Object> me(Authentication authentication) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("authenticated", authentication.isAuthenticated());
        body.put("name", authentication.getName());
        body.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList());
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            var jwt = jwtAuth.getToken();
            body.put("subject", jwt.getSubject());
            body.put("issuer", jwt.getIssuer() != null ? jwt.getIssuer().toString() : null);
            body.put("audience", jwt.getAudience());
            body.put("email", primerClaim(jwt.getClaims(), "email", "preferred_username", "upn"));
            body.put("displayName", jwt.getClaim("name"));
            body.put("scp", jwt.getClaim("scp"));
            body.put("roles", jwt.getClaim("roles"));
            body.put("issuedAt", jwt.getIssuedAt());
            body.put("expiresAt", jwt.getExpiresAt());
        }
        return body;
    }

    @PostMapping("/auth/write-check")
    public Map<String, Object> writeCheck(Authentication authentication) {
        return Map.of(
                "status", "ok",
                "requiredAuthority", props.writeAuthority(),
                "user", authentication.getName());
    }

    @GetMapping("/admin/ping")
    public Map<String, Object> adminPing(Authentication authentication) {
        return Map.of(
                "status", "ok",
                "requiredAuthority", props.adminAuthority(),
                "user", authentication.getName());
    }

    private static Object primerClaim(Map<String, Object> claims, String... nombres) {
        for (String nombre : List.of(nombres)) {
            Object valor = claims.get(nombre);
            if (valor != null) {
                return valor;
            }
        }
        return null;
    }
}
