/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.NatalyScholz.service;

/**
 *
 * @author natts
 */
import TechShop.NatalyScholz.domain.Item;
import TechShop.NatalyScholz.domain.Producto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

@Service
@SessionScope
public class CarritoService {

    private List<Item> items = new ArrayList<>();

    public List<Item> getItems() {
        return items;
    }

    public void agregar(Producto producto) {
        for (Item item : items) {
            if (item.getProducto().getIdProducto().equals(producto.getIdProducto())) {
                item.setCantidad(item.getCantidad() + 1);
                return;
            }
        }

        items.add(new Item(producto, 1));
    }

    public void eliminar(Long idProducto) {
        items.removeIf(item -> item.getProducto().getIdProducto().equals(idProducto));
    }

    public void actualizarCantidad(Long idProducto, int cantidad) {
        for (Item item : items) {
            if (item.getProducto().getIdProducto().equals(idProducto)) {
                if (cantidad <= 0) {
                    eliminar(idProducto);
                } else {
                    item.setCantidad(cantidad);
                }
                return;
            }
        }
    }

    public void limpiar() {
        items.clear();
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;

        for (Item item : items) {
            total = total.add(item.getSubTotal());
        }

        return total;
    }

    public int getCantidadItems() {
        int cantidad = 0;

        for (Item item : items) {
            cantidad += item.getCantidad();
        }

        return cantidad;
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }
}
