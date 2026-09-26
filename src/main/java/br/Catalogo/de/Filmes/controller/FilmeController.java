package br.Catalogo.de.Filmes.controller;

import br.Catalogo.de.Filmes.dto.FilmeDto;
import br.Catalogo.de.Filmes.dto.conteudosDados.ConteudoSearchDados;
import br.Catalogo.de.Filmes.service.FilmeService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/omdbapi.com/filmes")
public class FilmeController {

    private final FilmeService service;

    @GetMapping("/tituloIgual")
    public ResponseEntity<ConteudoSearchDados> tituloIgual(
            @RequestParam
            @NotEmpty(message = "O nome do filme não pode esta vazia.")
            @Size(max = 41, message = "O nome do filme não pode ultrapassar 41 caracteres.")
            String filme) {
        return ResponseEntity.ok().body(service.tituloIgual(filme));
    }

    @GetMapping("/buscar")
    public ResponseEntity<FilmeDto> buscarFilmes(
            @RequestParam
            @NotEmpty(message = "O nome do filme não pode esta vazia.")
            @Size(max = 41, message = "O nome do filme não pode ultrapassar 41 caracteres.")
            String filme) {
        return ResponseEntity.ok().body(service.buscarFilmes(filme));
    }

    @PostMapping("/add")
    public ResponseEntity<FilmeDto> adicionarFilme(
            @RequestParam
            @NotEmpty(message = "O nome do filme não pode esta vazia.")
            @Size(max = 41, message = "O nome do filme não pode ultrapassar 41 caracteres.")
            String filme) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarFilme(filme));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deletarFilme(
            @RequestParam
            @NotEmpty(message = "O nome do filme não pode esta vazia.")
            @Size(max = 41, message = "O nome do filme não pode ultrapassar 41 caracteres.")
            String filme) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(service.deletarFilme(filme));
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deletarTodosFilmes() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(service.deletarTodosFilmes());
    }

    @GetMapping("/all")
    public ResponseEntity<List<FilmeDto>> verTodosFilmes() {
        return ResponseEntity.ok().body(service.verTodosFilmes());
    }
}
