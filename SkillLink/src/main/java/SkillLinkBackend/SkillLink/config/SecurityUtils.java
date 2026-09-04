package SkillLinkBackend.SkillLink.config;

import SkillLinkBackend.SkillLink.exceptions.AccionNoPermitidaException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<String> emailTokenActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return Optional.empty();
        }
        Object email = jwt.getClaim("email");
        if (email == null) {
            email = jwt.getClaim("preferred_username");
        }
        return Optional.ofNullable(email).map(Object::toString).map(String::toLowerCase);
    }

    public static String emailObligatorio() {
        return emailTokenActual()
                .orElseThrow(() -> new AccionNoPermitidaException(
                        "El token no contiene un claim de email/preferred_username para verificar la propiedad."));
    }
}