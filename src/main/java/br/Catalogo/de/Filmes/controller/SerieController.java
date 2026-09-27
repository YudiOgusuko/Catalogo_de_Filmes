package br.Catalogo.de.Filmes.controller;

import br.Catalogo.de.Filmes.dto.SerieDto;
import br.Catalogo.de.Filmes.dto.SerieEpisodioDto;
import br.Catalogo.de.Filmes.dto.SerieTemporadaDto;
import br.Catalogo.de.Filmes.dto.conteudosDados.ConteudoSearchDados;
import br.Catalogo.de.Filmes.service.SerieService;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/omdbapi.com/series")
public class SerieController {

    private final SerieService service;

    @GetMapping("/tituloIgual")
    public ResponseEntity<ConteudoSearchDados> tituloIgual(
            @RequestParam
            @NotEmpty(message = "O nome da série não pode estar vazia.")
            @Size(max = 74, message = "O nome da série pode ter no máximo, 74 caracteres.")
            String serie) {
        return ResponseEntity.ok().body(service.tituloIgual(serie));
    }

    @GetMapping("/buscar")
    public ResponseEntity<SerieDto> buscarSerie(
            @RequestParam
            @NotEmpty(message = "O nome da série não pode estar vazia.")
            @Size(max = 74, message = "O nome da série pode ter no máximo, 74 caracteres.")
            String serie) {
        return ResponseEntity.ok().body(service.buscarSerie(serie));
    }

    @GetMapping("/pegarTemporada")
    public ResponseEntity<SerieTemporadaDto> pegarTemporada(
            @RequestParam
            @NotEmpty(message = "O nome da série não pode estar vazia.")
            @Size(max = 74, message = "O nome da série pode ter no máximo, 74 caracteres.")
            String serie,

            @RequestParam
            @NotNull(message = "A temporada não pode ser nula.")
            @Positive(message = "O número deve ser positivo.")
            @Max(value = 60, message = "O número máximo de temporada é 60")
            Integer temporada) {
        return ResponseEntity.ok().body(service.pegarTemporada(serie, temporada));
    }

    @GetMapping("/pegarEpisodio")
    public ResponseEntity<SerieEpisodioDto> pegarEpisodio(
            @RequestParam
            @NotEmpty(message = "O nome da série não pode estar vazia.")
            @Size(max = 74, message = "O nome da série pode ter no máximo, 74 caracteres.")
            String serie,

            @RequestParam
            @NotNull(message = "A temporada não pode ser nula.")
            @Positive(message = "O número deve ser positivo.")
            @Max(value = 60, message = "O número máximo de temporada é 60")
            Integer temporada,

            @RequestParam
            @NotNull(message = "O episódios  não pode ser nulo.")
            @Positive(message = "O número deve ser positivo.")
            @Max(value = 195, message = "O número máximo de episódios é 195.")
            Integer episodio) {
        return ResponseEntity.ok().body(service.pegarEpisodio(serie, temporada, episodio));
    }

    @GetMapping("/all")
    public ResponseEntity<List<SerieDto>> verTodosSeries() {
        return ResponseEntity.ok().body(service.verTodasSeries());
    }

    @PostMapping("/add")
    public ResponseEntity<SerieDto> adicionarSerie(
            @RequestParam
            @NotEmpty(message = "O nome do filme não pode esta vazia.")
            @Size(max = 41, message = "O nome do filme não pode ultrapassar 41 caracteres.")
            String serie) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarSerie(serie));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deletarSerie(
            @RequestParam
            @NotEmpty(message = "O nome do filme não pode esta vazia.")
            @Size(max = 41, message = "O nome do filme não pode ultrapassar 41 caracteres.")
            String serie) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(service.deletarSerie(serie));
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deletarTodosSeries() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(service.deletarTodasSeries());
    }
}
