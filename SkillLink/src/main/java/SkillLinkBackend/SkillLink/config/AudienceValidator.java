package SkillLinkBackend.SkillLink.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

/**
 * Valida que el Access Token haya sido emitido para esta API (claim {@code aud}).
 * Microsoft Entra ID puede emitir el GUID de la aplicacion o {@code api://GUID};
 * por eso se acepta una lista de audiencias configurables.
 */
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final List<String> audienciasEsperadas;

    public AudienceValidator(List<String> audienciasEsperadas) {
        this.audienciasEsperadas = audienciasEsperadas;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (audienciasEsperadas.isEmpty()) {
            return OAuth2TokenValidatorResult.success();
        }
        List<String> audiencias = jwt.getAudience();
        if (audiencias != null && audiencias.stream().anyMatch(audienciasEsperadas::contains)) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(new OAuth2Error(
                "invalid_token",
                "El token no contiene la audience esperada para esta API",
                null));
    }
}
