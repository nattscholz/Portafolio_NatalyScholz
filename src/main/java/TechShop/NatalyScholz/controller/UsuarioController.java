/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.NatalyScholz.controller;

/**
 *
 * @author natts
 */

import TechShop.NatalyScholz.domain.Usuario;
import TechShop.NatalyScholz.repository.RolRepository;
import TechShop.NatalyScholz.service.UsuarioService;
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
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;

    public UsuarioController(UsuarioService usuarioService,
                             RolRepository rolRepository) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
    }

    @GetMapping("/listado")
    public String listado(Model model) {

        var usuarios = usuarioService.getUsuarios(false);

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("usuario", new Usuario());

        return "usuario/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolRepository.findAll());

        return "usuario/modificar";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable("id") Long idUsuario,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOptional =
                usuarioService.getUsuario(idUsuario);

        if (usuarioOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "El usuario solicitado no existe."
            );

            return "redirect:/usuario/listado";
        }

        Usuario usuario = usuarioOptional.get();

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolRepository.findAll());

        return "usuario/modificar";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Usuario usuario,
            @RequestParam(value = "imagenFile", required = false)
            MultipartFile imagenFile,
            @RequestParam("rolSeleccionado")
            String rolSeleccionado,
            RedirectAttributes redirectAttributes) {

        try {
            usuarioService.save(
                    usuario,
                    imagenFile,
                    rolSeleccionado
            );

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    usuario.getIdUsuario() == null
                            ? "Usuario agregado correctamente."
                            : "Usuario actualizado correctamente."
            );

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/usuario/listado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long idUsuario,
                           RedirectAttributes redirectAttributes) {

        try {
            usuarioService.delete(idUsuario);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Usuario eliminado correctamente."
            );

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/usuario/listado";
    }
}