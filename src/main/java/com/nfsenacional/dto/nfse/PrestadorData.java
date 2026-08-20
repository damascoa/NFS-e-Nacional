package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Porte de {@code Nfse\Dto\Nfse\PrestadorData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrestadorData {

    /** CNPJ do prestador. Obrigatório se não for pessoa física. */
    private String cnpj;

    /** CPF do prestador. Obrigatório se pessoa física. */
    private String cpf;

    /** Número de Identificação Fiscal (NIF) do prestador. Não permitido se tpEmit=1. */
    private String nif;

    /** Código do motivo de não informar o NIF. */
    private String codigoNaoNif;

    /** Cadastro de Atividade Econômica da Pessoa Física. */
    private String caepf;

    /** Inscrição Municipal do prestador. */
    private String inscricaoMunicipal;

    /** Razão Social ou Nome do prestador. */
    private String nome;

    /** Endereço do prestador. */
    private EnderecoData endereco;

    /** Telefone do prestador. */
    private String telefone;

    /** Email do prestador. */
    private String email;

    /** Regime tributário do prestador. */
    private RegimeTributarioData regimeTributario;

}
