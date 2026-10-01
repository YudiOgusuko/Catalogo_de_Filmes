package br.Catalogo.de.Filmes.controller;

import br.Catalogo.de.Filmes.dto.FilmeDto;
import br.Catalogo.de.Filmes.dto.conteudosDados.ConteudoSearchDados;
import br.Catalogo.de.Filmes.service.FilmeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Year;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;

@WebMvcTest(FilmeController.class)
class FilmeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FilmeService service;

    private final String request = "/omdbapi.com/filmes";
    private final String filme = "Harry Potter";

    @Test
    @DisplayName("Pegar todos filmes com títulos iguais na API.")
    void tituloIgual() throws Exception {

        given(service.tituloIgual(filme))
                .willReturn(ConteudoSearchDados.builder().build());

        mockMvc.perform(
                get(request + "/tituloIgual")
                        .param("filme", filme)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

    }

    @Test
    @DisplayName("Buscar o filme no Banco.")
    void buscarFilme_NoBanco() throws Exception {

        given(service.buscarFilme(filme))
                .willReturn(List.of(FilmeDto.builder().build()));

        mockMvc.perform(
                get(request + "/buscar")
                        .param("filme", filme)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Buscar filme por Gênero.")
    void buscarFilmePorGenero() throws Exception {

        String genero = "Fantasia";

        given(service.buscarFilmePorGenero(genero))
                .willReturn(List.of(FilmeDto.builder().build()));

        mockMvc.perform(
                get(request + "/buscarPorGenero")
                        .param("genero", genero)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Buscar filme por Ator.")
    void buscarFilmePorAtor() throws Exception {

        String ator = "Johnny Depp";

        given(service.buscarFilmePorAtor(ator))
                .willReturn(List.of(FilmeDto.builder().build()));

        mockMvc.perform(
                get(request + "/buscarPorAtor")
                        .param("ator", ator)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Top 5 filmes do Catálogo.")
    void top5Filmes() throws Exception {

        given(service.top5Filmes())
                .willReturn(List.of(FilmeDto.builder().build()));

        mockMvc.perform(
                get(request + "/top5Filmes")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Filtrar filmes pela avaliação máxima.")
    void filtrarPorAvaliacaoMaxima() throws Exception {

        Double avaliacao = 9.3;

        given(service.filtrarPorAvaliacaoMaxima(avaliacao))
                .willReturn(List.of(FilmeDto.builder().build()));

        mockMvc.perform(
                get(request + "/filtrarPorAvaliacaoMaxima")
                        .param("avaliacao", String.valueOf(avaliacao))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Filtrar filmes pela avaliação mínima.")
    void filtrarPorAvaliacaoMinima() throws Exception {

        Double avaliacao = 7.0;

        given(service.filtrarPorAvaliacaoMinima(avaliacao))
                .willReturn(List.of(FilmeDto.builder().build()));

        mockMvc.perform(
                get(request + "/filtrarPorAvaliacaoMinima")
                        .param("avaliacao", String.valueOf(avaliacao))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Filtrar filmes pelo ano máximo.")
    void filtrarPorAnoMaximo() throws Exception {

        Year ano = Year.now();

        given(service.filtrarPorAnoMaximo(ano))
                .willReturn(List.of(FilmeDto.builder().build()));

        mockMvc.perform(
                get(request + "/filtrarPorAnoMaximo")
                        .param("ano", String.valueOf(ano))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Filtrar filmes pelo ano mínimo.")
    void filtrarPorAnoMinimo() throws Exception {

        Year ano = Year.now();

        given(service.filtrarPorAnoMinimo(ano))
                .willReturn(List.of(FilmeDto.builder().build()));

        mockMvc.perform(
                get(request + "/filtrarPorAnoMinimo")
                        .param("ano", String.valueOf(ano))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Ver todos os filmes")
    void verTodosFilmes() throws Exception {

        given(service.verTodosFilmes())
                .willReturn(List.of(FilmeDto.builder().build()));

        mockMvc.perform(
                get(request + "/all")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Adicionar filme")
    void adicionarFilme() throws Exception {

        given(service.adicionarFilme(filme))
                .willReturn(FilmeDto.builder().build());

        mockMvc.perform(
                post(request + "/add")
                        .param("filme", filme)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deletar algum filme.")
    void deletarFilme() throws Exception {

        String mensagem = String.format("O filme %s foi deletado do catálogo.", filme);

        given(service.deletarFilme(filme))
                .willReturn(mensagem);

        mockMvc.perform(
                delete(request + "/delete")
                        .param("filme", filme)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deletar todos os filmes.")
    void deletarTodosFilmes() throws Exception {

        String mensagem = "Todos os filmes foram deletados do catálogo.";

        given(service.deletarTodosFilmes())
                .willReturn(mensagem);

        mockMvc.perform(
                delete(request + "/deleteAll")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }
}