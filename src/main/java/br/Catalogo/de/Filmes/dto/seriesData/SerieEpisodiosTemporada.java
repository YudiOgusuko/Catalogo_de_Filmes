package br.Catalogo.de.Filmes.dto.seriesData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record SerieEpisodiosTemporada(@JsonProperty("Title") String titulo,
                                      @JsonProperty("Released") String ano,
                                      @JsonProperty("Episode") Integer episodio,
                                      @JsonProperty("imdbRating") Double avaliacao) {
}
