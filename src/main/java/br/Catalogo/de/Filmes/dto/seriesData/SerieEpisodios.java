package br.Catalogo.de.Filmes.dto.seriesData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record SerieEpisodios(@JsonProperty("Title") String titulo,
                             @JsonProperty("Released") String ano,
                             @JsonProperty("Season") Integer temporada,
                             @JsonProperty("Episode") Integer episodio,
                             @JsonProperty("Runtime") String duracao,
                             @JsonProperty("Plot") String descricao,
                             @JsonProperty("Poster") String poster,
                             @JsonProperty("imdbRating") String avaliacao) {
}
