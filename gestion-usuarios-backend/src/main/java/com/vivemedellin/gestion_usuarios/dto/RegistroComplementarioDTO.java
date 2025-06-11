/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivemedellin.gestion_usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 *
 * @author David
 */
@AllArgsConstructor
@NoArgsConstructor
public class RegistroComplementarioDTO {
    
    @NotBlank
    private String apodo;

    @NotBlank
    private String nombre;

    private String segundoNombre;

    @NotBlank
    private String apellido;

    private String segundoApellido;

    private String fotoPerfil;

    private String biografia;

    private String telefono;

    @NotNull
    private LocalDate fechaNacimiento;

    @NotNull
    private Integer idMunicipio;

    @NotNull
    private List<Integer> idsIntereses;

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(Integer idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public List<Integer> getIdsIntereses() {
        return idsIntereses;
    }

    public void setIdsIntereses(List<Integer> idsIntereses) {
        this.idsIntereses = idsIntereses;
    }

    @Override
    public String toString() {
        return "RegistroComplementarioDTO{" +
                "apodo='" + apodo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", segundoNombre='" + segundoNombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", segundoApellido='" + segundoApellido + '\'' +
                ", fotoPerfil='" + fotoPerfil + '\'' +
                ", biografia='" + biografia + '\'' +
                ", telefono='" + telefono + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", idMunicipio=" + idMunicipio +
                ", idsIntereses=" + idsIntereses +
                '}';
    }
}
