package br.Catalogo.de.Filmes.model;

import br.Catalogo.de.Filmes.dto.SerieEpisodioDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_episodios")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Episodio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tituloSerie;
    private String tituloEpisodio;
    private String duracao;
    private Integer temporada;
    private Integer episodio;
    private String descricao;
    private Double avaliacao;
    private String ano;
    private String poster;

    public Episodio (String serie, SerieEpisodioDto serieEpisodioDto) {
        this.tituloSerie = serie;
        this.tituloEpisodio = serieEpisodioDto.titulo();
        this.duracao = serieEpisodioDto.duracao();
        this.temporada = serieEpisodioDto.temporada();
        this.episodio = serieEpisodioDto.episodio();
        this.descricao = serieEpisodioDto.descricao();
        this.avaliacao = serieEpisodioDto.avaliacao();
        this.ano = serieEpisodioDto.ano();
        this.poster = serieEpisodioDto.poster();
    }
}
