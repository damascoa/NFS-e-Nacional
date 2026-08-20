package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.nfsenacional.enums.MotivoNaoNif;

/**
 * Porte de {@code Nfse\Dto\Nfse\TomadorData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TomadorData {

    /** CPF do tomador. Obrigatório se pessoa física. */
    private String cpf;

    /** CNPJ do tomador. Obrigatório se pessoa jurídica. */
    private String cnpj;

    /** Número de Identificação Fiscal (NIF) do tomador. Não permitido se tpEmit=2. */
    private String nif;

    /** Código do motivo de não informar o NIF. */
    private MotivoNaoNif codigoNaoNif;

    /** Cadastro de Atividade Econômica da Pessoa Física. */
    private String caepf;

    /** Inscrição Municipal do tomador. */
    private String inscricaoMunicipal;

    /** Razão Social ou Nome do tomador. */
    private String nome;

    /** Endereço do tomador. */
    private EnderecoData endereco;

    /** Telefone do tomador. */
    private String telefone;

    /** Email do tomador. */
    private String email;

}
