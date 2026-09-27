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

    @Column(unique = true)
    private String tituloEpisodio;
    private String duracao;
    private Integer temporada;
    private Integer episodio;
    private String descricao;
    private Double avaliacao;
    private String ano;
    private String poster;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "serie_id")
    private Serie serie;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "temporada_id")
    private Temporadas temporadas;

    public Episodio (SerieEpisodioDto serieEpisodioDto) {
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
