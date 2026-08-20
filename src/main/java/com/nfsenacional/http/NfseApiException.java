package com.nfsenacional.http;

import com.nfsenacional.dto.http.MensagemProcessamentoDto;

import java.util.Collections;
import java.util.List;

/**
 * Erro de requisição ou de negócio retornado pela Sefin/ADN/CNC.
 * <p>
 * Porte de {@code Nfse\Http\Exceptions\NfseApiException} (php-api).
 *
 * @author Renato
 */
public class NfseApiException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;
    private final List<MensagemProcessamentoDto> erros;

    public NfseApiException(String message, int statusCode, String responseBody, List<MensagemProcessamentoDto> erros) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.erros = erros != null ? erros : Collections.emptyList();
    }

    public static NfseApiException requestError(String message, int statusCode) {
        return new NfseApiException(message, statusCode, null, null);
    }

    public static NfseApiException requestError(String message, int statusCode, String responseBody, List<MensagemProcessamentoDto> erros) {
        return new NfseApiException(message, statusCode, responseBody, erros);
    }

    public static NfseApiException responseError(String message) {
        return new NfseApiException(message, 0, null, null);
    }

    public static NfseApiException responseError(String message, int statusCode, String responseBody, List<MensagemProcessamentoDto> erros) {
        return new NfseApiException(message, statusCode, responseBody, erros);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public List<MensagemProcessamentoDto> getErros() {
        return erros;
    }
}
