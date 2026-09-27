package br.Catalogo.de.Filmes.dto;

import br.Catalogo.de.Filmes.dto.seriesData.SerieEpisodiosTemporada;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

@Builder
@JsonPropertyOrder({
        "titulo",
        "episodio",
        "avaliacao",
        "anoEpisodio"
})

public record SerieEpisodiosTemporadaDto(String titulo,
                                         String anoEpisodio,
                                         Integer episodio,
                                         Double avaliacao) {

    public SerieEpisodiosTemporadaDto(SerieEpisodiosTemporada serieEpisodiosTemporada) {
        this(serieEpisodiosTemporada.titulo(), serieEpisodiosTemporada.ano(),
                serieEpisodiosTemporada.episodio(), serieEpisodiosTemporada.avaliacao());
    }
}
