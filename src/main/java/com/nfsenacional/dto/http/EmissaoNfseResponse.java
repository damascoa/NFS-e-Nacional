package com.nfsenacional.dto.http;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Retorno síncrono de {@code POST /nfse}. Se {@code erros} estiver vazio, {@code nfseXmlGZipB64}
 * traz o XML assinado da NFS-e (gzip + base64) já aprovado.
 * Porte de {@code Nfse\Dto\Http\EmissaoNfseResponse} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmissaoNfseResponse {
    private Integer tipoAmbiente;
    private String versaoAplicativo;
    private String dataHoraProcessamento;
    private String idDps;
    private String chaveAcesso;
    private String nfseXmlGZipB64;
    private List<MensagemProcessamentoDto> alertas;
    private List<MensagemProcessamentoDto> erros;
}
