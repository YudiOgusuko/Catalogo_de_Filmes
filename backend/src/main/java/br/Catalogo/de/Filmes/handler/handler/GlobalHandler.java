package br.Catalogo.de.Filmes.handler.handler;

import br.Catalogo.de.Filmes.handler.erroResponse.ErrorResponse;
import br.Catalogo.de.Filmes.handler.exception.BadRequestException;
import br.Catalogo.de.Filmes.handler.exception.NotFoundException;
import jakarta.validation.UnexpectedTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> badRequestHandlerMethod(HandlerMethodValidationException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Os dados fornecidos estão incorretos.")
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> badRequestHandlerMethod(MissingServletRequestParameterException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(String.format("O parâmetro obrigatório '%s' não foi informado.", e.getParameterName()))
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> badRequestHandlerMethod(MethodArgumentTypeMismatchException e) {

        String parametro = e.getName();
        Object valor = e.getValue();
        String tipoEsperado = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "tipo inválido";
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(String.format("O parâmetro '%s' recebeu '%s', que é inválido. Era esperado um valor do tipo '%s'.", parametro, valor, tipoEsperado))
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<ErrorResponse> badRequestHandlerMethod(UnexpectedTypeException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("A data passada é inválida. Por favor tente Novamente.")
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> badRequestHandlerMethod(NoResourceFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("A requisição realizada esta incorreta. Dê uma olhada no Construtor.")
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequestHandler(BadRequestException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundHandler(NotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
