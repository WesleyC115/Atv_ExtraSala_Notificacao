package com.example.demo.service;

import com.example.demo.dto.NotificacaoResponseDTO;
import com.example.demo.entity.Notificacao;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.NotificacaoRepository;
import com.example.demo.repository.ProdutoRepository;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacaoService {
    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;

    public ResponseEntity<List<NotificacaoResponseDTO>> findAll() {
        try {
            List<NotificacaoResponseDTO> notificacoes = notificacaoRepository.findAll().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(notificacoes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    public ResponseEntity<List<NotificacaoResponseDTO>> findByUsuarioAndStatus(String cpf, String status) {
        try {
            Usuario usuario = usuarioRepository.findById(cpf)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + cpf));
            List<NotificacaoResponseDTO> notificacoes = notificacaoRepository.findByUsuarioAndStatus(usuario, status)
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(notificacoes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    public ResponseEntity<List<NotificacaoResponseDTO>> findByUsuario(String cpf) {
        try {
            Usuario usuario = usuarioRepository.findById(cpf)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + cpf));
            List<NotificacaoResponseDTO> notificacoes = notificacaoRepository.findByUsuario(usuario)
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(notificacoes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    public ResponseEntity<String> marcarComoLida(Long id) {
        try {
            Notificacao notificacao = notificacaoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Notificação não encontrada: " + id));
            notificacao.setStatus("lida");
            notificacaoRepository.save(notificacao);
            return ResponseEntity.ok("Notificação marcada como lida: " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao marcar notificação como lida: " + e.getMessage());
        }
    }

    public ResponseEntity<String> delete(Long id) {
        try {
            if (!notificacaoRepository.existsById(id)) {
                throw new RuntimeException("Notificação não encontrada: " + id);
            }
            notificacaoRepository.deleteById(id);
            return ResponseEntity.ok("Notificação deletada com sucesso: " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao deletar notificação: " + e.getMessage());
        }
    }

    public ResponseEntity<String> create(NotificacaoResponseDTO dto) {
        try {
            Notificacao notificacao = toEntity(dto);
            notificacaoRepository.save(notificacao);
            return ResponseEntity.ok("Notificação criada com sucesso: " + notificacao.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao criar notificação: " + e.getMessage());
        }
    }

    public NotificacaoResponseDTO toDto(Notificacao notificacao) {
        return new NotificacaoResponseDTO(
                notificacao.getId(),
                notificacao.getMensagem(),
                notificacao.getCategoria(),
                notificacao.getStatus(),
                notificacao.getUsuario().getCpf(),
                notificacao.getProduto().getId()
        );
    }

    private Notificacao toEntity(NotificacaoResponseDTO dto) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuarioRepository.findById(dto.getUsuarioCpf())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + dto.getUsuarioCpf())));
        notificacao.setProduto(produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + dto.getProdutoId())));
        notificacao.setMensagem(dto.getMensagem());
        notificacao.setCategoria(dto.getCategoria());
        notificacao.setStatus(dto.getStatus());
        return notificacao;
    }
}