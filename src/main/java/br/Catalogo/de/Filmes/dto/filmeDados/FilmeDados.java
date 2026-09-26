package br.Catalogo.de.Filmes.dto.filmeDados;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record FilmeDados(@JsonProperty("Title") String titulo,
                         @JsonProperty("Year") Integer ano,
                         @JsonProperty("Runtime") String duracao,
                         @JsonProperty("Genre") String genero,
                         @JsonProperty("Director") String diretor,
                         @JsonProperty("Actors") String atores,
                         @JsonProperty("Plot") String trama,
                         @JsonProperty("Poster") String poster,
                         @JsonProperty("imdbRating") Double avaliacao) {
}
