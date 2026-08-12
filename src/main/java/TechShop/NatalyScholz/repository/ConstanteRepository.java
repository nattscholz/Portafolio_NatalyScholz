/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.NatalyScholz.repository;

/**
 *
 * @author natts
 */

import TechShop.NatalyScholz.domain.Constante;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConstanteRepository extends JpaRepository<Constante, Long> {

    List<Constante> findByActivoTrue();

    Optional<Constante> findByAtributo(String atributo);
}
