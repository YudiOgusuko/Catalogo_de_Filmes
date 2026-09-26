package br.Catalogo.de.Filmes.service;

import br.Catalogo.de.Filmes.dto.FilmeDto;
import br.Catalogo.de.Filmes.dto.conteudosDados.ConteudoSearchDados;
import br.Catalogo.de.Filmes.dto.filmeDados.FilmeDados;
import br.Catalogo.de.Filmes.handler.exception.BadRequestException;
import br.Catalogo.de.Filmes.handler.exception.NotFoundException;
import br.Catalogo.de.Filmes.model.Filme;
import br.Catalogo.de.Filmes.repository.IFilmeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
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

    public ConteudoSearchDados tituloIgual(String filme) {

        try {
            String url = String.format("%s%s&s=%s%s", apiUrl, apiKey, filme.toLowerCase().replace(" ", "+").trim(), "&type=movie");
            ConteudoSearchDados conteudoSearchDados = restTemplate.getForObject(url, ConteudoSearchDados.class);

            if(conteudoSearchDados == null || VerificarCampos.todosCamposNull(conteudoSearchDados)) {
                throw new NotFoundException(String.format("Nenhum dado do filme '%s' foi encontrado", filme));
            }

            return conteudoSearchDados;

        }catch (HandlerMethodValidationException e) {
            throw new BadRequestException(e.getMessage());
        }

    }

    public FilmeDto buscarFilmes(String filme) {

        Optional<Filme> filmeNoBanco = filmeRepository.findByTituloIgnoreCase(filme);
        if(filmeNoBanco.isPresent()) {return new FilmeDto(filmeNoBanco.get());}

        try {
            String url = String.format("%s%s&t=%s%s", apiUrl, apiKey, filme.toLowerCase().replace(" ", "+").trim(), "&type=movie");
            FilmeDados filmeDados = restTemplate.getForObject(url, FilmeDados.class);

            if(filmeDados == null || VerificarCampos.todosCamposNull(filmeDados)) {
                throw new NotFoundException(String.format("Nenhum dado do filme '%s' foi encontrado", filme));
            }

            return new FilmeDto(filmeDados);

        } catch (HandlerMethodValidationException e) {
            throw new BadRequestException(e.getMessage());
        }

    }

    public FilmeDto adicionarFilme(String filme) {

        Optional<Filme> filmeOptional = filmeRepository.findByTituloIgnoreCase(filme);

        if(filmeOptional.isPresent()) {
            throw new BadRequestException(String.format("O filme %s ja foi adicionado.", filme));
        }

        FilmeDto filmeDto = buscarFilmes(filme);

        filmeRepository.save(new Filme(filmeDto));
        return filmeDto;
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
}

