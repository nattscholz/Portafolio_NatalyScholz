/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.NatalyScholz.config;

/**
 *
 * @author natts
 */

import TechShop.NatalyScholz.service.RutaService;
import java.util.Arrays;
import java.util.stream.Stream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final RutaService rutaService;

    public SecurityConfig(RutaService rutaService) {
        this.rutaService = rutaService;
    }

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

    private static final String[] USUARIO_URLS = {
        "/carrito/**"
    };

    private static final String[] ADMIN_OR_VENDEDOR_URLS = {
        "/categoria/listado",
        "/producto/listado"
    };

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

        String[] publicUrls = unir(PUBLIC_URLS, rutaService.getRutasPublicas());
        String[] adminUrls = unir(ADMIN_URLS, rutaService.getRutasPorRol("ADMIN"));
        String[] vendedorUrls = rutaService.getRutasPorRol("VENDEDOR");
        String[] usuarioUrls = unir(USUARIO_URLS, rutaService.getRutasPorRol("USUARIO"));

        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers(publicUrls).permitAll()
                .requestMatchers(usuarioUrls).hasRole("USUARIO")
                .requestMatchers(ADMIN_OR_VENDEDOR_URLS).hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(vendedorUrls).hasAnyRole("ADMIN", "VENDEDOR")
                .requestMatchers(adminUrls).hasRole("ADMIN")
                .anyRequest().authenticated()
            )

            .formLogin((form) -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/", true)
            )

            .logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            .exceptionHandling((exception) -> exception
                .accessDeniedPage("/acceso_denegado")
            )

            .sessionManagement((session) -> session
                .maximumSessions(1)
                .expiredUrl("/login?expired")
            );

        return http.build();
    }

    private String[] unir(String[] arreglo1, String[] arreglo2) {
        return Stream.concat(Arrays.stream(arreglo1), Arrays.stream(arreglo2))
                .distinct()
                .toArray(String[]::new);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}