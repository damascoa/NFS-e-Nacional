package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\FornecedorData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FornecedorData {

    /** CNPJ do fornecedor. */
    private String cnpj;

    /** CPF do fornecedor. */
    private String cpf;

    /** NIF do fornecedor. */
    private String nif;

    /** Código do motivo de não informar o NIF. */
    private String codigoNaoNif;

    /** CAEPF do fornecedor. */
    private String caepf;

    /** Inscrição Municipal do fornecedor. */
    private String inscricaoMunicipal;

    /** Razão Social ou Nome do fornecedor. */
    private String nome;

    /** Endereço do fornecedor. */
    private EnderecoData endereco;

    /** Telefone do fornecedor. */
    private String telefone;

    /** Email do fornecedor. */
    private String email;

}
