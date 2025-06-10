/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivemedellin.gestion_usuarios.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.time.LocalDate;
import java.util.List;


import lombok.AllArgsConstructor;


@Entity
@Table(name = "tblusuarios")
@AllArgsConstructor
public class Usuario {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="id_usuario", nullable=false)
    private Integer id_usuario;

    @NotBlank
    @Column(name = "apodo", nullable = false)
    private String apodo;


    @NotBlank
    @Column(name = "nombre", nullable = false)
    private String nombre;


    @Column(name = "segundo_nombre")
    private String segundoNombre;


    @NotBlank
    @Column(name = "apellido", nullable = false)
    private String apellido;



    @Column(name = "segundo_apellido")
    private String segundoApellido;


    @Email
    @NotBlank
    @Column(name = "correo_electronico", unique = true, nullable = false)
    private String correoElectronico;


    @NotBlank
    @Column(name = "contrasena", nullable = false)
    private String contraseña; // Aquí se almacenará cifrada (bcrypt u otra)



    @Column(name = "foto_perfil")
    private String fotoPerfil; // URL a la imagen


    @Column(name = "biografia")
    private String biografia;


    @Column(name = "telefono")
    private String telefono;


    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;


    @Column(name = "correo_verificado", nullable = false)
    private boolean correoVerificado = false;

    @Column(name = "registrado_manual", nullable = false)
    private boolean registradoManual = true;


    @ManyToOne
    @JoinColumn(name = "municipio_residencia", referencedColumnName = "id_municipio")
    @NotNull
    private Municipio municipio;


    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InteresXUsuario> interesesXUsuario;


    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private TokenVerificacion tokenVerificacion;


    public Usuario() {


    }


    public Integer getId_usuario() {
        return id_usuario;
    }


    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }


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


    public String getCorreoElectronico() {
        return correoElectronico;
    }


    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }


    public String getContraseña() {
        return contraseña;
    }


    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
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


    public boolean isCorreoVerificado() {
        return correoVerificado;
    }


    public void setCorreoVerificado(boolean correoVerificado) {
        this.correoVerificado = correoVerificado;
    }


    public boolean isRegistradoManual() {
        return registradoManual;
    }


    public void setRegistradoManual(boolean registradoManual) {
        this.registradoManual = registradoManual;
    }


    public Municipio getMunicipio() {
        return municipio;
    }


    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }


}
