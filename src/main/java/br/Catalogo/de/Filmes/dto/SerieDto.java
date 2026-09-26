package br.Catalogo.de.Filmes.dto;

import br.Catalogo.de.Filmes.dto.seriesData.SerieDados;
import br.Catalogo.de.Filmes.model.Serie;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

@Builder
@JsonPropertyOrder({
        "titulo",
        "ano",
        "genero",
        "diretor",
        "atores",
        "trama",
        "poster",
        "avaliacao",
        "temporadas",

})
public record SerieDto(String titulo,
                       String ano,
                       String genero,
                       String diretor,
                       String atores,
                       String trama,
                       String poster,
                       Double avaliacao,
                       Integer temporadas) {

    public SerieDto(SerieDados serieDados) {
        this(serieDados.titulo(), serieDados.ano(), serieDados.genero(),
                serieDados.diretor(), serieDados.atores(), serieDados.trama(),
                serieDados.poster(), serieDados.avaliacao(), serieDados.temporadas());
    }

    public SerieDto(Serie serie) {
        this(serie.getTitulo(), serie.getAno(), serie.getGenero(),
                serie.getDiretor(), serie.getAtores(), serie.getTrama(),
                serie.getPoster(), serie.getAvaliacao(), serie.getTemporadas());
    }
}
