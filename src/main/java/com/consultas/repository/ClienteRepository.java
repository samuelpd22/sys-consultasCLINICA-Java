package com.consultas.repository;

import com.consultas.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("SELECT c FROM Cliente c WHERE LOWER(REPLACE(c.medico, ' ', '')) = LOWER(REPLACE(:medico, ' ', ''))")
    Cliente findByMedico(@Param("medico") String medico);

    List<Cliente> findByPreferencialTrue();  // Retorna todos os clientes preferenciais

    Optional<Cliente> findByNome(String nome);
    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Cliente> findByNomeContainingIgnoreCase(@Param("nome") String nome);


}


