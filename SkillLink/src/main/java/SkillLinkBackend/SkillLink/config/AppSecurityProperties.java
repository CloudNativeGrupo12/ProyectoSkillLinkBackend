package SkillLinkBackend.SkillLink.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Valores de seguridad externalizados (nunca versionar secretos).
 *
 * <pre>
 * app.security.issuer        -> claim iss esperado (IDaaS)
 * app.security.audience      -> claim aud esperado (GUID de la API y/o api://GUID), separado por comas
 * app.security.jwk-set-uri   -> opcional; si se define se usa en vez del discovery del issuer
 * app.security.write-scope   -> scope delegado que habilita operaciones de escritura
 * app.security.admin-role    -> app role que habilita operaciones administrativas
 * </pre>
 */
@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    private String issuer = "";
    private String audience = "";
    private String jwkSetUri = "";
    private String readScope = "skilllink.read";
    private String writeScope = "skilllink.write";
    private String adminRole = "ADMIN";

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getJwkSetUri() {
        return jwkSetUri;
    }

    public void setJwkSetUri(String jwkSetUri) {
        this.jwkSetUri = jwkSetUri;
    }

    public String getReadScope() {
        return readScope;
    }

    public void setReadScope(String readScope) {
        this.readScope = readScope;
    }

    public String getWriteScope() {
        return writeScope;
    }

    public void setWriteScope(String writeScope) {
        this.writeScope = writeScope;
    }

    public String getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(String adminRole) {
        this.adminRole = adminRole;
    }

    public List<String> audiencias() {
        List<String> valores = new ArrayList<>();
        if (audience != null) {
            Arrays.stream(audience.split(","))
                    .map(String::trim)
                    .filter(v -> !v.isBlank())
                    .forEach(valores::add);
        }
        return valores;
    }

    public String writeAuthority() {
        return "SCOPE_" + writeScope;
    }

    public String adminAuthority() {
        return "ROLE_" + adminRole;
    }
}
