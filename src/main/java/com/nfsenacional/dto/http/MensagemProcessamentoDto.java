package com.nfsenacional.dto.http;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Mensagem de erro/alerta retornada pela Sefin (em {@code erros}/{@code alertas} das respostas).
 * Porte de {@code Nfse\Dto\Http\MensagemProcessamentoDto} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensagemProcessamentoDto {
    private String mensagem;
    private List<Object> parametros;
    private String codigo;
    private String descricao;
    private String complemento;
}
