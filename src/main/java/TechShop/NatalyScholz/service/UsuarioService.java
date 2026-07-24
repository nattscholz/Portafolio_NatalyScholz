/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.NatalyScholz.service;

/**
 *
 * @author natts
 */

import TechShop.NatalyScholz.domain.Rol;
import TechShop.NatalyScholz.domain.Usuario;
import TechShop.NatalyScholz.repository.RolRepository;
import TechShop.NatalyScholz.repository.UsuarioRepository;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final FirebaseStorageService firebaseStorageService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          FirebaseStorageService firebaseStorageService,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.firebaseStorageService = firebaseStorageService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean activos) {
        if (activos) {
            return usuarioRepository.findByActivoTrue();
        }

        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existeUsuarioOCorreo(String username, String correo) {
        return usuarioRepository.existsByUsernameOrCorreo(username, correo);
    }

    @Transactional
    public void save(Usuario usuario,
                     MultipartFile imagenFile,
                     String nombreRol) {

        /*
         * Si el usuario es nuevo, se cifra la contraseña.
         * Si se está modificando y la contraseña viene vacía,
         * se conserva la contraseña anterior.
         */
        if (usuario.getIdUsuario() == null) {

            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria.");
            }

            usuario.setPassword(
                    passwordEncoder.encode(usuario.getPassword())
            );

        } else {

            Usuario usuarioActual = usuarioRepository
                    .findById(usuario.getIdUsuario())
                    .orElseThrow(() ->
                            new IllegalArgumentException("El usuario no existe.")
                    );

            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                usuario.setPassword(usuarioActual.getPassword());
            } else if (!usuario.getPassword().startsWith("$2")) {
                usuario.setPassword(
                        passwordEncoder.encode(usuario.getPassword())
                );
            }

            /*
             * Si no se selecciona una imagen nueva,
             * se conserva la ruta anterior.
             */
            if (usuario.getRutaImagen() == null
                    || usuario.getRutaImagen().isBlank()) {
                usuario.setRutaImagen(usuarioActual.getRutaImagen());
            }
        }

        /*
         * Busca el rol seleccionado y lo asigna al usuario.
         */
        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El rol " + nombreRol + " no existe."
                        )
                );

        Set<Rol> roles = new HashSet<>();
        roles.add(rol);
        usuario.setRoles(roles);

        /*
         * Se guarda primero para obtener el ID.
         */
        usuario = usuarioRepository.save(usuario);

        /*
         * Si se seleccionó una imagen, se sube a Firebase.
         */
        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String rutaImagen = firebaseStorageService.uploadImage(
                        imagenFile,
                        "usuario",
                        usuario.getIdUsuario()
                );

                usuario.setRutaImagen(rutaImagen);
                usuarioRepository.save(usuario);

            } catch (IOException e) {
                throw new IllegalStateException(
                        "No se pudo subir la imagen del usuario.",
                        e
                );
            }
        }
    }

    @Transactional
    public void delete(Long idUsuario) {

        if (!usuarioRepository.existsById(idUsuario)) {
            throw new IllegalArgumentException(
                    "El usuario con ID " + idUsuario + " no existe."
            );
        }

        try {
            usuarioRepository.deleteById(idUsuario);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar el usuario porque tiene datos asociados.",
                    e
            );
        }
    }
}
