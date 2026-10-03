package br.Catalogo.de.Filmes.dto;

import br.Catalogo.de.Filmes.dto.seriesData.SerieEpisodios;
import br.Catalogo.de.Filmes.service.Utilitarios;
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
        "data",
        "poster"
})
public record SerieEpisodioDto(String titulo,
                               String data,
                              Integer temporada,
                              Integer episodio,
                              String duracao,
                              String descricao,
                              String poster,
                              String avaliacao) {

    public SerieEpisodioDto (SerieEpisodios serieEpisodios, String descricao) {
        this(serieEpisodios.titulo(), Utilitarios.formatarDataEpisodio(serieEpisodios.data()), serieEpisodios.temporada(),
                serieEpisodios.episodio(), serieEpisodios.duracao(),
                descricao, serieEpisodios.poster(), serieEpisodios.avaliacao());

    }
}
