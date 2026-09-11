package SkillLinkBackend.SkillLink.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 403 Forbidden en formato RFC 7807: el token es valido pero no posee
 * el scope o rol requerido por la operacion.
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        ProblemJson.escribir(response, 403, "Acceso denegado",
                "El Access Token es valido, pero no posee el permiso (scope o rol) requerido.",
                request.getRequestURI());
    }
}
