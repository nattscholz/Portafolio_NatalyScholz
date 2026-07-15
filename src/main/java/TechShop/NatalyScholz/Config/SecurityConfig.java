/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.NatalyScholz.config;

/**
 *
 * @author natts
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /*
     * Rutas públicas:
     * Cualquier persona puede acceder sin iniciar sesión.
     */
    private static final String[] PUBLIC_URLS = {
        "/",
        "/index",
        "/login",
        "/errores/**",
        "/acceso_denegado",
        "/consultas/**",
        "/css/**",
        "/js/**",
        "/images/**",
        "/img/**",
        "/fav/**",
        "/webjars/**"
    };

    /*
     * Rutas para usuario final.
     * Por ejemplo: carrito y compra.
     */
    private static final String[] USUARIO_URLS = {
        "/carrito/**"
    };

    /*
     * Rutas permitidas para ADMIN o VENDEDOR.
     * VENDEDOR puede consultar listados, pero no debería modificar.
     */
    private static final String[] ADMIN_OR_VENDEDOR_URLS = {
        "/categoria/listado",
        "/producto/listado"
    };

    /*
     * Rutas exclusivas de ADMIN.
     * Aquí van acciones de agregar, guardar, modificar y eliminar.
     */
    private static final String[] ADMIN_URLS = {
        "/categoria/nuevo",
        "/categoria/guardar",
        "/categoria/modificar/**",
        "/categoria/eliminar",
        "/producto/nuevo",
        "/producto/guardar",
        "/producto/modificar/**",
        "/producto/eliminar"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests((requests) -> requests

                // Rutas públicas
                .requestMatchers(PUBLIC_URLS).permitAll()

                // Rutas para usuario final
                .requestMatchers(USUARIO_URLS).hasRole("USUARIO")

                // Rutas para ADMIN o VENDEDOR
                .requestMatchers(ADMIN_OR_VENDEDOR_URLS).hasAnyRole("ADMIN", "VENDEDOR")

                // Rutas solo ADMIN
                .requestMatchers(ADMIN_URLS).hasRole("ADMIN")

                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )

            // Login personalizado
            .formLogin((form) -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/", true)
            )

            // Logout
            .logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // Página de acceso denegado
            .exceptionHandling((exception) -> exception
                .accessDeniedPage("/acceso_denegado")
            )

            // Manejo de sesiones
            .sessionManagement((session) -> session
                .maximumSessions(1)
                .expiredUrl("/login?expired")
            );

        return http.build();
    }

    /*
     * PasswordEncoder:
     * BCrypt se usa para encriptar las contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * Usuarios en memoria para fase de desarrollo.
     * Según la presentación:
     * Juan = admin
     * Rebeca = vendedor
     * Pedro = usuario
     */
    @Bean
    public UserDetailsService users(PasswordEncoder passwordEncoder) {

        UserDetails juan = User.builder()
                .username("juan")
                .password(passwordEncoder.encode("123"))
                .roles("ADMIN")
                .build();

        UserDetails rebeca = User.builder()
                .username("rebeca")
                .password(passwordEncoder.encode("123"))
                .roles("VENDEDOR")
                .build();

        UserDetails pedro = User.builder()
                .username("pedro")
                .password(passwordEncoder.encode("123"))
                .roles("USUARIO")
                .build();

        return new InMemoryUserDetailsManager(juan, rebeca, pedro);
    }
}