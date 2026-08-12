/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.NatalyScholz.domain;

/**
 *
 * @author natts
 */

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "constante")
public class Constante implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_constante")
    private Long idConstante;

    @Column(name = "atributo", nullable = false, length = 50)
    private String atributo;

    @Column(name = "valor", nullable = false, length = 1024)
    private String valor;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "activo")
    private boolean activo;

    public Constante() {
    }

    public Constante(Long idConstante, String atributo, String valor, String descripcion, boolean activo) {
        this.idConstante = idConstante;
        this.atributo = atributo;
        this.valor = valor;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public Long getIdConstante() {
        return idConstante;
    }

    public void setIdConstante(Long idConstante) {
        this.idConstante = idConstante;
    }

    public String getAtributo() {
        return atributo;
    }

    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
