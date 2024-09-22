package cr.ac.una.springbootaopmaven.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para simplificar
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/public/**").permitAll() // Permitir rutas públicas
                        .anyRequest().authenticated() // Proteger todas las demás rutas
                )
                .httpBasic(Customizer.withDefaults()); // Habilitar autenticación básica con configuración por defecto

        return http.build();
    }

    // Configuración de usuarios en memoria (solo para pruebas; usa una fuente de datos para producción)
    @Bean
    public UserDetailsService userDetailsService() {
        var user = User.withUsername("user")
                .password("{noop}password") // {noop} indica que no se usará codificación de contraseñas (solo para pruebas)
                .roles("USER")
                .build();

        var admin = User.withUsername("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}