package br.Catalogo.de.Filmes.dto;

import br.Catalogo.de.Filmes.dto.seriesData.SerieEpisodiosTemporada;
import br.Catalogo.de.Filmes.dto.seriesData.SerieTemporadas;
import br.Catalogo.de.Filmes.service.Utilitarios;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Builder
@JsonPropertyOrder({
        "titulo",
        "episodio",
        "avaliacao",
        "dataLancamento"
})

public record SerieEpisodiosTemporadaDto(String titulo,
                                         String dataLancamento,
                                         Integer episodio,
                                         String avaliacao) {

    public SerieEpisodiosTemporadaDto(SerieEpisodiosTemporada serieEpisodiosTemporada) {
        this(serieEpisodiosTemporada.titulo(), Utilitarios.formatarDataEpTemporada(serieEpisodiosTemporada.dataLancamento()),
                serieEpisodiosTemporada.episodio(), serieEpisodiosTemporada.avaliacao());
    }
}
