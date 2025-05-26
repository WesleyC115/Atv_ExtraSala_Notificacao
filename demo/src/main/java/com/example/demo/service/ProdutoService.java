package com.example.demo.service;

import com.example.demo.dto.NotificacaoResponseDTO;
import com.example.demo.dto.ProdutoRequestDTO;
import com.example.demo.dto.ProdutoResponseDTO;
import com.example.demo.entity.Notificacao;
import com.example.demo.entity.Produto;
import com.example.demo.entity.Usuario;
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
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoService notificacaoService;

    public ResponseEntity<String> create(ProdutoRequestDTO dto) {
        try {
            if (produtoRepository.existsByNome(dto.getNome())) {
                return ResponseEntity.badRequest().body("Erro: Produto com nome '" + dto.getNome() + "' já existe.");
            }
            Produto produto = toEntity(dto);
            produtoRepository.save(produto);
            return ResponseEntity.ok("Produto criado com sucesso: " + produto.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao criar produto: " + e.getMessage());
        }
    }

    public ResponseEntity<List<ProdutoResponseDTO>> findAll() {
        try {
            List<ProdutoResponseDTO> produtos = produtoRepository.findAll().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    public ResponseEntity<String> update(Long id, ProdutoRequestDTO dto) {
        try {
            Produto produto = produtoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));
            Double precoAntigo = produto.getPreco();
            produto.setNome(dto.getNome());
            produto.setPreco(dto.getPreco());
            produto.setCategoria(dto.getCategoria());
            produtoRepository.save(produto);

            if (!precoAntigo.equals(dto.getPreco())) {
                notificarUsuarios(produto, precoAntigo);
            }

            return ResponseEntity.ok("Produto atualizado com sucesso: " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar produto: " + e.getMessage());
        }
    }

    public ResponseEntity<String> delete(Long id) {
        try {
            if (!produtoRepository.existsById(id)) {
                throw new RuntimeException("Produto não encontrado: " + id);
            }
            produtoRepository.deleteById(id);
            return ResponseEntity.ok("Produto deletado com sucesso: " + id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao deletar produto: " + e.getMessage());
        }
    }

    public ProdutoResponseDTO toDto(Produto produto) {
        List<Long> notificacoesIds = produto.getNotificacoes() != null
                ? produto.getNotificacoes().stream()
                .map(Notificacao::getId)
                .collect(Collectors.toList())
                : Collections.emptyList();
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getPreco(),
                produto.getCategoria(),
                notificacoesIds
        );
    }

    public Produto toEntity(ProdutoRequestDTO dto) {
        return new Produto(
                null,
                dto.getNome(),
                dto.getPreco(),
                dto.getCategoria(),
                Collections.emptyList()
        );
    }

    private void notificarUsuarios(Produto produto, Double precoAntigo) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        for (Usuario usuario : usuarios) {
            NotificacaoResponseDTO notificacaoDTO = new NotificacaoResponseDTO();
            notificacaoDTO.setMensagem(String.format("Preço do produto %s alterado de %s para %s",
                    produto.getNome(), precoAntigo, produto.getPreco()));
            notificacaoDTO.setCategoria("Atualização de Preço");
            notificacaoDTO.setStatus("pendente");
            notificacaoDTO.setUsuarioCpf(usuario.getCpf());
            notificacaoDTO.setProdutoId(produto.getId());
            notificacaoService.create(notificacaoDTO);
        }
    }
}