package SkillLinkBackend.SkillLink.config;

import SkillLinkBackend.SkillLink.exceptions.AccionNoPermitidaException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

/** Utilidades para leer la identidad del Access Token validado por Spring Security. */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<Jwt> jwtActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return Optional.empty();
        }
        return Optional.of(jwt);
    }

    /** Email del usuario autenticado segun los claims habituales de Entra ID (email, preferred_username, upn). */
    public static Optional<String> emailTokenActual() {
        return jwtActual().flatMap(jwt -> {
            Object email = jwt.getClaim("email");
            if (email == null) {
                email = jwt.getClaim("preferred_username");
            }
            if (email == null) {
                email = jwt.getClaim("upn");
            }
            return Optional.ofNullable(email).map(Object::toString).map(String::trim).map(String::toLowerCase);
        });
    }

    public static String emailObligatorio() {
        return emailTokenActual()
                .orElseThrow(() -> new AccionNoPermitidaException(
                        "El token no contiene un claim de email/preferred_username para verificar la propiedad."));
    }

    public static boolean tieneAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    public static boolean esAdmin() {
        return tieneAuthority("ROLE_ADMIN");
    }
}
