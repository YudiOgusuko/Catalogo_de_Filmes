package br.Catalogo.de.Filmes.handler.erroResponse;

import lombok.Builder;

@Builder
public record ErrorResponse(String message,
                            Integer status) {
}
