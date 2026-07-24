/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.NatalyScholz.repository;

/**
 *
 * @author natts
 */

import TechShop.NatalyScholz.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsernameAndActivoTrue(String username);

    List<Usuario> findByActivoTrue();

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByUsernameAndPassword(
            String username,
            String password
    );

    Optional<Usuario> findByUsernameOrCorreo(
            String username,
            String correo
    );

    boolean existsByUsernameOrCorreo(
            String username,
            String correo
    );
}