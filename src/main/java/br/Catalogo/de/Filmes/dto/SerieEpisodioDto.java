package br.Catalogo.de.Filmes.dto;

import br.Catalogo.de.Filmes.dto.seriesData.SerieEpisodios;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

@Builder
@JsonPropertyOrder({
        "titulo",
        "duracao",
        "temporada",
        "episodio",
        "descricao",
        "avaliacao",
        "ano",
        "poster"
})
public record SerieEpisodioDto(String titulo,
                              String ano,
                              Integer temporada,
                              Integer episodio,
                              String duracao,
                              String descricao,
                              String poster,
                              Double avaliacao) {

    public SerieEpisodioDto (SerieEpisodios serieEpisodios) {
        this(serieEpisodios.titulo(), serieEpisodios.ano(), serieEpisodios.temporada(),
                serieEpisodios.episodio(), serieEpisodios.duracao(),
                serieEpisodios.descricao(), serieEpisodios.poster(), serieEpisodios.avaliacao());

    }
}
