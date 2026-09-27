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
import br.Catalogo.de.Filmes.repository.ITemporadaRepository;
import br.Catalogo.de.Filmes.repository.ISerieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

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
    private final ITemporadaRepository episodioRepository;
    private final RestTemplate restTemplate;
    private final String tipo = "&type=series";

    public ConteudoSearchDados tituloIgual(String tituloSerie) {

        tituloSerie = formatarString(tituloSerie);

        try {
            String url = String.format("%s%s&s=%s%s", apiUrl, apiKey, formatarString(tituloSerie), tipo);
            ConteudoSearchDados conteudoSearchDados = restTemplate.getForObject(url, ConteudoSearchDados.class);

            if (conteudoSearchDados == null || VerificarCampos.todosCamposNull(conteudoSearchDados)) {
                throw new NotFoundException(String.format("Nenhum dado da série '%s' foi encontrado", tituloSerie));
            }

            return conteudoSearchDados;

        }catch (HandlerMethodValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public SerieDto buscarSerie(String tituloSerie) {

        tituloSerie = formatarString(tituloSerie);

        Optional<Serie> serieNoBanco = serieRepository.findByTituloIgnoreCase(tituloSerie);
        if(serieNoBanco.isPresent()) {return new SerieDto(serieNoBanco.get());}

        try {
            String url = String.format("%s%s&t=%s%s", apiUrl, apiKey, tituloSerie, tipo);
            SerieDados serieDados = restTemplate.getForObject(url, SerieDados.class);

            if(serieDados == null || VerificarCampos.todosCamposNull(serieDados)) {
                throw new NotFoundException(String.format("Nenhum dado da série '%s' foi encontrado", tituloSerie));
            }

            return new SerieDto(serieDados);

        } catch (HandlerMethodValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public SerieTemporadaDto pegarTemporada(String tituloSerie, Integer temporada) {

        tituloSerie = formatarString(tituloSerie);

        SerieDto serieDto = buscarSerie(tituloSerie);

        try {
            String url = String.format("%s%s&t=%s&season=%d%s", apiUrl, apiKey, serieDto.titulo(), temporada, tipo);
            SerieTemporadas serieTemporadas = restTemplate.getForObject(url, SerieTemporadas.class);

            if (serieTemporadas == null || VerificarCampos.todosCamposNull(serieTemporadas)) {
                throw new NotFoundException(String.format("Nenhuma temporada da série %s foi encontrada", tituloSerie));
            }

            SerieTemporadaDto serieTemporadaDto = new SerieTemporadaDto(serieTemporadas);

            Serie serieBanco = serieRepository.findByTituloIgnoreCase(tituloSerie)
                    .orElseThrow(() -> new NotFoundException("Nenhuma série foi encontrada."));

            serieTemporadaDto.episodios().forEach(x ->
                    serieBanco.buscarTemporada(new Temporadas(serieTemporadaDto, x)));

            serieRepository.save(serieBanco);

            return serieTemporadaDto;

        }catch (RestClientException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public SerieEpisodioDto pegarEpisodio(String tituloSerie, Integer temporada, Integer episodio) {

        tituloSerie = formatarString(tituloSerie);

        Serie serieBanco = serieRepository.findByTituloIgnoreCase(tituloSerie)
                .orElseThrow(() -> new NotFoundException("Nenhuma série foi encontrada."));

        try {
            String url = String.format("%s%s&t=%s&season=%d&episode=%d%s", apiUrl, apiKey, tituloSerie, temporada, episodio, tipo);
            SerieEpisodios serieEpisodios = restTemplate.getForObject(url, SerieEpisodios.class);

            if(serieEpisodios == null || VerificarCampos.todosCamposNull(serieEpisodios)) {
                throw new NotFoundException(String.format("Nenhum episódio foi encontrado para a série '%s' na temporada '%d.", tituloSerie, episodio));
            }

            SerieEpisodioDto serieEpisodioDto = new SerieEpisodioDto(serieEpisodios);

            serieBanco.buscarEpisodio(temporada, new Episodio(serieEpisodioDto));

            serieRepository.save(serieBanco);

            return serieEpisodioDto;

        } catch (RestClientException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public List<SerieDto> verTodasSeries() {
        return serieRepository.findAll().stream().map(SerieDto::new).toList();
    }

    public SerieDto adicionarSerie(String tituloSerie) {

        tituloSerie = formatarString(tituloSerie);

        Optional<Serie> serieOptional = serieRepository.findByTituloIgnoreCase(tituloSerie);

        if(serieOptional.isPresent()) {
            throw new BadRequestException(String.format("A série %s ja foi adicionada.", serieOptional));
        }

        SerieDto serieDto = buscarSerie(tituloSerie);

        serieRepository.save(new Serie(serieDto));
        return serieDto;
    }

    public String deletarSerie(String tituloSerie) {

        tituloSerie = formatarString(tituloSerie);

        Serie serieParaDeletar = serieRepository.findByTituloIgnoreCase(tituloSerie)
                .orElseThrow(() -> new NotFoundException("A série informada não está no catálogo."));

        serieRepository.delete(serieParaDeletar);
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
