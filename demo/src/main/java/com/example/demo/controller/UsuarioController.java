package com.example.demo.controller;


import com.example.demo.dto.UsuarioRequestDTO;
import com.example.demo.dto.UsuarioResponseDTO;
import com.example.demo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<String> criarUsuario(@RequestBody UsuarioRequestDTO dto) {
        return usuarioService.create(dto);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return usuarioService.findAll();
    }

    @PutMapping("/atualizar/{cpf}")
    public ResponseEntity<String> atualizarUsuario(@PathVariable String cpf, @RequestBody UsuarioRequestDTO dto) {
        return usuarioService.update(cpf,dto);
    }

    @DeleteMapping("/deletar/{cpf}")
    public ResponseEntity<String> deletarUsuario(@PathVariable String cpf) {
        return usuarioService.delete(cpf);
    }
}