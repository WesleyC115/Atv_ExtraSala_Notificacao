package com.example.demo.service;

import com.example.demo.dto.UsuarioRequestDTO;
import com.example.demo.dto.UsuarioResponseDTO;
import com.example.demo.entity.Notificacao;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public ResponseEntity<String> create(UsuarioRequestDTO dto) {
        try {
            Usuario usuario = toEntity(dto);
            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Usuário criado com sucesso: " + usuario.getCpf());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao criar usuário: " + e.getMessage());
        }
    }

    public ResponseEntity<List<UsuarioResponseDTO>> findAll() {
        try {
            List<UsuarioResponseDTO> usuarios = usuarioRepository.findAll().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    public ResponseEntity<String> update(String cpf, UsuarioRequestDTO dto) {
        try {
            Usuario usuario = usuarioRepository.findById(cpf)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + cpf));
            usuario.setNome(dto.getNome());
            usuario.setSobrenome(dto.getSobrenome());
            usuario.setEmail(dto.getEmail());
            usuario.setSenha(dto.getSenha());
            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Usuário atualizado com sucesso: " + cpf);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar usuário: " + e.getMessage());
        }
    }

    public ResponseEntity<String> delete(String cpf) {
        try {
            if (!usuarioRepository.existsById(cpf)) {
                throw new RuntimeException("Usuário não encontrado: " + cpf);
            }
            usuarioRepository.deleteById(cpf);
            return ResponseEntity.ok("Usuário deletado com sucesso: " + cpf);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao deletar usuário: " + e.getMessage());
        }
    }

    public UsuarioResponseDTO toDto(Usuario usuario) {
        List<Long> notificacoesIds = usuario.getNotificacoes() != null
                ? usuario.getNotificacoes().stream()
                .map(Notificacao::getId)
                .collect(Collectors.toList())
                : Collections.emptyList();
        return new UsuarioResponseDTO(
                usuario.getCpf(),
                usuario.getNome(),
                usuario.getSobrenome(),
                usuario.getEmail(),
                usuario.getSenha(),
                notificacoesIds
        );
    }

    public Usuario toEntity(UsuarioRequestDTO dto) {
        return new Usuario(
                dto.getCpf(),
                dto.getNome(),
                dto.getSobrenome(),
                dto.getEmail(),
                dto.getSenha(),
                Collections.emptyList()
        );
    }
}