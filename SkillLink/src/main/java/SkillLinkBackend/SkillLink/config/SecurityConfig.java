package SkillLinkBackend.SkillLink.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SkillLink como OAuth 2.0 Resource Server.
 *
 * <p>La API no autentica credenciales ni emite tokens: valida los Access Tokens JWT
 * emitidos por el IDaaS (Microsoft Entra ID) y aplica autorizacion por ruta.</p>
 *
 * <pre>
 * Publico (permitAll)   : /api/v1/public/**, GET de catalogos y busquedas
 * Autenticado           : cualquier otra ruta /api/**
 * Escritura de dominio  : POST/PUT/DELETE de publicaciones, perfiles, personas... -> SCOPE_{write-scope}
 * Administracion        : /api/v1/admin/** y escritura de catalogos maestros   -> ROLE_{admin-role}
 * </pre>
 */
@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(AppSecurityProperties.class)
public class SecurityConfig {

    private static final String[] CATALOGOS_PUBLICOS = {
            "/api/v1/catalogo/**",
            "/api/v1/publicaciones/**",
            "/api/v1/servicios/**",
            "/api/v1/categorias-servicio/**",
            "/api/v1/certificaciones/**",
            "/api/v1/paises/**",
            "/api/v1/regiones/**",
            "/api/v1/ciudades/**",
            "/api/v1/comunas/**",
            "/api/v1/membresias/**"
    };

    private static final String[] CATALOGOS_MAESTROS = {
            "/api/v1/servicios/**",
            "/api/v1/categorias-servicio/**",
            "/api/v1/membresias/**",
            "/api/v1/paises/**",
            "/api/v1/regiones/**",
            "/api/v1/ciudades/**",
            "/api/v1/comunas/**"
    };

    private static final String[] RECURSOS_DE_USUARIO = {
            "/api/v1/catalogo/**",
            "/api/v1/publicaciones/**",
            "/api/v1/perfiles/**",
            "/api/v1/personas/**",
            "/api/v1/clientes/**",
            "/api/v1/trabajadores/**",
            "/api/v1/curriculums/**",
            "/api/v1/certificaciones/**",
            "/api/v1/auth/write-check"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AppSecurityProperties props,
                                                   JwtAuthenticationConverter jwtAuthenticationConverter,
                                                   RestAuthenticationEntryPoint entryPoint,
                                                   RestAccessDeniedHandler accessDeniedHandler) throws Exception {
        String write = props.writeAuthority();
        String admin = props.adminAuthority();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Preflight CORS y recursos publicos
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/v1/public/**").permitAll()
                        // Datos de contacto de un perfil: lectura protegida (usuario autenticado)
                        .requestMatchers(HttpMethod.GET, "/api/v1/catalogo/perfiles/**", "/api/v1/perfiles/**").authenticated()
                        .requestMatchers(HttpMethod.GET, CATALOGOS_PUBLICOS).permitAll()
                        // Administracion: app role ADMIN
                        .requestMatchers("/api/v1/admin/**").hasAuthority(admin)
                        .requestMatchers(HttpMethod.POST, CATALOGOS_MAESTROS).hasAuthority(admin)
                        .requestMatchers(HttpMethod.PUT, CATALOGOS_MAESTROS).hasAuthority(admin)
                        .requestMatchers(HttpMethod.PATCH, CATALOGOS_MAESTROS).hasAuthority(admin)
                        .requestMatchers(HttpMethod.DELETE, CATALOGOS_MAESTROS).hasAuthority(admin)
                        // Escritura de recursos de usuario: scope delegado de escritura (o ADMIN)
                        .requestMatchers(HttpMethod.POST, RECURSOS_DE_USUARIO).hasAnyAuthority(write, admin)
                        .requestMatchers(HttpMethod.PUT, RECURSOS_DE_USUARIO).hasAnyAuthority(write, admin)
                        .requestMatchers(HttpMethod.PATCH, RECURSOS_DE_USUARIO).hasAnyAuthority(write, admin)
                        .requestMatchers(HttpMethod.DELETE, RECURSOS_DE_USUARIO).hasAnyAuthority(write, admin)
                        // Todo lo demas de la API requiere autenticacion
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().denyAll())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler));
        return http.build();
    }

    /**
     * Decodificador JWT: firma (JWKS del IDaaS) + issuer + audience + vigencia.
     * La resolucion de claves es perezosa: la API arranca aunque el IDaaS no este configurado
     * y los endpoints publicos siguen respondiendo.
     */
    @Bean
    public JwtDecoder jwtDecoder(AppSecurityProperties props) {
        String issuer = props.getIssuer();
        String jwkSetUri = props.getJwkSetUri();
        boolean tieneIssuer = issuer != null && !issuer.isBlank();
        boolean tieneJwks = jwkSetUri != null && !jwkSetUri.isBlank();

        if (!tieneIssuer && !tieneJwks) {
            throw new IllegalStateException(
                    "Configure app.security.issuer (JWT_ISSUER) o app.security.jwk-set-uri (JWT_JWK_SET_URI).");
        }

        NimbusJwtDecoder decoder = tieneJwks
                ? NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build()
                : NimbusJwtDecoder.withIssuerLocation(issuer).build();

        OAuth2TokenValidator<Jwt> validadorBase = tieneIssuer
                ? JwtValidators.createDefaultWithIssuer(issuer)
                : JwtValidators.createDefault();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                validadorBase, new AudienceValidator(props.audiencias())));
        return decoder;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new AuthoritiesConverter());
        converter.setPrincipalClaimName("sub");
        return converter;
    }
}
