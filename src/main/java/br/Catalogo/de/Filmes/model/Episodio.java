package br.Catalogo.de.Filmes.model;

import br.Catalogo.de.Filmes.dto.SerieEpisodiosTemporadaDto;
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

    @Column(nullable = false)
    private String tituloEpisodio;
    private Integer episodio;
    private String avaliacao;
    private String ano;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "serie_id")
    private Serie serie;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "temporada_id")
    private Temporadas temporadas;

    public Episodio (SerieEpisodiosTemporadaDto serieEpisodioDto) {
        this.tituloEpisodio = serieEpisodioDto.titulo();
        this.ano = serieEpisodioDto.anoEpisodio();
        this.episodio = serieEpisodioDto.episodio();
        this.avaliacao = serieEpisodioDto.avaliacao();
    }
}
