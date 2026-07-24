/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.NatalyScholz.controller;

/**
 *
 * @author natts
 */

import TechShop.NatalyScholz.domain.Cliente;
import TechShop.NatalyScholz.service.ClienteService;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {

        var clientes = clienteService.getClientes(false);

        model.addAttribute("clientes", clientes);
        model.addAttribute("totalClientes", clientes.size());

        return "cliente/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("cliente", new Cliente());

        return "cliente/modificar";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable("id") Long idCliente,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        Optional<Cliente> cliente = clienteService.getCliente(idCliente);

        if (cliente.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El cliente no existe."
            );

            return "redirect:/cliente/listado";
        }

        model.addAttribute("cliente", cliente.get());

        return "cliente/modificar";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Cliente cliente,
            @RequestParam(value = "imagenFile", required = false)
            MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {

        try {

            clienteService.save(cliente, imagenFile);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    cliente.getIdCliente() == null
                            ? "Cliente agregado correctamente."
                            : "Cliente actualizado correctamente."
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/cliente/listado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long idCliente,
                           RedirectAttributes redirectAttributes) {

        try {

            clienteService.delete(idCliente);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Cliente eliminado correctamente."
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/cliente/listado";
    }
}
