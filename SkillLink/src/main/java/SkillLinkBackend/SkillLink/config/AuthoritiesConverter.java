package SkillLinkBackend.SkillLink.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Convierte claims del JWT en authorities de Spring Security.
 *
 * <pre>
 * scp   = "skilllink.read skilllink.write"  -> SCOPE_skilllink.read, SCOPE_skilllink.write  (Entra ID)
 * scope = "skilllink.read"                  -> SCOPE_skilllink.read                         (OAuth2 estandar)
 * roles = ["ADMIN"]                         -> ROLE_ADMIN                                   (App Roles)
 * </pre>
 */
public class AuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        agregarScopes(jwt.getClaim("scp"), authorities);
        agregarScopes(jwt.getClaim("scope"), authorities);
        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles != null) {
            roles.stream()
                    .filter(r -> r != null && !r.isBlank())
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.trim()))
                    .forEach(authorities::add);
        }
        return authorities;
    }

    private void agregarScopes(Object claim, Set<GrantedAuthority> authorities) {
        if (claim == null) {
            return;
        }
        if (claim instanceof String texto) {
            for (String scope : texto.split(" ")) {
                if (!scope.isBlank()) {
                    authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope.trim()));
                }
            }
        } else if (claim instanceof Collection<?> lista) {
            for (Object scope : lista) {
                if (scope != null && !scope.toString().isBlank()) {
                    authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope.toString().trim()));
                }
            }
        }
    }
}
