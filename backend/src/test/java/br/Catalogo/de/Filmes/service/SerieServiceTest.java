package br.Catalogo.de.Filmes.service;

import br.Catalogo.de.Filmes.dto.SerieDto;
import br.Catalogo.de.Filmes.dto.SerieEpisodioDto;
import br.Catalogo.de.Filmes.dto.SerieEpisodiosTemporadaDto;
import br.Catalogo.de.Filmes.dto.SerieTemporadaDto;
import br.Catalogo.de.Filmes.dto.conteudosDados.ConteudoDados;
import br.Catalogo.de.Filmes.dto.conteudosDados.ConteudoSearchDados;
import br.Catalogo.de.Filmes.dto.seriesData.SerieDados;
import br.Catalogo.de.Filmes.dto.seriesData.SerieEpisodios;
import br.Catalogo.de.Filmes.dto.seriesData.SerieEpisodiosTemporada;
import br.Catalogo.de.Filmes.dto.seriesData.SerieTemporadas;
import br.Catalogo.de.Filmes.enums.Genero;
import br.Catalogo.de.Filmes.handler.exception.NotFoundException;
import br.Catalogo.de.Filmes.model.Episodio;
import br.Catalogo.de.Filmes.model.Serie;
import br.Catalogo.de.Filmes.repository.ISerieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class SerieServiceTest {

    @InjectMocks
    private SerieService service;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ISerieRepository repository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "apiKey", "${omdb.api.key}");
        ReflectionTestUtils.setField(service, "apiUrl", "${omdb.api.url}");
    }

    private final String serieEspecifica = "Game of Thrones";

    @Test
    @DisplayName("Pegar todas as séries com títulos iguais na API.")
    void tituloIgual() {

        ConteudoDados conteudoDados1 = ConteudoDados.builder().titulo("Game of Thrones").build();
        ConteudoDados conteudoDados2 = ConteudoDados.builder().titulo("Game of Silence").build();
        ConteudoDados conteudoDados3 = ConteudoDados.builder().titulo("The Trust: A Game of Greed").build();
        ConteudoDados conteudoDados4 = ConteudoDados.builder().titulo("Ellen's Game of Games").build();

        List<ConteudoDados> conteudoDadosList = List.of(conteudoDados1, conteudoDados2, conteudoDados3, conteudoDados4);

        ConteudoSearchDados conteudoSearchDados = ConteudoSearchDados.builder().conteudoDados(conteudoDadosList).build();

        given(restTemplate.getForObject(anyString(), eq(ConteudoSearchDados.class)))
                .willReturn(conteudoSearchDados);

        ConteudoSearchDados resutado = service.tituloIgual("Game of");

        assertThat(resutado).isNotNull();
        then(restTemplate).should().getForObject(anyString(), any());
    }

    @Test
    @DisplayName("Buscar série no Banco.")
    void buscarSerie_noBanco() {

        Serie serie = Serie.builder().titulo(serieEspecifica).build();

        given(repository.findByTituloContainingIgnoreCase(anyString()))
                .willReturn(List.of(serie));

        List<SerieDto> resultado = service.buscarSerie(serieEspecifica);

        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("Buscar série na API.")
    void buscarSerie_NaApi() {

        SerieDados serieDados = SerieDados.builder().titulo(serieEspecifica).genero("Ação").build();

        given(repository.findByTituloContainingIgnoreCase(anyString()))
                .willReturn(List.of());

        given(restTemplate.getForObject(anyString(), eq(SerieDados.class)))
                .willReturn(serieDados);

        List<SerieDto> resultado = service.buscarSerie(serieEspecifica);

        assertThat(resultado).isNotNull();
        then(restTemplate).should().getForObject(anyString(), any());
        then(repository).should(never()).findAllByAtoresContainingIgnoreCase(anyString());
    }

    @Test
    @DisplayName("Lançar Exceção ao tentar buscar a série.")
    void buscarSerie_Excecao() {

        given(repository.findByTituloContainingIgnoreCase(anyString()))
                .willReturn(List.of());

        given(restTemplate.getForObject(anyString(), eq(SerieDados.class)))
                .willReturn(null);

        assertThrows(NotFoundException.class, () -> service.buscarSerie(serieEspecifica));

        then(restTemplate).should().getForObject(anyString(), any());
    }

    @Test
    @DisplayName("Buscar séries pelo gênero.")
    void buscarSeriePorGenero() {

        String generoString = "Ação";
        Genero genero = Genero.pegarGenero(generoString);

        Serie serie1 = Serie.builder().titulo("Game of Thrones").build();
        Serie serie2 = Serie.builder().titulo("Prison Break").build();

        List<Serie> serie = List.of(serie1, serie2);

        given(repository.findAllByGenero(genero))
                .willReturn(serie);

        List<SerieDto> resultado = service.buscarSeriePorGenero(generoString);

        assertThat(resultado).isNotNull();
        then(repository).should().findAllByGenero(genero);
    }

    @Test
    @DisplayName("Buscar séries por ator ou atriz.")
    void buscarSeriePorAtor() {

        String atriz = "Peter Dinklage";

        List<Serie> series = List.of(Serie.builder().titulo("Game of Thrones").build());

        given(repository.findAllByAtoresContainingIgnoreCase(atriz))
                .willReturn(series);

        List<SerieDto> resultado = service.buscarSeriePorAtor(atriz);

        assertThat(resultado).isNotNull();
        then(repository).should().findAllByAtoresContainingIgnoreCase(atriz);
    }

    @Test
    @DisplayName("Ver os Top 5 melhores séries do catálogo.")
    void top5Series() {

        given(repository.findTop5())
                .willReturn(List.of(Serie.builder().build()));

        List<SerieDto> resultado = service.top5Series();

        assertThat(resultado).isNotNull();
        then(repository).should().findTop5();
    }

    @Test
    @DisplayName("Ver os Top 5 melhores episódio de uma séries do catálogo.")
    void top5EpisodiosDaSerie() {

        Episodio episodio1 = Episodio.builder().tituloEpisodio("Battle of the Bastards").build();
        Episodio episodio2 = Episodio.builder().tituloEpisodio("The Winds of Winter").build();
        Episodio episodio3 = Episodio.builder().tituloEpisodio("The Rains of Castamere").build();
        Episodio episodio4 = Episodio.builder().tituloEpisodio("Hardhome").build();
        Episodio episodio5 = Episodio.builder().tituloEpisodio("The Lion and the Rose").build();

        List<Episodio> episodios = List.of(episodio1, episodio2, episodio3, episodio4, episodio5);

        given(repository.findByTituloIgnoreCase(serieEspecifica))
                .willReturn(Optional.of(Serie.builder().build()));

        given(repository.findTop5Episodios(any(), eq(PageRequest.of(0, 5))))
                .willReturn(episodios);

        List<SerieEpisodiosTemporadaDto> resultado = service.top5EpisodiosDaSerie("Game of Thrones");

        assertThat(resultado).isNotNull();
        then(repository).should().findByTituloIgnoreCase(any());
        then(repository).should().findTop5Episodios(any(), any());
    }

    @Test
    @DisplayName("Buscar temporada de alguma série.")
    void buscarTemporada() {

        Integer temporada = 6;

        SerieEpisodiosTemporada ep1 = SerieEpisodiosTemporada.builder().titulo("The Red Woman").build();
        SerieEpisodiosTemporada ep2 = SerieEpisodiosTemporada.builder().titulo("Home").build();
        SerieEpisodiosTemporada ep3 = SerieEpisodiosTemporada.builder().titulo("Oathbreaker").build();
        SerieEpisodiosTemporada ep4 = SerieEpisodiosTemporada.builder().titulo("Book of the Stranger").build();
        SerieEpisodiosTemporada ep5 = SerieEpisodiosTemporada.builder().titulo("The Door").build();
        SerieEpisodiosTemporada ep6 = SerieEpisodiosTemporada.builder().titulo("Blood of My Blood").build();
        SerieEpisodiosTemporada ep7 = SerieEpisodiosTemporada.builder().titulo("The Broken Man").build();
        SerieEpisodiosTemporada ep8 = SerieEpisodiosTemporada.builder().titulo("No One").build();
        SerieEpisodiosTemporada ep9 = SerieEpisodiosTemporada.builder().titulo("Battle of the Bastards").build();
        SerieEpisodiosTemporada ep10 = SerieEpisodiosTemporada.builder().titulo("The Winds of Winter").build();

        SerieTemporadas serieTemporadas = SerieTemporadas.builder().temporada(temporada)
                .serieEpisodioPorTemporadas(List.of(ep1, ep2, ep3, ep4, ep5, ep6, ep7, ep8, ep9, ep10))
                .build();

        given(repository.findByTituloContainingIgnoreCase(anyString())).willReturn(List.of(Serie.builder().build()));
        given(restTemplate.getForObject(anyString(), eq(SerieTemporadas.class))).willReturn(serieTemporadas);

        SerieTemporadaDto resultado = service.buscarTemporada(serieEspecifica, temporada);

        assertThat(resultado).isNotNull();
        then(repository).should().findByTituloContainingIgnoreCase(any());
        then(restTemplate).should().getForObject(anyString(), any());
    }

    @Test
    @DisplayName("Buscar episódio de temporada de alguma série no Banco.")
    void buscarEpisodio() {

        Integer temporada = 6;
        Integer episodio = 9;

        SerieEpisodios serieEpisodios = SerieEpisodios.builder()
                .titulo("Battle of the Bastards")
                .temporada(6)
                .episodio(9)
                .avaliacao("9.9")
                .descricao("N/A")
                .build();

        given(repository.findByTituloContainingIgnoreCase(anyString())).willReturn(List.of(Serie.builder().build()));
        given(restTemplate.getForObject(anyString(), eq(SerieEpisodios.class))).willReturn(serieEpisodios);

        SerieEpisodioDto resultado = service.buscarEpisodio(serieEspecifica, temporada, episodio);

        assertThat(resultado).isNotNull();
        then(repository).should().findByTituloContainingIgnoreCase(any());
        then(restTemplate).should().getForObject(anyString(), any());
    }

    @Test
    @DisplayName("Filtrar séries por temporada máxima.")
    void filtrarPorTemporadaMaxima() {

        Integer temporada = 5;

        given(repository.findByTemporadasLessThanEqual(temporada))
                .willReturn(List.of(Serie.builder().build()));

        List<SerieDto> resultado = service.filtrarPorTemporadaMaxima(temporada);

        assertThat(resultado).isNotNull();
        then(repository).should().findByTemporadasLessThanEqual(any());
    }

    @Test
    @DisplayName("Filtrar séries por temporada mínima.")
    void filtrarPorTemporadaMinima() {

        Integer temporada = 6;

        given(repository.findByTemporadasGreaterThanEqual(temporada))
                .willReturn(List.of(Serie.builder().build()));

        List<SerieDto> resultado = service.filtrarPorTemporadaMinima(temporada);

        assertThat(resultado).isNotNull();
        then(repository).should().findByTemporadasGreaterThanEqual(any());

    }

    @Test
    @DisplayName("Filtrar séries por avaliação máxima.")
    void filtrarPorAvaliacaoMaxima() {

        Double avaliacao = 9.0;

        Serie serie1 = Serie.builder().titulo("Dexter").build();
        Serie serie2 = Serie.builder().titulo("Prison Break").build();

        List<Serie> series = List.of(serie1, serie2);

        given(repository.findByAvaliacaoMaxima(avaliacao))
                .willReturn(series);

        List<SerieDto> resultado = service.filtrarPorAvaliacaoMaxima(avaliacao);

        assertThat(resultado).isNotNull();
        then(repository).should().findByAvaliacaoMaxima(avaliacao);
    }

    @Test
    @DisplayName("Filtrar séries por avaliação mínima.")
    void filtrarPorAvaliacaoMinima() {

        Double avaliacao = 9.1;

        Serie serie1 = Serie.builder().titulo("The Sopranos").build();
        Serie serie2 = Serie.builder().titulo("The Wire").build();
        Serie serie3 = Serie.builder().titulo("Game of Thrones").build();
        Serie serie4 = Serie.builder().titulo("Breaking Bad").build();

        List<Serie> series = List.of(serie1, serie2, serie3, serie4);

        given(repository.findByAvaliacaoMinima(avaliacao))
                .willReturn(series);

        List<SerieDto> resultado = service.filtrarPorAvaliacaoMinima(avaliacao);

        assertThat(resultado).isNotNull();
        then(repository).should().findByAvaliacaoMinima(avaliacao);
    }

    @Test
    @DisplayName("Filtrar séries por ano máximo.")
    void filtrarPorAnoMaximo() {

        Year ano = Year.of(2005);

        Serie serie1 = Serie.builder().titulo("The Sopranos").ano("1999–2007").build();
        Serie serie2 = Serie.builder().titulo("The Wire").ano("2002–2008").build();
        Serie serie3 = Serie.builder().titulo("Prison Break").ano("2005–2017").build();

        List<Serie> filmes = List.of(serie1, serie2, serie3);

        given(repository.findAll())
                .willReturn(filmes);

        List<SerieDto> resultado = service.filtrarPorAnoMaximo(ano);

        assertThat(resultado).isNotNull();
        then(repository).should().findAll();
    }

    @Test
    @DisplayName("Filtrar séries por ano mínimo.")
    void filtrarPorAnoMinimo() {

        Year ano = Year.of(2010);

        Serie serie1 = Serie.builder().titulo("Game of Thrones").ano("2011–2019").build();
        Serie serie2 = Serie.builder().titulo("Breaking Bad").ano("2008–2013").build();
        Serie serie3 = Serie.builder().titulo("Dexter").ano("2006–2013").build();
        Serie serie4 = Serie.builder().titulo("Prison Break").ano("2005–2017").build();
        Serie serie5 = Serie.builder().titulo("Better Call Saul").ano("2015–2022").build();

        List<Serie> series = List.of(serie1, serie2, serie3, serie4, serie5);

        given(repository.findAll())
                .willReturn(series);

        List<SerieDto> resultado = service.filtrarPorAnoMinimo(ano);

        assertThat(resultado).isNotNull();
        then(repository).should().findAll();
    }

    @Test
    @DisplayName("Ver todas as séries do catálogo.")
    void verTodasSeries() {

        Serie serie1 = Serie.builder().titulo("The Sopranos").build();
        Serie serie2 = Serie.builder().titulo("The Wire").build();
        Serie serie3 = Serie.builder().titulo("Game of Thrones").build();
        Serie serie4 = Serie.builder().titulo("Breaking Bad").build();

        List<Serie> series = List.of(serie1, serie2, serie3, serie4);

        given(repository.findAll())
                .willReturn(series);


        List<SerieDto> resultado = service.verTodasSeries();

        assertThat(resultado).isNotNull();
        then(repository).should().findAll();
    }

    @Test
    @DisplayName("Adicionar série no Catálogo.")
    void adicionarSerie() {

        SerieDados serieDados = SerieDados.builder()
                .titulo(serieEspecifica)
                .ano("2011–2019")
                .genero("Action, Adventure, Drama")
                .avaliacao("9.2")
                .temporadas(2)
                .build();


        SerieEpisodiosTemporada ep1 = SerieEpisodiosTemporada.builder()
                .titulo("Winter Is Coming")
                .episodio(1)
                .avaliacao("9.0")
                .dataLancamento("2011-04-17")
                .build();

        SerieTemporadas temporadas = SerieTemporadas.builder()
                .temporada(1)
                .serieEpisodioPorTemporadas(List.of(ep1))
                .build();

        given(repository.existsByTituloIgnoreCase(anyString())).willReturn(false);
        given(restTemplate.getForObject(anyString(), eq(SerieDados.class))).willReturn(serieDados);
        given(restTemplate.getForObject(anyString(), eq(SerieTemporadas.class))).willReturn(temporadas);
        given(repository.save(any(Serie.class))).willAnswer(inv -> inv.getArgument(0));

        SerieDto resultado = service.adicionarSerie(serieEspecifica);

        assertThat(resultado).isNotNull();

        then(restTemplate).should().getForObject(anyString(), eq(SerieDados.class));
        then(repository).should().existsByTituloIgnoreCase(anyString());
        then(repository).should().save(any(Serie.class));
    }

    @Test
    @DisplayName("Deletar alguma série no Catálogo.")
    void deletarSerie() {

        Serie serie = Serie.builder().titulo("Dexter").build();

        given(repository.findByTituloIgnoreCase(anyString())).willReturn(Optional.of(serie));

        String resultado = service.deletarSerie(serieEspecifica);

        assertThat(resultado).containsIgnoringCase("Foi deletada do catálogo.");

        then(repository).should().delete(serie);
    }

    @Test
    @DisplayName("Deletar todas as séries do Catálogo.")
    void deletarTodasSeries() {

        String resultado = service.deletarTodasSeries();

        assertThat(resultado).isEqualTo("Todos as séries foram deletadas do catálogo.");

        then(repository).should().deleteAll();
    }
}