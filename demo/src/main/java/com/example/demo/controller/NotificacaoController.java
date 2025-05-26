package com.example.demo.controller;


import com.example.demo.dto.NotificacaoResponseDTO;
import com.example.demo.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @GetMapping
    public ResponseEntity<List<NotificacaoResponseDTO>> listarNotificacoes() {
        return notificacaoService.findAll();
    }

    @GetMapping("/get/UserEstatus")
    public ResponseEntity<List<NotificacaoResponseDTO>> listarNotificacoesPorUsuarioEstatus(@RequestParam String cpf,@RequestParam String status) {
        return notificacaoService.findByUsuarioAndStatus(cpf,status);
    }

    @GetMapping("/get/{cpf}")
    public ResponseEntity<List<NotificacaoResponseDTO>> listarnotificacoesPorUsuario(@PathVariable String cpf) {
        return notificacaoService.findByUsuario(cpf);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<String> atualizarNotificacaoComoLida(@PathVariable Long id) {
        return notificacaoService.marcarComoLida(id);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<String> deletarNotificacao(@PathVariable Long id) {
        return notificacaoService.delete(id);
    }
}