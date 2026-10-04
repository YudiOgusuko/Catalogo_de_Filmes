package br.Catalogo.de.Filmes.controller;

import java.time.Year;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.Catalogo.de.Filmes.dto.SerieDto;
import br.Catalogo.de.Filmes.dto.SerieEpisodioDto;
import br.Catalogo.de.Filmes.dto.SerieEpisodiosTemporadaDto;
import br.Catalogo.de.Filmes.dto.SerieTemporadaDto;
import br.Catalogo.de.Filmes.dto.conteudosDados.ConteudoSearchDados;
import br.Catalogo.de.Filmes.service.SerieService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/omdbapi.com/series")
@Validated
public class SerieController {

    private final SerieService service;

    @GetMapping("/tituloIgual")
    public ResponseEntity<ConteudoSearchDados> tituloIgual(
            @RequestParam
            @NotEmpty(message = "O nome da série não pode estar vazio.")
            @Size(max = 74, message = "O nome da série pode ter no máximo, 74 caracteres.")
            String serie) {
        return ResponseEntity.ok().body(service.tituloIgual(serie));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<SerieDto>> buscarSerie(
            @RequestParam
            @NotEmpty(message = "O nome da série não pode estar vazio.")
            @Size(max = 74, message = "O nome da série pode ter no máximo 74 caracteres.")
            String serie) {
        return ResponseEntity.ok().body(service.buscarSerie(serie));
    }

    @GetMapping("/buscarPorGenero")
    public ResponseEntity<List<SerieDto>> buscarSeriePorGenero(
            @RequestParam
            @NotEmpty(message = "O gênero não pode estar vazio.")
            @Size(max = 12, message = "O gênero da série não pode ultrapassar 12 caracteres.")
            String genero) {
        return ResponseEntity.ok().body(service.buscarSeriePorGenero(genero));
    }

    @GetMapping("/buscarPorAtor")
    public ResponseEntity<List<SerieDto>> buscarSeriePorAtor(
            @RequestParam
            @NotEmpty(message = "O nome do(a) ator(a) não pode estar vazio.")
            @Size(max = 30, message = "O nome do(a) ator(a) não pode ultrapassar 30 caracteres.")
            String ator) {
        return ResponseEntity.ok().body(service.buscarSeriePorAtor(ator));
    }

    @GetMapping("/top5Series")
    public ResponseEntity<List<SerieDto>> top5Series() {
        return ResponseEntity.ok().body(service.top5Series());
    }

    @GetMapping("/top5Episodios")
    public ResponseEntity<List<SerieEpisodiosTemporadaDto>> top5EpisodiosDaSerie(
            @RequestParam
            @NotEmpty(message = "O nome da série não pode estar vazio.")
            @Size(max = 30, message = "O nome da série pode ter no máximo 30 caracteres.")
            String serie){
        return ResponseEntity.ok().body(service.top5EpisodiosDaSerie(serie));
    }

    @GetMapping("/pegarTemporada")
    public ResponseEntity<SerieTemporadaDto> buscarTemporada(
            @RequestParam
            @NotEmpty(message = "O nome da série não pode estar vazio.")
            @Size(max = 74, message = "O nome da série pode ter no máximo, 74 caracteres.")
            String serie,

            @RequestParam
            @NotNull(message = "A temporada não pode ser nula.")
            @Positive(message = "O número da temporada deve ser positivo.")
            @Max(value = 60, message = "O número máximo de temporada é 60")
            Integer temporada) {
        return ResponseEntity.ok().body(service.buscarTemporada(serie, temporada));
    }

    @GetMapping("/pegarEpisodio")
    public ResponseEntity<SerieEpisodioDto> buscarEpisodio(
            @RequestParam
            @NotEmpty(message = "O nome da série não pode estar vazio.")
            @Size(max = 74, message = "O nome da série pode ter no máximo, 74 caracteres.")
            String serie,

            @RequestParam
            @NotNull(message = "A temporada não pode ser nula.")
            @Positive(message = "O número da temporada deve ser positivo.")
            @Max(value = 60, message = "O número máximo de temporada é 60")
            Integer temporada,

            @RequestParam
            @NotNull(message = "O episódio não pode ser nulo.")
            @Positive(message = "O número do episódio deve ser positivo.")
            @Max(value = 195, message = "O número máximo de episódios é 195.")
            Integer episodio) {
        return ResponseEntity.ok().body(service.buscarEpisodio(serie, temporada, episodio));
    }

    @GetMapping("filtrarPorTemporadaMaxima")
    public ResponseEntity<List<SerieDto>> filtrarPorTemporadaMaxima(
            @RequestParam
            @NotNull(message = "A temporada não pode ser nula.")
            @Positive(message = "O número da temporada deve ser positivo.")
            @Max(value = 60, message = "O número máximo de temporada é 60")
            Integer temporada) {
        return ResponseEntity.ok().body(service. filtrarPorTemporadaMaxima(temporada));
    }

    @GetMapping("filtrarPorTemporadaMinima")
    public ResponseEntity<List<SerieDto>> filtrarPorTemporadaMinima(
            @RequestParam
            @NotNull(message = "A temporada não pode ser nula.")
            @Positive(message = "O número da temporada deve ser positivo.")
            @Max(value = 60, message = "O número máximo de temporada é 60")
            Integer temporada) {
        return ResponseEntity.ok().body(service. filtrarPorTemporadaMinima(temporada));
    }

    @GetMapping("filtrarPorAvaliacaoMaxima")
    public ResponseEntity<List<SerieDto>> filtrarPorAvaliacaoMaxima(
            @RequestParam
            @NotNull(message = "A avaliação não pode ser nula.")
            @Positive(message = "O valor da avaliação deve ser positivo.")
            @Max(value = 10, message = "O número máximo da avaliação é 10")
            Double avaliacao) {
        return ResponseEntity.ok().body(service. filtrarPorAvaliacaoMaxima(avaliacao));
    }

    @GetMapping("filtrarPorAvaliacaoMinima")
    public ResponseEntity<List<SerieDto>> filtrarPorAvaliacaoMinima(
            @RequestParam
            @NotNull(message = "A avaliação não pode ser nula.")
            @Positive(message = "O valor da avaliação deve ser positivo.")
            @Max(value = 10, message = "O número máximo da avaliação é 10")
            Double avaliacao) {
        return ResponseEntity.ok().body(service. filtrarPorAvaliacaoMinima(avaliacao));
    }

    @GetMapping("filtrarPorAnoMaximo")
    public ResponseEntity<List<SerieDto>> filtrarPorAnoMaximo(
            @RequestParam
            @NotNull(message = "O ano não pode ser nulo.")
            @PastOrPresent(message = "O ano não pode ser maior que o ano atual.")
            Year ano) {
        return ResponseEntity.ok().body(service. filtrarPorAnoMaximo(ano));
    }

    @GetMapping("filtrarPorAnoMinimo")
    public ResponseEntity<List<SerieDto>> filtrarPorAnoMinimo(
            @RequestParam
            @NotNull(message = "O ano não pode ser nulo.")
            @PastOrPresent(message = "O ano não pode ser maior que o ano atual.")
            Year ano) {
        return ResponseEntity.ok().body(service. filtrarPorAnoMinimo(ano));
    }

    @GetMapping("/all")
    public ResponseEntity<List<SerieDto>> verTodasSeries() {
        return ResponseEntity.ok().body(service.verTodasSeries());
    }

    @PostMapping("/add")
    public ResponseEntity<SerieDto> adicionarSerie(
            @RequestParam
            @NotEmpty(message = "O nome da série não pode esta vazio.")
            @Size(max = 41, message = "O nome da série não pode ultrapassar 41 caracteres.")
            String serie) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarSerie(serie));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deletarSerie(
            @RequestParam
            @NotEmpty(message = "O nome da série não pode estar vazio.")
            @Size(max = 41, message = "O nome da série não pode ultrapassar 41 caracteres.")
            String serie) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(service.deletarSerie(serie));
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deletarTodasSeries() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(service.deletarTodasSeries());
    }
}
