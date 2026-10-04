package br.Catalogo.de.Filmes.controller;

import br.Catalogo.de.Filmes.dto.FilmeDto;
import br.Catalogo.de.Filmes.dto.conteudosDados.ConteudoSearchDados;
import br.Catalogo.de.Filmes.service.FilmeService;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/omdbapi.com/filmes")
@Validated
public class FilmeController {

    private final FilmeService service;

    @GetMapping("/tituloIgual")
    public ResponseEntity<ConteudoSearchDados> tituloIgual(
            @RequestParam
            @NotEmpty(message = "O nome do filme não pode estar vazio.")
            @Size(max = 41, message = "O nome do filme não pode ultrapassar 41 caracteres.")
            String filme) {
        return ResponseEntity.ok().body(service.tituloIgual(filme));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<FilmeDto>> buscarFilme(
            @RequestParam
            @NotEmpty(message = "O nome do filme não pode estar vazio.")
            @Size(max = 41, message = "O nome do filme não pode ultrapassar 41 caracteres.")
            String filme) {
        return ResponseEntity.ok().body(service.buscarFilme(filme));
    }

    @GetMapping("/buscarPorGenero")
    public ResponseEntity<List<FilmeDto>> buscarFilmePorGenero(
            @RequestParam
            @NotEmpty(message = "O gênero não pode estar vazio.")
            @Size(max = 12, message = "O gênero do filme não pode ultrapassar 12 caracteres.")
            String genero) {
        return ResponseEntity.ok().body(service.buscarFilmePorGenero(genero));
    }

    @GetMapping("/buscarPorAtor")
    public ResponseEntity<List<FilmeDto>> buscarFilmePorAtor(
            @RequestParam
            @NotEmpty(message = "O nome do(a) ator(a) não pode estar vazio.")
            @Size(max = 30, message = "O nome do(a) ator(a) não pode ultrapassar 30 caracteres.")
            String ator) {
        return ResponseEntity.ok().body(service.buscarFilmePorAtor(ator));
    }

    @GetMapping("/top5Filmes")
    public ResponseEntity<List<FilmeDto>> top5Filmes() {
        return ResponseEntity.ok().body(service.top5Filmes());
    }

    @GetMapping("/filtrarPorAvaliacaoMaxima")
    public ResponseEntity<List<FilmeDto>> filtrarPorAvaliacaoMaxima(
            @RequestParam
            @NotNull(message = "A avaliação não pode ser nula.")
            @Positive(message = "O valor da avaliação deve ser positivo.")
            @Max(value = 10, message = "O número máximo da avaliação é 10")
            Double avaliacao) {
        return ResponseEntity.ok().body(service.filtrarPorAvaliacaoMaxima(avaliacao));
    }

    @GetMapping("/filtrarPorAvaliacaoMinima")
    public ResponseEntity<List<FilmeDto>> filtrarPorAvaliacaoMinima(
            @RequestParam
            @NotNull(message = "A avaliação não pode ser nula.")
            @Positive(message = "O valor da avaliação deve ser positivo.")
            @Max(value = 10, message = "O número máximo da avaliação é 10")
            Double avaliacao) {
        return ResponseEntity.ok().body(service.filtrarPorAvaliacaoMinima(avaliacao));
    }

    @GetMapping("/filtrarPorAnoMaximo")
    public ResponseEntity<List<FilmeDto>> filtrarPorAnoMaximo(
            @RequestParam
            @NotNull(message = "O ano não pode ser nulo.")
            @PastOrPresent(message = "O ano não pode ser maior que o ano atual.")
            Year ano) {
        return ResponseEntity.ok().body(service.filtrarPorAnoMaximo(ano));
    }

    @GetMapping("/filtrarPorAnoMinimo")
    public ResponseEntity<List<FilmeDto>> filtrarPorAnoMinimo(
            @RequestParam
            @NotNull(message = "A avaliação não pode ser nulo.")
            @PastOrPresent(message = "O ano não pode ser maior que o ano atual.")
            Year ano) {
        return ResponseEntity.ok().body(service.filtrarPorAnoMinimo(ano));
    }

    @GetMapping("/all")
    public ResponseEntity<List<FilmeDto>> verTodosFilmes() {
        return ResponseEntity.ok().body(service.verTodosFilmes());
    }

    @PostMapping("/add")
    public ResponseEntity<FilmeDto> adicionarFilme(
            @RequestParam
            @NotEmpty(message = "O nome do filme não pode estar vazio.")
            @Size(max = 41, message = "O nome do filme não pode ultrapassar 41 caracteres.")
            String filme) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarFilme(filme));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deletarFilme(
            @RequestParam
            @NotEmpty(message = "O nome do filme não pode estar vazio.")
            @Size(max = 41, message = "O nome do filme não pode ultrapassar 41 caracteres.")
            String filme) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(service.deletarFilme(filme));
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deletarTodosFilmes() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(service.deletarTodosFilmes());
    }
}
