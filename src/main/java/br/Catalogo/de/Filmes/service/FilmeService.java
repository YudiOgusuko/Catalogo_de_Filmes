package br.Catalogo.de.Filmes.service;

import br.Catalogo.de.Filmes.dto.FilmeDto;
import br.Catalogo.de.Filmes.dto.conteudosDados.ConteudoSearchDados;
import br.Catalogo.de.Filmes.dto.filmeDados.FilmeDados;
import br.Catalogo.de.Filmes.handler.exception.BadRequestException;
import br.Catalogo.de.Filmes.handler.exception.NotFoundException;
import br.Catalogo.de.Filmes.model.Filme;
import br.Catalogo.de.Filmes.model.Genero;
import br.Catalogo.de.Filmes.repository.IFilmeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmeService {

    @Value("${omdb.api.key}")
    private String apiKey;

    @Value("${omdb.api.url}")
    private String apiUrl;

    private final IFilmeRepository filmeRepository;
    private final RestTemplate restTemplate;

    public ConteudoSearchDados tituloIgual(String tituloFilme) {

        tituloFilme = formatarString(tituloFilme);

        try {
            String url = String.format("%s%s&s=%s%s", apiUrl, apiKey, tituloFilme, "&type=movie");
            ConteudoSearchDados conteudoSearchDados = restTemplate.getForObject(url, ConteudoSearchDados.class);

            if(conteudoSearchDados == null || VerificarCampos.todosCamposNull(conteudoSearchDados)) {
                throw new NotFoundException(String.format("Nenhum dado do filme '%s' foi encontrado", tituloFilme));
            }

            return conteudoSearchDados;

        }catch (HandlerMethodValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public FilmeDto buscarFilmes(String tituloFilme) {

        tituloFilme = formatarString(tituloFilme);

        Optional<Filme> filmeNoBanco = filmeRepository.findByTituloIgnoreCase(tituloFilme);
        if(filmeNoBanco.isPresent()) {return new FilmeDto(filmeNoBanco.get());}

        try {
            String url = String.format("%s%s&t=%s%s", apiUrl, apiKey, tituloFilme, "&type=movie");
            FilmeDados filmeDados = restTemplate.getForObject(url, FilmeDados.class);

            if(filmeDados == null || VerificarCampos.todosCamposNull(filmeDados)) {
                throw new NotFoundException(String.format("Nenhum dado do filme '%s' foi encontrado", tituloFilme));
            }

            String genero = filmeDados.genero().split(",")[0].trim();

            return new FilmeDto(filmeDados, Genero.pegarStringGenero(genero));

        } catch (HandlerMethodValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }


    public FilmeDto adicionarFilme(String tituloFilme) {

        tituloFilme = formatarString(tituloFilme);

        Optional<Filme> filmeOptional = filmeRepository.findByTituloIgnoreCase(tituloFilme);
        if(filmeOptional.isPresent()) {
            throw new BadRequestException(String.format("O filme %s ja foi adicionado.", tituloFilme));
        }

        try {
            FilmeDto filmeDto = buscarFilmes(tituloFilme);

            String trama = "N/A";
            if(!filmeDto.trama().equalsIgnoreCase("N/A")) {
                trama = IATraducao.traduzir(
                                Map.of("trama", filmeDto.trama()))
                        .get("trama");
            }

            filmeRepository.save(new Filme(filmeDto, Genero.pegarGenero(filmeDto.genero()), trama));
            return filmeDto;
        } catch (HandlerMethodValidationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }


    public String deletarFilme(String filme) {

        Filme filmeParaDeletar = filmeRepository.findByTituloIgnoreCase(filme)
                .orElseThrow(() -> new NotFoundException(String.format("O filme %s não está no catálogo.", filme)));

        filmeRepository.delete(filmeParaDeletar);

        return String.format("O filme %s foi deletado do catálogo.", filme);
    }


    public String deletarTodosFilmes() {
        filmeRepository.deleteAll();
        return "Todos os filmes foram deletados do catálogo.";
    }


    public List<FilmeDto> verTodosFilmes() {
        return filmeRepository.findAll().stream().map(FilmeDto::new).toList();
    }


    private String formatarString(String txt) {
        if(txt == null || txt.isBlank()) {
            return "";
        }

        String[] palavra = txt.trim().split("\\s+");
        StringBuilder stringBuilder = new StringBuilder();

        for(String p : palavra) {
            if(!p.isBlank()) {
                String formatar = p.substring(0, 1).toUpperCase()
                        + p.substring(1).toLowerCase();

                stringBuilder.append(formatar).append(" ");
            }
        }

        return stringBuilder.toString().trim();
    }
}

