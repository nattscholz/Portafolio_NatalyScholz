/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.NatalyScholz.controller;

/**
 *
 * @author natts
 */

import TechShop.NatalyScholz.domain.Factura;
import TechShop.NatalyScholz.domain.Producto;
import TechShop.NatalyScholz.domain.Usuario;
import TechShop.NatalyScholz.service.CarritoService;
import TechShop.NatalyScholz.service.FacturaService;
import TechShop.NatalyScholz.service.ProductoService;
import TechShop.NatalyScholz.service.UsuarioService;
import java.security.Principal;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final ProductoService productoService;
    private final FacturaService facturaService;
    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public CarritoController(CarritoService carritoService,
                             ProductoService productoService,
                             FacturaService facturaService,
                             UsuarioService usuarioService,
                             MessageSource messageSource) {
        this.carritoService = carritoService;
        this.productoService = productoService;
        this.facturaService = facturaService;
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("items", carritoService.getItems());
        model.addAttribute("total", carritoService.getTotal());
        model.addAttribute("cantidadItems", carritoService.getCantidadItems());

        return "carrito/listado";
    }

    @PostMapping("/agregar")
    public String agregar(@RequestParam Long idProducto,
                          RedirectAttributes redirectAttributes) {

        Optional<Producto> productoOpt = productoService.getProducto(idProducto);

        if (productoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("producto.error01", null, Locale.getDefault())
            );

            return "redirect:/producto/listado";
        }

        carritoService.agregar(productoOpt.get());

        redirectAttributes.addFlashAttribute(
                "todoOk",
                messageSource.getMessage("carrito.agregado", null, Locale.getDefault())
        );

        return "redirect:/carrito/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Long idProducto,
                           RedirectAttributes redirectAttributes) {

        carritoService.eliminar(idProducto);

        redirectAttributes.addFlashAttribute(
                "todoOk",
                messageSource.getMessage("carrito.eliminado", null, Locale.getDefault())
        );

        return "redirect:/carrito/listado";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Long idProducto,
                             @RequestParam int cantidad,
                             RedirectAttributes redirectAttributes) {

        carritoService.actualizarCantidad(idProducto, cantidad);

        redirectAttributes.addFlashAttribute(
                "todoOk",
                messageSource.getMessage("carrito.actualizado", null, Locale.getDefault())
        );

        return "redirect:/carrito/listado";
    }

    @PostMapping("/limpiar")
    public String limpiar(RedirectAttributes redirectAttributes) {

        carritoService.limpiar();

        redirectAttributes.addFlashAttribute(
                "todoOk",
                messageSource.getMessage("carrito.limpiado", null, Locale.getDefault())
        );

        return "redirect:/carrito/listado";
    }

    @PostMapping("/facturar")
    public String facturar(Principal principal,
                           RedirectAttributes redirectAttributes) {

        if (carritoService.estaVacio()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("carrito.vacio", null, Locale.getDefault())
            );

            return "redirect:/carrito/listado";
        }

        try {
            String username = principal.getName();

            Usuario usuario = usuarioService.getUsuarioPorUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

            Factura factura = facturaService.facturar(usuario);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    messageSource.getMessage("carrito.facturado", null, Locale.getDefault())
            );

            return "redirect:/carrito/factura?idFactura=" + factura.getIdFactura();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("carrito.errorFacturar", null, Locale.getDefault())
            );

            return "redirect:/carrito/listado";
        }
    }

    @GetMapping("/factura")
    public String factura(@RequestParam Integer idFactura,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        Optional<Factura> facturaOpt = facturaService.getFactura(idFactura);

        if (facturaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("carrito.facturaNoEncontrada", null, Locale.getDefault())
            );

            return "redirect:/carrito/listado";
        }

        model.addAttribute("factura", facturaOpt.get());

        return "carrito/factura";
    }
}