/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.NatalyScholz.service;

/**
 *
 * @author natts
 */

import TechShop.NatalyScholz.domain.EstadoFactura;
import TechShop.NatalyScholz.domain.Factura;
import TechShop.NatalyScholz.domain.Item;
import TechShop.NatalyScholz.domain.Usuario;
import TechShop.NatalyScholz.domain.Venta;
import TechShop.NatalyScholz.repository.FacturaRepository;
import TechShop.NatalyScholz.repository.VentaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final VentaRepository ventaRepository;
    private final CarritoService carritoService;

    public FacturaService(FacturaRepository facturaRepository,
                          VentaRepository ventaRepository,
                          CarritoService carritoService) {
        this.facturaRepository = facturaRepository;
        this.ventaRepository = ventaRepository;
        this.carritoService = carritoService;
    }

    @Transactional
    public Factura facturar(Usuario usuario) {

        if (carritoService.estaVacio()) {
            throw new IllegalStateException("El carrito está vacío.");
        }

        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setFecha(LocalDateTime.now());
        factura.setTotal(carritoService.getTotal());
        factura.setEstado(EstadoFactura.Pagada);

        factura = facturaRepository.save(factura);

        for (Item item : carritoService.getItems()) {
            Venta venta = new Venta();
            venta.setFactura(factura);
            venta.setProducto(item.getProducto());
            venta.setCantidad(item.getCantidad());
            venta.setPrecioHistorico(item.getPrecioHistorico());

            ventaRepository.save(venta);
        }

        carritoService.limpiar();

        return factura;
    }

    @Transactional(readOnly = true)
    public Optional<Factura> getFactura(Integer idFactura) {
        return facturaRepository.findByIdFacturaConDetalle(idFactura);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalCarrito() {
        return carritoService.getTotal();
    }
}