package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\IntermediarioData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntermediarioData {

    /** CNPJ do intermediário. Obrigatório se pessoa jurídica. */
    private String cnpj;

    /** CPF do intermediário. Obrigatório se pessoa física. */
    private String cpf;

    /** Número de Identificação Fiscal (NIF) do intermediário. Não permitido se tpEmit=3. */
    private String nif;

    /** Código do motivo de não informar o NIF. */
    private String codigoNaoNif;

    /** Cadastro de Atividade Econômica da Pessoa Física. */
    private String caepf;

    /** Inscrição Municipal do intermediário. */
    private String inscricaoMunicipal;

    /** Razão Social ou Nome do intermediário. */
    private String nome;

    /** Endereço do intermediário. */
    private EnderecoData endereco;

    /** Telefone do intermediário. */
    private String telefone;

    /** Email do intermediário. */
    private String email;

}
