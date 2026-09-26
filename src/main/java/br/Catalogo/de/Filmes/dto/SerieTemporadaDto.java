package br.Catalogo.de.Filmes.dto;

import br.Catalogo.de.Filmes.dto.seriesData.SerieTemporadas;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

import java.util.List;

@Builder
@JsonPropertyOrder({
        "tituloSerie",
        "temporada",
        "anoTemporada",
        "episodios"
})
public record SerieTemporadaDto(String tituloSerie,
                                Integer temporada,
                                Integer anoTemporada,
                                List<SerieEpisodiosTemporadaDto> episodios) {

    public SerieTemporadaDto(SerieTemporadas serieTemporadas) {
        this(serieTemporadas.titulo(),
             serieTemporadas.temporada(),
             Integer.parseInt(serieTemporadas.serieEpisodioPorTemporadas().get(0).ano().substring(0, 4)),
              serieTemporadas.serieEpisodioPorTemporadas().stream().map(SerieEpisodiosTemporadaDto::new).toList());
    }
}
