package SkillLinkBackend.SkillLink.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/** Escribe un cuerpo application/problem+json sin depender del mapeador JSON del contexto. */
final class ProblemJson {

    private ProblemJson() {
    }

    static void escribir(HttpServletResponse response, int status, String titulo, String detalle,
                         String instancia) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String cuerpo = "{"
                + "\"type\":\"about:blank\","
                + "\"title\":\"" + escapar(titulo) + "\","
                + "\"status\":" + status + ","
                + "\"detail\":\"" + escapar(detalle) + "\","
                + "\"instance\":\"" + escapar(instancia) + "\","
                + "\"timestamp\":\"" + Instant.now() + "\""
                + "}";
        response.getWriter().write(cuerpo);
        response.getWriter().flush();
    }

    private static String escapar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ");
    }
}
