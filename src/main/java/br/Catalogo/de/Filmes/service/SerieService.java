package br.Catalogo.de.Filmes.service;

import br.Catalogo.de.Filmes.dto.SerieDto;
import br.Catalogo.de.Filmes.dto.SerieEpisodioDto;
import br.Catalogo.de.Filmes.dto.SerieTemporadaDto;
import br.Catalogo.de.Filmes.dto.conteudosDados.ConteudoSearchDados;
import br.Catalogo.de.Filmes.dto.seriesData.SerieDados;
import br.Catalogo.de.Filmes.dto.seriesData.SerieEpisodios;
import br.Catalogo.de.Filmes.dto.seriesData.SerieTemporadas;
import br.Catalogo.de.Filmes.handler.exception.BadRequestException;
import br.Catalogo.de.Filmes.handler.exception.NotFoundException;
import br.Catalogo.de.Filmes.model.Episodio;
import br.Catalogo.de.Filmes.model.Serie;
import br.Catalogo.de.Filmes.model.Temporadas;
import br.Catalogo.de.Filmes.repository.IEpisodioRepository;
import br.Catalogo.de.Filmes.repository.ISerieRepository;
import br.Catalogo.de.Filmes.repository.ITemporadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SerieService {

    @Value("${omdb.api.key}")
    private String apiKey;

    @Value("${omdb.api.url}")
    private String apiUrl;

    private final ISerieRepository serieRepository;
    private final IEpisodioRepository episodioRepository;
    private final ITemporadaRepository temporadaRepository;
    private final RestTemplate restTemplate;
    private final String tipo = "&type=series";

    public ConteudoSearchDados tituloIgual(String serie) {

        try {
            String url = String.format("%s%s&s=%s%s", apiUrl, apiKey, serie.toLowerCase().replace(" ", "+").trim(), tipo);
            ConteudoSearchDados conteudoSearchDados = restTemplate.getForObject(url, ConteudoSearchDados.class);

            if (conteudoSearchDados == null || VerificarCampos.todosCamposNull(conteudoSearchDados)) {
                throw new NotFoundException(String.format("Nenhum dado da série '%s' foi encontrado", serie));
            }

            return conteudoSearchDados;

        }catch (HandlerMethodValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public SerieDto buscarSerie(String serie) {

        Optional<Serie> serieNoBanco = serieRepository.findByTituloIgnoreCase(serie);
        if(serieNoBanco.isPresent()) {return new SerieDto(serieNoBanco.get());}

        try {
            String url = String.format("%s%s&t=%s%s", apiUrl, apiKey, serie.toLowerCase().replace(" ", "+").trim(), tipo);
            SerieDados serieDados = restTemplate.getForObject(url, SerieDados.class);

            if(serieDados == null || VerificarCampos.todosCamposNull(serieDados)) {
                throw new NotFoundException(String.format("Nenhum dado da série '%s' foi encontrado", serie));
            }

            return new SerieDto(serieDados);

        } catch (HandlerMethodValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public SerieTemporadaDto pegarTemporada(String serie, Integer temporada) {

        SerieDto serieDto = buscarSerie(serie);

        try {
            String url = String.format("%s%s&t=%s&season=%d%s", apiUrl, apiKey, serieDto.titulo(), temporada, tipo);
            SerieTemporadas serieTemporadas = restTemplate.getForObject(url, SerieTemporadas.class);

            if (serieTemporadas == null || VerificarCampos.todosCamposNull(serieTemporadas)) {
                throw new NotFoundException(String.format("Nenhuma temporada da série %s foi encontrada", serie));
            }

            SerieTemporadaDto serieTemporadaDto = new SerieTemporadaDto(serieTemporadas);
            List<Temporadas> temporadasList = new ArrayList<>();
            serieTemporadaDto.episodios().forEach(x -> {
                temporadasList.add(new Temporadas(serieTemporadaDto.tituloSerie(), serieTemporadaDto.temporada(), serieTemporadaDto.anoTemporada(), x));
            });


            temporadaRepository.saveAll(temporadasList);
            return serieTemporadaDto;

        }catch (RestClientException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public SerieEpisodioDto pegarEpisodio(String serie, Integer temporada, Integer episodio) {

        SerieTemporadaDto serieTemporadaDto = pegarTemporada(serie, temporada);

        try {
            String url = String.format("%s%s&t=%s&season=%d&episode=%d%s", apiUrl, apiKey, serieTemporadaDto.tituloSerie(), temporada, episodio, tipo);
            SerieEpisodios serieEpisodios = restTemplate.getForObject(url, SerieEpisodios.class);

            if(serieEpisodios == null || VerificarCampos.todosCamposNull(serieEpisodios)) {
                throw new NotFoundException(String.format("Nenhum episódio foi encontrado para a série '%s' na temporada '%d.", serie, episodio));
            }

            SerieEpisodioDto serieEpisodioDto = new SerieEpisodioDto(serieEpisodios);
            episodioRepository.save(new Episodio(serieTemporadaDto.tituloSerie(), serieEpisodioDto));

            return serieEpisodioDto;

        } catch (RestClientException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public List<SerieDto> verTodasSeries() {
        return serieRepository.findAll().stream().map(SerieDto::new).toList();
    }

    public SerieDto adicionarSerie(String serie) {

        Optional<Serie> serieOptional = serieRepository.findByTituloIgnoreCase(serie);

        if(serieOptional.isPresent()) {
            throw new BadRequestException(String.format("A série %s ja foi adicionada.", serieOptional));
        }

        SerieDto serieDto = buscarSerie(serie);

        serieRepository.save(new Serie(serieDto));
        return serieDto;
    }

    public String deletarSerie(String serie) {

        Serie serieParaDeletar = serieRepository.findByTituloIgnoreCase(serie)
                .orElseThrow(() -> new NotFoundException(String.format("A série %s não está no catálogo.", serie)));

        serieRepository.delete(serieParaDeletar);
        return String.format("A série %s foi deletada do catálogo.", serie);
    }

    public String deletarTodasSeries() {
        serieRepository.deleteAll();
        return "Todos as séries foram deletadas do catálogo.";
    }
}
