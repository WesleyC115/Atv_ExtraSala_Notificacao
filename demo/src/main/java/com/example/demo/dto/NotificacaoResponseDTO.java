package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacaoResponseDTO {
    private Long id;
    private String mensagem;
    private String categoria;
    private String status;
    private String usuarioCpf;
    private Long produtoId;
}