/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.NatalyScholz.service;

/**
 *
 * @author natts
 */

import TechShop.NatalyScholz.domain.Ruta;
import TechShop.NatalyScholz.repository.RutaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RutaService {

    private final RutaRepository rutaRepository;

    public RutaService(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }

    @Transactional(readOnly = true)
    public String[] getRutasPublicas() {
        List<Ruta> rutas = rutaRepository.findAllByRequiereRolFalse();

        return rutas.stream()
                .map(Ruta::getRuta)
                .toArray(String[]::new);
    }

    @Transactional(readOnly = true)
    public String[] getRutasPorRol(String nombreRol) {
        List<Ruta> rutas = rutaRepository.findAllByRolNombre(nombreRol);

        return rutas.stream()
                .map(Ruta::getRuta)
                .toArray(String[]::new);
    }
}