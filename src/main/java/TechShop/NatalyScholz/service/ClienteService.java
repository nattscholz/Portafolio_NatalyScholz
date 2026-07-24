/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.NatalyScholz.service;

/**
 *
 * @author natts
 */

import TechShop.NatalyScholz.domain.Cliente;
import TechShop.NatalyScholz.repository.ClienteRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final FirebaseStorageService firebaseStorageService;

    public ClienteService(ClienteRepository clienteRepository,
                          FirebaseStorageService firebaseStorageService) {
        this.clienteRepository = clienteRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Transactional(readOnly = true)
    public List<Cliente> getClientes(boolean activos) {

        if (activos) {
            return clienteRepository.findByActivoTrue();
        }

        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> getCliente(Long idCliente) {
        return clienteRepository.findById(idCliente);
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> getClientePorCorreo(String correo) {
        return clienteRepository.findByCorreo(correo);
    }

    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo) {
        return clienteRepository.existsByCorreo(correo);
    }

    @Transactional
    public void save(Cliente cliente,
                     MultipartFile imagenFile) {

        if (cliente.getIdCliente() != null) {

            Cliente clienteActual = clienteRepository
                    .findById(cliente.getIdCliente())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "El cliente no existe."
                            )
                    );

            if (cliente.getRutaImagen() == null
                    || cliente.getRutaImagen().isBlank()) {

                cliente.setRutaImagen(
                        clienteActual.getRutaImagen()
                );
            }
        }

        cliente = clienteRepository.save(cliente);

        if (imagenFile != null && !imagenFile.isEmpty()) {

            try {

                String rutaImagen =
                        firebaseStorageService.uploadImage(
                                imagenFile,
                                "cliente",
                                cliente.getIdCliente()
                        );

                cliente.setRutaImagen(rutaImagen);

                clienteRepository.save(cliente);

            } catch (IOException e) {

                throw new IllegalStateException(
                        "No se pudo subir la imagen del cliente.",
                        e
                );
            }
        }
    }

    @Transactional
    public void delete(Long idCliente) {

        if (!clienteRepository.existsById(idCliente)) {

            throw new IllegalArgumentException(
                    "El cliente con ID "
                            + idCliente
                            + " no existe."
            );
        }

        try {

            clienteRepository.deleteById(idCliente);

        } catch (DataIntegrityViolationException e) {

            throw new IllegalStateException(
                    "No se puede eliminar el cliente porque tiene datos asociados.",
                    e
            );
        }
    }
}
