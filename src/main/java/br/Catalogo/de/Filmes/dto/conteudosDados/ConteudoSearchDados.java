package br.Catalogo.de.Filmes.dto.conteudosDados;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record ConteudoSearchDados(@JsonProperty("Search") List<ConteudoDados> conteudoDados) {
}
