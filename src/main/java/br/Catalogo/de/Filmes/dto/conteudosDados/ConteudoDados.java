package br.Catalogo.de.Filmes.dto.conteudosDados;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConteudoDados(@JsonProperty("Title") String titulo,
                            @JsonProperty("Year") String ano) {
}
