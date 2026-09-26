package br.Catalogo.de.Filmes.model;

import br.Catalogo.de.Filmes.dto.SerieDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_series")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String ano;
    private String genero;
    private String diretor;
    private String atores;
    private String trama;
    private String poster;
    private Double avaliacao;
    private Integer temporadas;

    public Serie(SerieDto serieDto) {
        this.titulo = serieDto.titulo();
        this.ano = serieDto.ano();
        this.genero = serieDto.genero();
        this.diretor = serieDto.diretor();
        this.atores = serieDto.atores();
        this.trama = serieDto.trama();
        this.poster = serieDto.poster();
        this.avaliacao = serieDto.avaliacao();
        this.temporadas = serieDto.temporadas();

    }
}
