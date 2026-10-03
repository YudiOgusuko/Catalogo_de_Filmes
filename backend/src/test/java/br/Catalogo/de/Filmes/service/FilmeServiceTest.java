package br.Catalogo.de.Filmes.service;

import br.Catalogo.de.Filmes.dto.FilmeDto;
import br.Catalogo.de.Filmes.dto.conteudosDados.ConteudoDados;
import br.Catalogo.de.Filmes.dto.conteudosDados.ConteudoSearchDados;
import br.Catalogo.de.Filmes.dto.filmeDados.FilmeDados;
import br.Catalogo.de.Filmes.enums.Genero;
import br.Catalogo.de.Filmes.handler.exception.NotFoundException;
import br.Catalogo.de.Filmes.model.Filme;
import br.Catalogo.de.Filmes.repository.IFilmeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FilmeServiceTest {

    @InjectMocks
    private FilmeService service;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private IFilmeRepository repository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "apiKey", "${omdb.api.key}");
        ReflectionTestUtils.setField(service, "apiUrl", "${omdb.api.url}");
    }

    private final String filmeEspecifico = "Harry Potter and the Prisoner of Azkaban";

    @Test
    @DisplayName("Pegar todos filmes com títulos iguais na API.")
    void tituloIgual() {

        ConteudoDados conteudoDados1 = ConteudoDados.builder().titulo("Harry Potter and the Deathly Hallows: Part 2").ano("2011").build();
        ConteudoDados conteudoDados2 = ConteudoDados.builder().titulo("Harry Potter and the Order of the Phoenix").ano("2007").build();
        ConteudoDados conteudoDados3 = ConteudoDados.builder().titulo("Harry Potter and the Chamber of Secrets").ano("2002").build();
        List<ConteudoDados> conteudoDadosList = List.of(conteudoDados1, conteudoDados2, conteudoDados3);

        ConteudoSearchDados conteudoSearchDados = ConteudoSearchDados.builder().conteudoDados(conteudoDadosList).build();

        given(restTemplate.getForObject(anyString(), eq(ConteudoSearchDados.class)))
                .willReturn(conteudoSearchDados);

        ConteudoSearchDados resutado = service.tituloIgual("Harry Potter");

        assertThat(resutado).isNotNull();
        then(restTemplate).should().getForObject(anyString(), any());
    }

    @Test
    @DisplayName("Buscar o filme no Banco.")
    void buscarFilme_noBanco() {

       Filme filme = Filme.builder().titulo("Harry Potter and the Prisoner of Azkaban").ano(2004).genero(Genero.ADVENTURE).build();

       given(repository.findByTituloContainingIgnoreCase(anyString()))
               .willReturn(List.of(filme));

        List<FilmeDto> resultado = service.buscarFilme(filmeEspecifico);

        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("Buscar o filme na API.")
    void buscarFilme_NaApi() {

        FilmeDados filmeDados = FilmeDados.builder().titulo("Harry Potter and the Prisoner of Azkaban").ano(2004).genero("Adventure").build();

        given(repository.findByTituloContainingIgnoreCase(anyString()))
                .willReturn(List.of());

        given(restTemplate.getForObject(anyString(), eq(FilmeDados.class)))
                .willReturn(filmeDados);

        List<FilmeDto> resultado = service.buscarFilme(filmeEspecifico);

        assertThat(resultado).isNotNull();
        then(restTemplate).should().getForObject(anyString(), any());
        then(repository).should(never()).findAllByAtoresContainingIgnoreCase(anyString());
    }

    @Test
    @DisplayName("Lançar Exceção ao tentar buscar o filme.")
    void buscarFilme_Excecao() {

        given(repository.findByTituloContainingIgnoreCase(anyString()))
                .willReturn(List.of());

        given(restTemplate.getForObject(anyString(), eq(FilmeDados.class)))
                .willReturn(null);

        assertThrows(NotFoundException.class, () -> service.buscarFilme(filmeEspecifico));

        then(restTemplate).should().getForObject(anyString(), any());
    }

    @Test
    @DisplayName("Buscar filmes pelo gênero.")
    void buscarFilmePorGenero() {

        String generoString = "Ação";
        Genero genero = Genero.pegarGenero(generoString);

        List<Filme> filme = List.of(Filme.builder().titulo("Harry Potter and the Half-Blood Prince").ano(2009).genero(genero).build());

        given(repository.findAllByGenero(genero))
                .willReturn(filme);

        List<FilmeDto> resultado = service.buscarFilmePorGenero(generoString);

        assertThat(resultado).isNotNull();
        then(repository).should().findAllByGenero(genero);
    }

    @Test
    @DisplayName("Buscar filmes por ator ou atriz.")
    void buscarFilmePorAtor() {

        String atriz = "Emma Watson";

        Filme filme1 = Filme.builder().titulo("Harry Potter and the Prisoner of Azkaban").build();
        Filme filme2 = Filme.builder().titulo("Harry Potter and the Goblet of Fire").build();
        Filme filme3 = Filme.builder().titulo("Harry Potter and the Half-Blood Prince").build();

        List<Filme> filmes = List.of(filme1, filme2, filme3);

        given(repository.findAllByAtoresContainingIgnoreCase(atriz))
                .willReturn(filmes);

        List<FilmeDto> resultado = service.buscarFilmePorAtor(atriz);

        assertThat(resultado).isNotNull();
        then(repository).should().findAllByAtoresContainingIgnoreCase(atriz);
    }

    @Test
    @DisplayName("Ver os Top 5 melhores filmes do catálogo.")
    void top5Filmes() {

        given(repository.findTop5())
                .willReturn(List.of(Filme.builder().build()));

        List<FilmeDto> resultado = service.top5Filmes();

        assertThat(resultado).isNotNull();
        then(repository).should().findTop5();
    }

    @Test
    @DisplayName("Filtrar filmes por avaliação máxima.")
    void filtrarPorAvaliacaoMaxima() {

       Double avaliacao = 7.9;

        Filme filme1 = Filme.builder().titulo("Harry Potter and the Prisoner of Azkaban").build();
        Filme filme2 = Filme.builder().titulo("Harry Potter and the Goblet of Fire").build();
        Filme filme3 = Filme.builder().titulo("Harry Potter and the Half-Blood Prince").build();
        Filme filme4 = Filme.builder().titulo("Toy Story 2").build();

        List<Filme> filmes = List.of(filme1, filme2, filme3, filme4);

        given(repository.findByAvaliacaoMaxima(avaliacao))
                .willReturn(filmes);

        List<FilmeDto> resultado = service.filtrarPorAvaliacaoMaxima(avaliacao);

        assertThat(resultado).isNotNull();
        then(repository).should().findByAvaliacaoMaxima(avaliacao);
    }

    @Test
    @DisplayName("Filtrar filmes por avaliação mínima.")
    void filtrarPorAvaliacaoMinima() {

        Double avaliacao = 7.8;

        Filme filme1 = Filme.builder().titulo("Harry Potter and the Prisoner of Azkaban").build();
        Filme filme2 = Filme.builder().titulo("Toy Story 2").build();

        List<Filme> filmes = List.of(filme1, filme2);

        given(repository.findByAvaliacaoMinima(avaliacao))
                .willReturn(filmes);

        List<FilmeDto> resultado = service.filtrarPorAvaliacaoMinima(avaliacao);

        assertThat(resultado).isNotNull();
        then(repository).should().findByAvaliacaoMinima(avaliacao);
    }

    @Test
    @DisplayName("Filtrar filmes por ano máximo.")
    void filtrarPorAnoMaximo() {

        Year ano = Year.of(2004);

        Filme filme1 = Filme.builder().titulo("Harry Potter and the Prisoner of Azkaban").ano(2004).build();
        Filme filme2 = Filme.builder().titulo("Harry Potter and the Goblet of Fire").ano(2005).build();
        Filme filme3 = Filme.builder().titulo("Harry Potter and the Half-Blood Prince").ano(2009).build();
        Filme filme4 = Filme.builder().titulo("Toy Story 2").ano(1999).build();

        List<Filme> filmes = List.of(filme1, filme2, filme3, filme4);

        given(repository.findAll())
                .willReturn(filmes);

        List<FilmeDto> resultado = service.filtrarPorAnoMaximo(ano);

        assertThat(resultado).isNotNull();
        then(repository).should().findAll();
    }

    @Test
    @DisplayName("Filtrar filmes por ano mínimo.")
    void filtrarPorAnoMinimo() {

        Year ano = Year.of(2007);

        Filme filme1 = Filme.builder().titulo("Harry Potter and the Prisoner of Azkaban").ano(2004).build();
        Filme filme2 = Filme.builder().titulo("Harry Potter and the Goblet of Fire").ano(2005).build();
        Filme filme3 = Filme.builder().titulo("Harry Potter and the Half-Blood Prince").ano(2009).build();
        Filme filme4 = Filme.builder().titulo("Toy Story 2").ano(1999).build();

        List<Filme> filmes = List.of(filme1, filme2, filme3, filme4);

        given(repository.findAll())
                .willReturn(filmes);

        List<FilmeDto> resultado = service.filtrarPorAnoMinimo(ano);

        assertThat(resultado).isNotNull();
        then(repository).should().findAll();
    }

    @Test
    @DisplayName("Ver todos os filmes do catálogo.")
    void verTodosFilmes() {

        Filme filme1 = Filme.builder().titulo("Harry Potter and the Prisoner of Azkaban").ano(2004).build();
        Filme filme2 = Filme.builder().titulo("Harry Potter and the Goblet of Fire").ano(2005).build();
        Filme filme3 = Filme.builder().titulo("Harry Potter and the Half-Blood Prince").ano(2009).build();
        Filme filme4 = Filme.builder().titulo("Toy Story 2").ano(1999).build();

        List<Filme> filmes = List.of(filme1, filme2, filme3, filme4);

        given(repository.findAll())
                .willReturn(filmes);


        List<FilmeDto> resultado = service.verTodosFilmes();

        assertThat(resultado).isNotNull();
        then(repository).should().findAll();
    }

    @Test
    @DisplayName("Adicionar filme no Catálogo.")
    void adicionarFilme() {

        String titulo = "Harry Potter and the Sorcerer's Stone";

        FilmeDados filmeDados = FilmeDados.builder()
                .titulo(titulo)
                .genero("Adventure, Family, Fantasy")
                .trama("N/A")
                .build();

        given(repository.existsByTituloIgnoreCase(anyString())).willReturn(false);
        given(restTemplate.getForObject(anyString(), eq(FilmeDados.class))).willReturn(filmeDados);
        given(repository.save(any(Filme.class))).willAnswer(inv -> inv.getArgument(0));

        FilmeDto resultado = service.adicionarFilme(titulo);


        assertThat(resultado).isNotNull();

        then(restTemplate).should().getForObject(anyString(), eq(FilmeDados.class));
        then(repository).should().existsByTituloIgnoreCase(anyString());
        then(repository).should().save(any(Filme.class));
    }

    @Test
    @DisplayName("Deletar algum filme no Catálogo.")
    void deletarFilme() {

        Filme filme = Filme.builder().titulo("Harry Potter and the Prisoner of Azkaban").ano(2004).build();

        given(repository.findByTituloIgnoreCase(anyString())).willReturn(Optional.of(filme));

        String resultado = service.deletarFilme(filmeEspecifico);

        assertThat(resultado).containsIgnoringCase("Foi deletado do catálogo.");

        then(repository).should().delete(filme);
    }

    @Test
    @DisplayName("Deletar todos os filmes do Catálogo.")
    void deletarTodosFilmes() {

        String resultado = service.deletarTodosFilmes();

        assertThat(resultado).isEqualTo("Todos os filmes foram deletados do catálogo.");

        then(repository).should().deleteAll();
    }
}