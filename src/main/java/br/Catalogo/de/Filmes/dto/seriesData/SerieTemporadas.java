package br.Catalogo.de.Filmes.dto.seriesData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record SerieTemporadas(@JsonProperty("Season") Integer temporada,
                              @JsonProperty("Episodes") List<SerieEpisodiosTemporada> serieEpisodioPorTemporadas) {
}
