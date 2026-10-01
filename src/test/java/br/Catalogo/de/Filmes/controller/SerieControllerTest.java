package br.Catalogo.de.Filmes.controller;

import br.Catalogo.de.Filmes.dto.SerieDto;
import br.Catalogo.de.Filmes.dto.SerieEpisodioDto;
import br.Catalogo.de.Filmes.dto.SerieEpisodiosTemporadaDto;
import br.Catalogo.de.Filmes.dto.SerieTemporadaDto;
import br.Catalogo.de.Filmes.dto.conteudosDados.ConteudoSearchDados;
import br.Catalogo.de.Filmes.service.SerieService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Year;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SerieController.class)
class SerieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SerieService service;

    private final String request = "/omdbapi.com/series";
    private final String serie = "The Wire";

    @Test
    @DisplayName("Pegar todas as séries com títulos iguais na API.")
    void tituloIgual() throws Exception {

        given(service.tituloIgual(serie))
                .willReturn(ConteudoSearchDados.builder().build());

        mockMvc.perform(
                get(request + "/tituloIgual")
                        .param("serie", serie)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    @DisplayName("Buscar a série no Banco.")
    void buscarSerieNoBanco() throws Exception {

        given(service.buscarSerie(serie))
                .willReturn(List.of(SerieDto.builder().build()));

        mockMvc.perform(
                get(request + "/buscar")
                        .param("serie", serie)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Buscar série por Gênero.")
    void buscarSeriePorGenero() throws Exception {

        String genero = "Crime";

        given(service.buscarSeriePorGenero(genero))
                .willReturn(List.of(SerieDto.builder().build()));

        mockMvc.perform(
                get(request + "/buscarPorGenero")
                        .param("genero", genero)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Buscar série por Ator.")
    void buscarSeriePorAtor() throws Exception {

        String ator = "James Gandolfini";

        given(service.buscarSeriePorAtor(ator))
                .willReturn(List.of(SerieDto.builder().build()));

        mockMvc.perform(
                get(request + "/buscarPorAtor")
                        .param("ator", ator)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Top 5 séries do Catálogo.")
    void top5Series() throws Exception {

        given(service.top5Series())
                .willReturn(List.of(SerieDto.builder().build()));

        mockMvc.perform(
                get(request + "/top5Series")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Top 5 episódios da série.")
    void top5EpisodiosDaSerie() throws Exception {

        given(service.top5EpisodiosDaSerie(serie))
                .willReturn(List.of(SerieEpisodiosTemporadaDto.builder().build()));

        mockMvc.perform(
                get(request + "/top5Episodios")
                        .param("serie", serie)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Buscar temporada de série no Banco.")
    void buscarTemporada_NoBanco() throws Exception {

        Integer temporada = 1;

        given(service.buscarTemporada(serie, temporada))
                .willReturn(SerieTemporadaDto.builder().build());

        mockMvc.perform(
                get(request + "/pegarTemporada")
                        .param("serie", serie)
                        .param("temporada", String.valueOf(temporada))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Buscar episódio de série no Banco.")
    void buscarEpisodio_NoBanco() throws Exception {

        Integer temporada = 1;
        Integer episodio = 10;

        given(service.buscarEpisodio(serie, temporada, episodio))
                .willReturn(SerieEpisodioDto.builder().build());

        mockMvc.perform(
                get(request + "/pegarEpisodio")
                        .param("serie", serie)
                        .param("temporada", String.valueOf(temporada))
                        .param("episodio", String.valueOf(episodio))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Filtrar séries por temporadas mínimas.")
    void filtrarPorTemporadaMaxima() throws Exception {

        Integer temporada = 6;

        given(service.filtrarPorTemporadaMaxima(temporada))
                .willReturn(List.of(SerieDto.builder().build()));

        mockMvc.perform(
                get(request + "/filtrarPorTemporadaMaxima")
                        .param("temporada", String.valueOf(temporada))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Filtrar séries por temporadas máximas.")
    void filtrarPorTemporadaMinima() throws Exception {

        Integer temporada = 2;

        given(service.filtrarPorTemporadaMaxima(temporada))
                .willReturn(List.of(SerieDto.builder().build()));

        mockMvc.perform(
                get(request + "/filtrarPorTemporadaMinima")
                        .param("temporada", String.valueOf(temporada))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Filtrar séries pela avaliação máxima.")
    void filtrarPorAvaliacaoMaxima() throws Exception {

    Double avaliacao = 9.3;

    given(service.filtrarPorAvaliacaoMaxima(avaliacao))
            .willReturn(List.of(SerieDto.builder().build()));

    mockMvc.perform(
            get(request + "/filtrarPorAvaliacaoMaxima")
                    .param("avaliacao", String.valueOf(avaliacao))
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk());
}

    @Test
    @DisplayName("Filtrar séries pela avaliação mínima.")
    void filtrarPorAvaliacaoMinima() throws Exception {

        Double avaliacao = 7.0;

        given(service.filtrarPorAvaliacaoMinima(avaliacao))
                .willReturn(List.of(SerieDto.builder().build()));

        mockMvc.perform(
                get(request + "/filtrarPorAvaliacaoMinima")
                        .param("avaliacao", String.valueOf(avaliacao))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Filtrar séries pelo ano máximo.")
    void filtrarPorAnoMaximo() throws Exception {

        Year ano = Year.now();

        given(service.filtrarPorAnoMaximo(ano))
                .willReturn(List.of(SerieDto.builder().build()));

        mockMvc.perform(
                get(request + "/filtrarPorAnoMaximo")
                        .param("ano", String.valueOf(ano))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Filtrar séries pelo ano mínimo.")
    void filtrarPorAnoMinimo() throws Exception {

        Year ano = Year.now();

        given(service.filtrarPorAnoMinimo(ano))
                .willReturn(List.of(SerieDto.builder().build()));

        mockMvc.perform(
                get(request + "/filtrarPorAnoMinimo")
                        .param("ano", String.valueOf(ano))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Ver todos as série")
    void verTodasSeries() throws Exception {

        given(service.verTodasSeries())
                .willReturn(List.of(SerieDto.builder().build()));

        mockMvc.perform(
                get(request + "/all")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Adicionar série")
    void adicionarSerie() throws Exception {

        given(service.adicionarSerie(serie))
                .willReturn(SerieDto.builder().build());

        mockMvc.perform(
                post(request + "/add")
                        .param("serie", serie)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
    }
    @Test
    @DisplayName("Deletar alguma série.")
    void deletarSerie() throws Exception {

        String mensagem = String.format("A série %s foi deletada do catálogo.", serie);

        given(service.deletarSerie(serie))
                .willReturn(mensagem);

        mockMvc.perform(
                delete(request + "/delete")
                        .param("serie", serie)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deletar todas as séries.")
    void deletarTodasSeries() throws Exception {

        String mensagem = "Todos as séries foram deletadas do catálogo.";

        given(service.deletarTodasSeries())
                .willReturn(mensagem);

        mockMvc.perform(
                delete(request + "/deleteAll")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }
}