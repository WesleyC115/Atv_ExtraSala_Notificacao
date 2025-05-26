package com.example.demo.repository;


import com.example.demo.entity.Notificacao;
import com.example.demo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao,Long> {
    List<Notificacao> findByUsuarioAndStatus(Usuario usuario, String status);

    List<Notificacao> findByUsuario(Usuario usuario);
}