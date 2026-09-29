package br.Catalogo.de.Filmes.dto.seriesData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record SerieDados(@JsonProperty("Title") String titulo,
                         @JsonProperty("Year") String ano,
                         @JsonProperty("Genre") String genero,
                         @JsonProperty("Director") String diretor,
                         @JsonProperty("Actors") String atores,
                         @JsonProperty("Plot") String trama,
                         @JsonProperty("Poster") String poster,
                         @JsonProperty("imdbRating") String avaliacao,
                         @JsonProperty("totalSeasons") Integer temporadas) {
}
