package br.Catalogo.de.Filmes.model;

import br.Catalogo.de.Filmes.dto.FilmeDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_filmes")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;

    private Integer ano;
    private String duracao;
    private Genero genero;
    private String diretor;
    private String atores;
    private String trama;
    private String poster;
    private String avaliacao;

    public Filme(FilmeDto filmeDto, Genero genero, String trama) {
        this.titulo = filmeDto.titulo();
        this.ano = filmeDto.ano();
        this.duracao = filmeDto.duracao();
        this.genero = genero;
        this.diretor = filmeDto.diretor();
        this.atores = filmeDto.atores();
        this.trama = trama;
        this.poster = filmeDto.poster();
        this.avaliacao = filmeDto.avaliacao();
    }
}
