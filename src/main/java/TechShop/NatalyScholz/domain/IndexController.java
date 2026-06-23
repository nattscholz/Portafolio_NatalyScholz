/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.NatalyScholz.domain;

import TechShop.NatalyScholz.domain.Categoria;
import TechShop.NatalyScholz.service.CategoriaService;
import TechShop.NatalyScholz.service.ProductoService;
import java.util.Collections;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author natts
 */
@Controller
public class IndexController {
    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public IndexController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/")
    public String cargarPaginaInicio(Model model) {

        var productos = productoService.getProductos(true);
        model.addAttribute("productos", productos);

        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);

        return "index";
    }

    @GetMapping("/consultas/{idCategoria}")
    public String listado(@PathVariable("idCategoria") Long idCategoria, Model model) {

        model.addAttribute("idCategoriaActual", idCategoria);

        var categoriaOptional = categoriaService.getCategoria(idCategoria);

        if (categoriaOptional.isEmpty()) {
            model.addAttribute("productos", Collections.emptyList());
        } else {
            Categoria categoria = categoriaOptional.get();
            var productos = categoria.getProductos();
            model.addAttribute("productos", productos);
        }

        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);

        return "index";
    }
}
