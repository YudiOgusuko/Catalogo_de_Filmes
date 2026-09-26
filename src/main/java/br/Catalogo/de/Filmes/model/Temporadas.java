package br.Catalogo.de.Filmes.model;

import br.Catalogo.de.Filmes.dto.SerieEpisodiosTemporadaDto;
import jakarta.persistence.*;
import lombok.*;

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

    private String tituloSerie;
    private Integer temporada;
    private Integer anoTemporada;
    private String tituloEpisodio;
    private String anoEpisodio;
    private Integer episodio;
    private Double avaliacao;

    public Temporadas(String tituloSerie, Integer temporada, Integer anoTemporada, SerieEpisodiosTemporadaDto serieEpisodiosTemporadaDto) {
        this.tituloSerie = tituloSerie;
        this.temporada = temporada;
        this.anoTemporada = anoTemporada;
        this.tituloEpisodio = serieEpisodiosTemporadaDto.tituloEpisodio();
        this.anoEpisodio = serieEpisodiosTemporadaDto.anoEpisodio();
        this.episodio = serieEpisodiosTemporadaDto.episodio();
        this.avaliacao = serieEpisodiosTemporadaDto.avaliacao();
    }


}
