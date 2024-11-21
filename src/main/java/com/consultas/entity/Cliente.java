package com.consultas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clientes_clinica")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String email;

    private String dataConsulta;

    @Enumerated(EnumType.STRING)
    private Status status;

    //@Enumerated(EnumType.STRING)
    private String medico;

    private String telefone;

    private String dataDeNascimento;

    private String genero;

    private String endereco;

    private String profissao;

    private String contatoEmergencia;

    private String nomeContatoEmergencia;

    private boolean preferencial; // Adicionado o campo preferencial

}
