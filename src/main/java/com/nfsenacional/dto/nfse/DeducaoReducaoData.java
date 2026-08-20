package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * Porte de {@code Nfse\Dto\Nfse\DeducaoReducaoData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeducaoReducaoData {

    /** Percentual de dedução/redução da base de cálculo. */
    private Double percentualDeducaoReducao;

    /** Valor monetário de dedução/redução da base de cálculo. */
    private Double valorDeducaoReducao;

    /** Documentos comprobatórios da dedução/redução. */
    private List<DocumentoDeducaoData> documentos;

}
