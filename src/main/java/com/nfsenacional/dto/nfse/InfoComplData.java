package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\InfoComplData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfoComplData {

    /** Oc. 0-1 Tam 1-40 Identificador do documento técnico. Identificador de Documento de Responsabilidade Técnica: ART, RRT, DRT, Outros. */
    private String idDocumentoTecnico;

    /** Oc. 0-1 Tam 255 Chave da nota, número identificador da nota, número do contrato ou outro identificador de documento emitido pelo prestador de serviços, que subsidia a emissão dessa nota pelo tomador do serviço ou intermediário (preenchimento obrigatório caso a nota esteja sendo emitida pelo Tomador ou intermediário do serviço). */
    private String documentoReferencia;

    /** Oc. 0-1 Tam 255 Campo livre para preenchimento pelo contribuinte. */
    private String informacoesComplementares;

}
