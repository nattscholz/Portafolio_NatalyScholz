/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.NatalyScholz.service;

/**
 *
 * @author natts
 */

import TechShop.NatalyScholz.domain.Constante;
import TechShop.NatalyScholz.repository.ConstanteRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConstanteService {

    private final ConstanteRepository constanteRepository;

    public ConstanteService(ConstanteRepository constanteRepository) {
        this.constanteRepository = constanteRepository;
    }

    @Transactional(readOnly = true)
    public List<Constante> getConstantes(boolean activos) {
        if (activos) {
            return constanteRepository.findByActivoTrue();
        }

        return constanteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Constante> getConstante(Long idConstante) {
        return constanteRepository.findById(idConstante);
    }

    @Transactional(readOnly = true)
    public Optional<Constante> getConstantePorAtributo(String atributo) {
        return constanteRepository.findByAtributo(atributo);
    }

    @Transactional
    public void save(Constante constante) {
        constanteRepository.save(constante);
    }

    @Transactional
    public void delete(Long idConstante) {

        if (!constanteRepository.existsById(idConstante)) {
            throw new IllegalArgumentException(
                    "La constante con ID " + idConstante + " no existe."
            );
        }

        try {
            constanteRepository.deleteById(idConstante);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar la constante porque tiene datos asociados.",
                    e
            );
        }
    }
}