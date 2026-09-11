package SkillLinkBackend.SkillLink.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 401 Unauthorized en formato RFC 7807: la API no pudo autenticar la solicitud
 * (sin token, token corrupto, firma invalida, issuer/audience incorrectos o expirado).
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String detalle = authException instanceof InvalidBearerTokenException
                ? "El Access Token no es valido para esta API (firma, issuer, audience o expiracion)."
                : "La solicitud requiere un Access Token valido (Authorization: Bearer <token>).";
        response.setHeader("WWW-Authenticate", "Bearer realm=\"skilllink\"");
        ProblemJson.escribir(response, 401, "No autenticado", detalle, request.getRequestURI());
    }
}
