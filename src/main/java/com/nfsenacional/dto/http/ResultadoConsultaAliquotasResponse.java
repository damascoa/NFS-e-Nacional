package com.nfsenacional.dto.http;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Retorno da consulta de alíquota(s) vigente(s) ou histórico (ADN).
 * <p>
 * {@code aliquotas} é indexado por código de serviço (não é uma lista simples — apesar do docblock
 * {@code @var AliquotaDto[]} no PHP de origem, o uso real em
 * {@code AdnClient::mapAliquotaResponse()} monta um array associativo {@code servico => AliquotaDto[]}).
 * Porte de {@code Nfse\Dto\Http\ResultadoConsultaAliquotasResponse} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoConsultaAliquotasResponse {
    private String mensagem;
    private Map<String, List<AliquotaDto>> aliquotas;
}
