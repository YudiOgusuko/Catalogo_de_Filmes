package br.Catalogo.de.Filmes.service;

import java.time.Year;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import br.Catalogo.de.Filmes.dto.SerieDto;
import br.Catalogo.de.Filmes.dto.SerieEpisodioDto;
import br.Catalogo.de.Filmes.dto.SerieEpisodiosTemporadaDto;
import br.Catalogo.de.Filmes.dto.SerieTemporadaDto;
import br.Catalogo.de.Filmes.dto.conteudosDados.ConteudoSearchDados;
import br.Catalogo.de.Filmes.dto.seriesData.SerieDados;
import br.Catalogo.de.Filmes.dto.seriesData.SerieEpisodios;
import br.Catalogo.de.Filmes.dto.seriesData.SerieTemporadas;
import br.Catalogo.de.Filmes.enums.Genero;
import br.Catalogo.de.Filmes.handler.exception.BadRequestException;
import br.Catalogo.de.Filmes.handler.exception.NotFoundException;
import br.Catalogo.de.Filmes.model.Episodio;
import br.Catalogo.de.Filmes.model.Serie;
import br.Catalogo.de.Filmes.model.Temporadas;
import br.Catalogo.de.Filmes.repository.ISerieRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SerieService {

    @Value("${omdb.api.key}")
    private String apiKey;

    @Value("${omdb.api.url}")
    private String apiUrl;

    private final ISerieRepository serieRepository;
    private final RestTemplate restTemplate;
    private final String tipo = "&type=series";

    public ConteudoSearchDados tituloIgual(String tituloSerie) {

        tituloSerie = formatarString(tituloSerie);

        try {
            String url = String.format("%s%s&s=%s%s", apiUrl, apiKey, formatarString(tituloSerie), tipo);
            ConteudoSearchDados conteudoSearchDados = restTemplate.getForObject(url, ConteudoSearchDados.class);

            if (conteudoSearchDados == null || Utilitarios.verificarCamposNull(conteudoSearchDados)) {
                throw new NotFoundException(String.format("Nenhum dado da série '%s' foi encontrado", tituloSerie));
            }

            return conteudoSearchDados;

        }catch (HandlerMethodValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public List<SerieDto> buscarSerie(String tituloSerie) {

        tituloSerie = formatarString(tituloSerie);

        List<SerieDto> serieNoBanco = serieRepository.findByTituloContainingIgnoreCase(tituloSerie).stream().map(SerieDto::new).toList();
        if(!serieNoBanco.isEmpty()) {
            return serieNoBanco;
        }
        return List.of(buscarSerieNaApi(tituloSerie));
    }

    private SerieDto buscarSerieNaApi(String tituloSerie) {

        String url = String.format("%s%s&t=%s%s", apiUrl, apiKey, tituloSerie, tipo);
        SerieDados serieDados = restTemplate.getForObject(url, SerieDados.class);

        if(serieDados == null || Utilitarios.verificarCamposNull(serieDados)) {
            throw new NotFoundException(String.format("Nenhum dado da série '%s' foi encontrado", tituloSerie));
        }

        String genero = serieDados.genero().split(",")[0].trim();

        return new SerieDto(serieDados, Genero.pegarStringGenero(genero));
    }

    public List<SerieDto> buscarSeriePorGenero(String genero) {
        return serieRepository.findAllByGenero(Genero.pegarGenero(genero)).stream().map(SerieDto::new).toList();
    }

    public List<SerieDto> buscarSeriePorAtor(String ator) {
        return serieRepository.findAllByAtoresContainingIgnoreCase(ator).stream().map(SerieDto::new).toList();
    }

    public List<SerieDto> top5Series() {
        return serieRepository.findTop5().stream().map(SerieDto::new).toList();
    }

    public List<SerieEpisodiosTemporadaDto> top5EpisodiosDaSerie(String tituloSerie) {
        Serie serie = serieRepository.findByTituloIgnoreCase(tituloSerie)
                .orElseThrow(() -> new NotFoundException(String.format("A série '%s' não está no catálogo.", tituloSerie)));

        return serieRepository.findTop5Episodios(serie.getId(), PageRequest.of(0, 5))
                .stream()
                .map(SerieEpisodiosTemporadaDto::new)
                .toList();
    }

    public SerieTemporadaDto buscarTemporada(String tituloSerie, Integer temporada) {
        List<SerieDto> serieList = buscarSerie(tituloSerie);
        SerieDto serie = serieList.get(0);
        return buscarTemporadaNaApi(serie.titulo(), temporada);
    }

    private SerieTemporadaDto buscarTemporadaNaApi(String tituloSerie, Integer temporada) {
        try {

            String url = String.format("%s%s&t=%s&season=%d%s", apiUrl, apiKey, tituloSerie, temporada, tipo);
            SerieTemporadas serieTemporadas = restTemplate.getForObject(url, SerieTemporadas.class);

            if (serieTemporadas == null || Utilitarios.verificarCamposNull(serieTemporadas)) {
                throw new NotFoundException(String.format("Temporada %d da série %s não foi encontrada", temporada, tituloSerie));
            }

            return new SerieTemporadaDto(serieTemporadas);

        }catch (RestClientException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public SerieEpisodioDto buscarEpisodio(String tituloSerie, Integer temporada, Integer episodio) {
        List<SerieDto> serieList = buscarSerie(tituloSerie);
        SerieDto serie = serieList.get(0);
        return buscarEpisodioNaApi(serie.titulo(), temporada, episodio);
    }

    private SerieEpisodioDto buscarEpisodioNaApi(String tituloSerie, Integer temporada, Integer episodio) {
        try {

            String url = String.format("%s%s&t=%s&season=%d&episode=%d%s", apiUrl, apiKey, tituloSerie, temporada, episodio, tipo);
            SerieEpisodios serieEpisodios = restTemplate.getForObject(url, SerieEpisodios.class);

            if(serieEpisodios == null || Utilitarios.verificarCamposNull(serieEpisodios)) {
                throw new NotFoundException(String.format("Nenhum episódio foi encontrado para a série '%s' na temporada '%d'.", tituloSerie, episodio));
            }

            String descricao = "N/A";
            if(!serieEpisodios.descricao().equalsIgnoreCase("N/A")) {
                descricao = IATraducao.traduzir(
                        Map.of("descricao", serieEpisodios.descricao())
                ).get("descricao");
            }

            return new SerieEpisodioDto(serieEpisodios, descricao);

        } catch (RestClientException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public List<SerieDto> filtrarPorTemporadaMaxima(Integer temporada) {
        return serieRepository.findByTemporadasLessThanEqual(temporada).stream().map(SerieDto::new).toList();
    }

    public List<SerieDto> filtrarPorTemporadaMinima(Integer temporada) {
        return serieRepository.findByTemporadasGreaterThanEqual(temporada).stream().map(SerieDto::new).toList();
    }

    public List<SerieDto> filtrarPorAvaliacaoMaxima(Double avaliacao) {
        return serieRepository.findByAvaliacaoMaxima(avaliacao).stream().map(SerieDto::new).toList();
    }

    public List<SerieDto> filtrarPorAvaliacaoMinima(Double avaliacao) {
        return serieRepository.findByAvaliacaoMinima(avaliacao).stream().map(SerieDto::new).toList();
    }

    public List<SerieDto> filtrarPorAnoMaximo(Year anoMaximo) {
        return serieRepository.findAll().stream()
                .filter(serie -> {
                    String ano = serie.getAno();
                    if (ano == null || ano.isBlank()) return false;

                    if (ano.length() >= 9 && ano.contains("-")) {
                        Year anoInicio = Year.parse(ano.substring(0, 4));
                        return anoInicio.equals(anoMaximo) || anoInicio.isBefore(anoMaximo);
                    }
                    else {
                        Year anoUnico = Year.parse(ano.substring(0, Math.min(ano.length(), 4)));
                        return anoUnico.equals(anoMaximo) || anoUnico.isBefore(anoMaximo);
                    }
                })
                .map(SerieDto::new)
                .toList();
    }

    public List<SerieDto> filtrarPorAnoMinimo(Year anoMinimo) {
        return serieRepository.findAll().stream()
                .filter(serie -> {
                    String ano = serie.getAno();
                    if (ano == null || ano.isBlank()) return false;

                    if (ano.length() >= 9 && ano.contains("-")) {
                        Year anoFim = Year.parse(ano.substring(5, 9));
                        return anoFim.equals(anoMinimo) || anoFim.isAfter(anoMinimo);
                    }
                    else {
                        Year anoUnico = Year.parse(ano.substring(0, Math.min(ano.length(), 4)));
                        return anoUnico.equals(anoMinimo) || anoUnico.isAfter(anoMinimo);
                    }
                })
                .map(SerieDto::new)
                .toList();
    }

    public List<SerieDto> verTodasSeries() {
        return serieRepository.findAll().stream().map(SerieDto::new).toList();
    }

    @Transactional
    public SerieDto adicionarSerie(String tituloSerie) {

        tituloSerie = formatarString(tituloSerie);

        if(serieRepository.existsByTituloIgnoreCase(tituloSerie)){
            throw new BadRequestException(String.format("A série '%s' ja foi adicionada.", tituloSerie));
        }

        try {
            SerieDto serieDto = buscarSerieNaApi(tituloSerie);
            Serie serie = new Serie(serieDto, Genero.pegarGenero(serieDto.genero()));

            for (int i = 1; i <= serie.getTemporadas(); i++) {
                SerieTemporadaDto temporadaDto = buscarTemporadaNaApi(serie.getTitulo(), i);

                if (temporadaDto.episodios() != null) {
                    Temporadas temporadas = new Temporadas(temporadaDto);
                    serie.salvarTemporadas(temporadas);

                    for(SerieEpisodiosTemporadaDto ep : temporadaDto.episodios()) {
                        serie.salvarEpisodios(temporadas, new Episodio(ep));
                    }
                }
            }

            String trama = serie.getTrama();
            if(trama != null && !trama.equalsIgnoreCase("N/A")) {
                trama = IATraducao.traduzir(Map.of("trama", serie.getTrama())).get("trama");
                serie.setTrama(trama);
            }

            serieRepository.save(serie);
            return serieDto;

        } catch (RestClientException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public String deletarSerie(String tituloSerie) {

        String serieNome = formatarString(tituloSerie);
        Serie serie = serieRepository.findByTituloIgnoreCase(tituloSerie)
                        .orElseThrow(() -> new NotFoundException(String.format("A série '%s' não esta no catálogo.", serieNome)));

        serieRepository.delete(serie);
        return String.format("A série %s foi deletada do catálogo.", tituloSerie);
    }

    public String deletarTodasSeries() {
        serieRepository.deleteAll();
        return "Todos as séries foram deletadas do catálogo.";
    }

    private String formatarString(String txt) {
        if(txt == null || txt.isBlank()) {
            return "";
        }

        String[] palavra = txt.trim().split("\\s+");
        StringBuilder stringBuilder = new StringBuilder();

        for(String p : palavra) {
            if(!p.isBlank()) {
                String formatar = p.substring(0, 1).toUpperCase()
                        + p.substring(1).toLowerCase();

                stringBuilder.append(formatar).append(" ");
            }
        }

        return stringBuilder.toString().trim();
    }
}