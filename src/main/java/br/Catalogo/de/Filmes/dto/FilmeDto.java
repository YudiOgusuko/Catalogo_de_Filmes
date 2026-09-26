package br.Catalogo.de.Filmes.dto;

import br.Catalogo.de.Filmes.dto.filmeDados.FilmeDados;
import br.Catalogo.de.Filmes.model.Filme;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

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
                     Double avaliacao){

    public FilmeDto (FilmeDados filmeDados) {
        this(filmeDados.titulo(), filmeDados.ano(), filmeDados.duracao(),
                filmeDados.genero(), filmeDados.diretor(), filmeDados.atores(),
                filmeDados.trama(), filmeDados.poster(), filmeDados.avaliacao());
    }

    public FilmeDto (Filme filme) {
        this(filme.getTitulo(), filme.getAno(), filme.getDuracao(),
                filme.getGenero(), filme.getDiretor(), filme.getAtores(),
                filme.getTrama(), filme.getPoster(), filme.getAvaliacao());
    }
}
