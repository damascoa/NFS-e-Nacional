package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\LocalPrestacaoData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalPrestacaoData {

    /** Código do município onde o serviço foi prestado (IBGE). Utilizar 0000000 para "Águas Marítimas". */
    private String codigoLocalPrestacao;

    /** Código do país onde o serviço foi prestado (ISO2). Obrigatório se o serviço for prestado no exterior. */
    private String codigoPaisPrestacao;

}
