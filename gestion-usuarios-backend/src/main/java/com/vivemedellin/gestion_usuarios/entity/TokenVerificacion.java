package com.vivemedellin.gestion_usuarios.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;


@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TokenVerificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String token;


    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;


    private LocalDateTime expiracion;


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getToken() {
        return token;
    }


    public void setToken(String token) {
        this.token = token;
    }


    public Usuario getUsuario() {
        return usuario;
    }


    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }


    public LocalDateTime getExpiracion() {
        return expiracion;
    }


    public void setExpiracion(LocalDateTime expiracion) {
        this.expiracion = expiracion;
    }
}

