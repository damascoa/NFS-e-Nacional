package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\ImovelData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImovelData {

    /** Inscrição imobiliária fiscal fornecida pela Prefeitura Municipal. */
    private String inscricaoImobiliariaFiscal;

    /** Código do Cadastro Imobiliário Brasileiro (CIB). */
    private String codigoCib;

    /** Endereço do imóvel. */
    private EnderecoData endereco;

}
