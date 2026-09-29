package br.Catalogo.de.Filmes.model;

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "serie_id")
    private Serie serie;

    @OneToMany(mappedBy = "temporadas")
    private List<Episodio> episodioList;

    public Temporadas(SerieTemporadaDto serieTemporadaDto) {
        this.temporada = serieTemporadaDto.temporada();
        this.anoTemporada = serieTemporadaDto.anoTemporada();
    }


}
