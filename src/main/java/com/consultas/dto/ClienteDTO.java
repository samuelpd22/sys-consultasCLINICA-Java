package com.consultas.dto;

import com.consultas.entity.Status;
import lombok.Getter;


public record ClienteDTO(String nome,
                         String email,
                         String medico,
                         String dataConsulta,
                         String telefone,
                         String dataDeNascimento,
                         String genero,
                         String endereco,
                         String profissao,
                         String contatoEmergencia,
                         String nomeContatoEmergencia,
                         boolean preferencial) {
}
