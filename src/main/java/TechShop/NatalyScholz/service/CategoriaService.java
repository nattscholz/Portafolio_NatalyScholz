/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package TechShop.NatalyScholz.service;

import TechShop.NatalyScholz.domain.Categoria;
import TechShop.NatalyScholz.repository.CategoriaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> getCategorias(boolean activo) {
        var lista = categoriaRepository.findAll();

        if (activo) {
            lista.removeIf(categoria -> !categoria.isActivo());
        }

        return lista;
    }
}