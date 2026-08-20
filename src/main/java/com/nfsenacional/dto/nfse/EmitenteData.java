package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\EmitenteData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmitenteData {

    /** CNPJ do emitente. */
    private String cnpj;

    /** CPF do emitente. */
    private String cpf;

    /** Inscrição Municipal do emitente. */
    private String inscricaoMunicipal;

    /** Razão Social ou Nome do emitente. */
    private String nome;

    /** Nome Fantasia do emitente. */
    private String nomeFantasia;

    /** Endereço do emitente. */
    private EnderecoEmitenteData endereco;

    /** Telefone do emitente. */
    private String telefone;

    /** Email do emitente. */
    private String email;

}
