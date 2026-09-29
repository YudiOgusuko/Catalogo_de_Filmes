package br.Catalogo.de.Filmes.model;

import br.Catalogo.de.Filmes.dto.SerieDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

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

    @Column(unique = true)
    private String titulo;

    private String ano;
    private Genero genero;
    private String diretor;
    private String atores;
    private String trama;
    private String poster;
    private String avaliacao;
    private Integer temporadas;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL)
    private List<Temporadas> temporadasList = new ArrayList<>();

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL)
    private List<Episodio> episodiosList = new ArrayList<>();

    public Serie(SerieDto serieDto, Genero genero) {
        this.titulo = serieDto.titulo();
        this.ano = serieDto.ano();
        this.genero = genero;
        this.diretor = serieDto.diretor();
        this.atores = serieDto.atores();
        this.trama = serieDto.trama();
        this.poster = serieDto.poster();
        this.avaliacao = serieDto.avaliacao();
        this.temporadas = serieDto.temporadas();

    }

    public Serie(SerieDto serieDto) {
        this.titulo = serieDto.titulo();
        this.ano = serieDto.ano();
        this.genero = Genero.pegarGenero(serieDto.genero());
        this.diretor = serieDto.diretor();
        this.atores = serieDto.atores();
        this.trama = serieDto.trama();
        this.poster = serieDto.poster();
        this.avaliacao = serieDto.avaliacao();
        this.temporadas = serieDto.temporadas();

    }

    public void salvarTemporadas(Temporadas temporadas) {
        temporadas.setSerie(this);
        this.getTemporadasList().add(temporadas);
    }

    public void salvarEpisodios(Temporadas temporadas, Episodio episodio) {
        episodio.setSerie(this);
        this.getEpisodiosList().add(episodio);

        episodio.setTemporadas(temporadas);
    }
}
