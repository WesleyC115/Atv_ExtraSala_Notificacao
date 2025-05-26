package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {
    private String cpf;
    private String nome;
    private String sobrenome;
    private String email;
    private String senha;
    private List<Long> notificacoesIds;

}