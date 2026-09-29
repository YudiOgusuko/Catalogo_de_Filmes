package br.Catalogo.de.Filmes.dto;

import br.Catalogo.de.Filmes.dto.filmeDados.FilmeDados;
import br.Catalogo.de.Filmes.model.Filme;
import br.Catalogo.de.Filmes.model.Genero;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

@Builder
@JsonPropertyOrder({
        "titulo",
        "ano",
        "duracao",
        "genero",
        "diretor",
        "atores",
        "trama",
        "poster",
        "avaliacao",
})

public record FilmeDto(String titulo,
                     Integer ano,
                     String duracao,
                     String genero,
                     String diretor,
                     String atores,
                     String trama,
                     String poster,
                     String avaliacao){

    public FilmeDto (FilmeDados filmeDados, String genero) {
        this(filmeDados.titulo(), filmeDados.ano(), filmeDados.duracao(),
                genero, filmeDados.diretor(), filmeDados.atores(),
                filmeDados.trama(), filmeDados.poster(), filmeDados.avaliacao());
    }

    public FilmeDto (Filme filme) {
        this(filme.getTitulo(), filme.getAno(), filme.getDuracao(),
               Genero.pegarGeneroString(filme.getGenero()), filme.getDiretor(), filme.getAtores(),
                      filme.getTrama(), filme.getPoster(), filme.getAvaliacao());
    }
}
