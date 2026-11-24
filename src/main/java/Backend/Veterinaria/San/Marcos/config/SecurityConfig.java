
package Backend.Veterinaria.San.Marcos.config;

import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }

    // Convertir el rol del JWT en un permiso de Spring Security
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            String rol = jwt.getClaimAsString("rol");

            Set<String> rolesValidos = Set.of(
                "ADMINISTRADOR",
                "VENDEDOR",
                "CLIENTE"
            );

            if (rol == null || !rolesValidos.contains(rol)) {
                return List.of();
            }

            return List.of(
                new SimpleGrantedAuthority("ROLE_" + rol)
            );
        });

        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter)
            throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            .authorizeHttpRequests(auth -> auth

                // Registro y login publicos
                .requestMatchers("/api/v1/auth/**")
                .permitAll()

                // Documentacion Swagger
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                )
                .permitAll()

                // Todos los roles autenticados pueden consultar productos
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/v1/productos",
                    "/api/v1/productos/**"
                )
                .hasAnyRole("ADMINISTRADOR", "VENDEDOR", "CLIENTE")

                // Solo el administrador modifica productos
                .requestMatchers(
                    "/api/v1/productos",
                    "/api/v1/productos/**"
                )
                .hasRole("ADMINISTRADOR")

                // Administrador y vendedor consultan compras
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/v1/compras",
                    "/api/v1/compras/**"
                )
                .hasAnyRole("ADMINISTRADOR", "VENDEDOR")

                // Administrador y cliente pueden comprar
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/v1/compras"
                )
                .hasAnyRole("ADMINISTRADOR", "CLIENTE")

                // Clientes pueden solicitar citas
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/v1/citas"
                )
                .hasAnyRole("ADMINISTRADOR", "CLIENTE")

                // Por ahora, solo administracion consulta
                // y modifica el conjunto completo de citas
                .requestMatchers(
                    "/api/v1/citas",
                    "/api/v1/citas/**"
                )
                .hasRole("ADMINISTRADOR")

                // Bloquear cualquier otra ruta no configurada
                .anyRequest()
                .denyAll()
            )

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(
                        jwtAuthenticationConverter
                    )
                )
            );

        return http.build();
    }
}
