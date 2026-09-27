package br.Catalogo.de.Filmes.model;

import br.Catalogo.de.Filmes.dto.SerieEpisodiosTemporadaDto;
import br.Catalogo.de.Filmes.dto.SerieTemporadaDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tb_temporadas")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Temporadas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer temporada;
    private Integer anoTemporada;

    @Column(unique = true)
    private String tituloEpisodio;

    private String anoEpisodio;
    private Integer episodio;
    private Double avaliacao;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "serie_id")
    private Serie serie;

    @OneToMany(mappedBy = "temporadas")
    private List<Episodio> episodioList;

    public Temporadas(SerieTemporadaDto serieTemporadaDto, SerieEpisodiosTemporadaDto serieEpisodiosTemporadaDto) {
        this.temporada = serieTemporadaDto.temporada();
        this.anoTemporada = serieTemporadaDto.anoTemporada();
        this.tituloEpisodio = serieEpisodiosTemporadaDto.titulo();
        this.anoEpisodio = serieEpisodiosTemporadaDto.anoEpisodio();
        this.episodio = serieEpisodiosTemporadaDto.episodio();
        this.avaliacao = serieEpisodiosTemporadaDto.avaliacao();
    }

    public Temporadas(SerieTemporadaDto serieTemporadaDto) {
        this.temporada = serieTemporadaDto.temporada();
        this.anoTemporada = serieTemporadaDto.anoTemporada();
        this.tituloEpisodio = serieTemporadaDto.episodios().get(0).titulo();
        this.anoEpisodio = serieTemporadaDto.episodios().get(0).anoEpisodio();
        this.episodio = serieTemporadaDto.episodios().get(0).episodio();
        this.avaliacao = serieTemporadaDto.episodios().get(0).avaliacao();
    }


}
