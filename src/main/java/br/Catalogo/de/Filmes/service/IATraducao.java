package br.Catalogo.de.Filmes.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.http.HttpException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class IATraducao {

    private static final List<String> MODELOS = List.of(
            "gemini-3.1-flash-lite",
            "gemini-3-flash-preview");

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Client CLIENT = criarCliente();

    private static final int tentativas_max = 3;

    public static Client criarCliente() {
        String apiKey = Dotenv.load().get("GEMINI_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("A chave GEMINI_API_KEY não foi encontrada no .env ou nas variáveis do sistema.");
        }

       return Client.builder().apiKey(apiKey).build();
    }

    public static Map<String, String> traduzir(Map<String, String> dados) {

        Map<String, String> dadosParaTraduzir = new LinkedHashMap<>();

        dados.forEach((chave, valor) -> {
            if(valor != null && !valor.isBlank() && !valor.equals("N/A")) {
                dadosParaTraduzir.put(chave, valor);
            }
        });

        if(dadosParaTraduzir.isEmpty()) {
            return dados;
        }

        String prompt = """
                Traduza para português do Brasil os VALORES do objeto JSON abaixo.
                Mantenha as chaves exatamente iguais.
                Não traduza nomes próprios.
                Responda somente com o JSON, sem explicações.
                """ + MAPPER.writeValueAsString(dadosParaTraduzir);

        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .build();

        GenerateContentResponse response = gerarSemErro(prompt, config);

        Map<String, String> dadosTraduzidos = MAPPER.readValue(
                response != null ? response.text() : null, new TypeReference<>() {
                });

        Map<String, String> resultado = new LinkedHashMap<>(dados);
        resultado.putAll(dadosTraduzidos);
        return resultado;

    }

    private static GenerateContentResponse gerarSemErro(String prompt, GenerateContentConfig config) {
        RuntimeException ultimoErro = null;

        for (String modelo : MODELOS) {
            for (int tentativa = 1; tentativa <= tentativas_max; tentativa++) {
                try {
                    return CLIENT.models.generateContent(modelo, prompt, config);
                } catch (IOException | HttpException e) {
                    ultimoErro = new RuntimeException(e);

                    if (!erroTemporario(e)) {
                        throw ultimoErro;
                    }
                    esperar(tentativa);
                }
            }
        }
        assert ultimoErro != null;
        throw ultimoErro;
    }

    private static boolean erroTemporario(Exception e) {
        String msg = String.valueOf(e.getMessage());
        return msg.contains("503") || msg.contains("429") || msg.contains("500");
    }

    private static void esperar(int tentativa) {
        try {
            long base = (long) Math.pow(2, tentativa - 1) * 1000;
            long jitter = ThreadLocalRandom.current().nextLong(0, 500);
            Thread.sleep(base + jitter);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ie);
        }
    }
}
