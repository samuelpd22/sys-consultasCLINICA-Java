package com.consultas.controller;

import com.consultas.dto.ClienteDTO;
import com.consultas.dto.FILAdeClientesDTO;
import com.consultas.entity.Cliente;
import com.consultas.entity.Status;
import com.consultas.repository.ClienteRepository;
import com.consultas.service.ClienteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/cad")
public class ClienteController {

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private ClienteRepository repository;

    @Autowired
    private ClienteService service;


    @GetMapping
    public ResponseEntity<List<Cliente>> todosClientes(){
        List<Cliente> clientes = repository.findAll();

        // Ordenar clientes, preferenciais primeiro
        clientes.sort((c1, c2) -> Boolean.compare(c2.isPreferencial(), c1.isPreferencial()));

        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Cliente> cadastrarOuAtualizarCliente(@RequestBody ClienteDTO data) {
        // Verifica se o cliente já existe, baseado no nome (pode ser outros critérios como telefone)
        Optional<Cliente> clienteExistente = repository.findByNome(data.nome());

        Cliente cliente;
        if (clienteExistente.isPresent()) {
            // Atualiza cliente existente
            cliente = clienteExistente.get();
            cliente.setDataConsulta(data.dataConsulta());
            cliente.setMedico(data.medico());
            cliente.setStatus(Status.valueOf("Agendado"));

            // Atualizar informações relevantes como a preferência, se necessário
            int idade = service.calcularIdade(cliente.getDataDeNascimento());
            cliente.setPreferencial(idade >= 60);

            repository.save(cliente);  // Salva o cliente atualizado
            return new ResponseEntity<>(cliente, HttpStatus.OK);  // Retorna o cliente atualizado
        } else {
            // Cria um novo cliente
            cliente = new Cliente();
            BeanUtils.copyProperties(data, cliente);
            cliente.setStatus(Status.valueOf("Agendado"));

            // Verificar idade e definir a preferência
            int idade = service.calcularIdade(cliente.getDataDeNascimento());
            cliente.setPreferencial(idade >= 60);

            repository.save(cliente);  // Salva o novo cliente
            return new ResponseEntity<>(cliente, HttpStatus.CREATED);  // Retorna o novo cliente criado
        }
    }


    //APENAS TESTE
    @PostMapping("/fila")
    public ResponseEntity<Cliente> filaDeClientes(@RequestBody FILAdeClientesDTO data){
        Cliente novoCli = new Cliente();
        BeanUtils.copyProperties(data, novoCli);
        novoCli.setStatus(Status.valueOf("Pendente"));
        repository.save(novoCli);
        return new ResponseEntity(novoCli,HttpStatus.CREATED);
    }
    @GetMapping("/{id}/telefone")
    public ResponseEntity<Map<String, String>> getTelefone(@PathVariable Long id) {
        Cliente cliente = repository.findById(id).orElse(null);

        if (cliente != null) {
            // Retorna o telefone em um JSON com a chave "telefone"
            Map<String, String> response = new HashMap<>();
            response.put("telefone", cliente.getTelefone());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    //APENAS TESTE

    @GetMapping("/medico/{medico}")
    public ResponseEntity<List<Cliente>> listaPorMedico(@PathVariable String medico){
        Cliente listaClientes = repository.findByMedico(medico);
        return new ResponseEntity(listaClientes,HttpStatus.OK);
    }





    @PutMapping("/cancelar/{id}")
    public ResponseEntity<Cliente> cancelarEAgendarExclusao(@PathVariable Long id) {
        return repository.findById(id)
                .map(tarefa -> {
                    // Define o status como "cancelado"
                    tarefa.setStatus(Status.valueOf("Cancelado"));
                    repository.save(tarefa); // Salva a tarefa atualizada

                    // Agenda a exclusão após 2 horas (7200000 milissegundos)
                    taskScheduler.schedule(() -> repository.deleteById(id),
                            new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2))
                    );

                    return ResponseEntity.ok(tarefa);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/reagendar/{id}")
    public ResponseEntity<Cliente> reagendarConsulta(@PathVariable Long id,@RequestBody ClienteDTO data) {
        return repository.findById(id)

                .map(cliente -> {
                    // Define o status como "cancelado"

                    cliente.setDataConsulta(data.dataConsulta());
                    cliente.setMedico(data.medico());
                    cliente.setStatus(Status.valueOf("Agendado"));
                    repository.save(cliente); // Salva a tarefa atualizada
                    return ResponseEntity.ok(cliente);

                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



    @GetMapping("/cliente")
    public ResponseEntity<List<Cliente>> buscarClientesPorNome(@RequestParam String nome) {
        List<Cliente> clientes = repository.findByNomeContainingIgnoreCase(nome);
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }
}

