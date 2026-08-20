package com.nfsenacional.enums;

/**
 * Código da Situação Tributária do PIS/COFINS Baseado no schema: TSCST
 * <p>Porte de {@code Nfse\Enums\CstPisCofins} (php-api).
 */
public enum CstPisCofins {
    /** Nenhum */
    NENHUM("00", "Nenhum"),
    /** Operação Tributável com Alíquota Básica */
    OPERACAO_TRIBUTAVEL_ALIQUOTA_BASICA("01", "Operação Tributável com Alíquota Básica"),
    /** Operação Tributável com Alíquota Diferenciada */
    OPERACAO_TRIBUTAVEL_ALIQUOTA_DIFERENCIADA("02", "Operação Tributável com Alíquota Diferenciada"),
    /** Operação Tributável com Alíquota por Unidade de Medida de Produto */
    OPERACAO_TRIBUTAVEL_ALIQUOTA_POR_UNIDADE("03", "Operação Tributável com Alíquota por Unidade de Medida de Produto"),
    /** Operação Tributável Monofásica - Revenda a Alíquota Zero */
    OPERACAO_TRIBUTAVEL_MONOFASICA_ALIQUOTA_ZERO("04", "Operação Tributável Monofásica - Revenda a Alíquota Zero"),
    /** Operação Tributável por Substituição Tributária */
    OPERACAO_TRIBUTAVEL_SUBSTITUICAO_TRIBUTARIA("05", "Operação Tributável por Substituição Tributária"),
    /** Operação Tributável a Alíquota Zero */
    OPERACAO_TRIBUTAVEL_ALIQUOTA_ZERO("06", "Operação Tributável a Alíquota Zero"),
    /** Operação Isenta da Contribuição */
    OPERACAO_ISENTA("07", "Operação Isenta da Contribuição"),
    /** Operação sem Incidência da Contribuição */
    OPERACAO_SEM_INCIDENCIA("08", "Operação sem Incidência da Contribuição"),
    /** Operação com Suspensão da Contribuição */
    OPERACAO_COM_SUSPENSAO("09", "Operação com Suspensão da Contribuição"),
    /** Outras Operações de Saída */
    OUTRAS_OPERACOES_SAIDA("49", "Outras Operações de Saída"),
    /** Operação com Direito a Crédito - Vinculada Exclusivamente a Receita Tributada no Mercado Interno */
    CREDITO_RECEITA_TRIBUTADA_MERCADO_INTERNO("50", "Operação com Direito a Crédito - Vinculada Exclusivamente a Receita Tributada no Mercado Interno"),
    /** Operação com Direito a Crédito - Vinculada Exclusivamente a Receita Não Tributada no Mercado Interno */
    CREDITO_RECEITA_NAO_TRIBUTADA_MERCADO_INTERNO("51", "Operação com Direito a Crédito - Vinculada Exclusivamente a Receita Não Tributada no Mercado Interno"),
    /** Operação com Direito a Crédito - Vinculada Exclusivamente a Receita de Exportação */
    CREDITO_RECEITA_EXPORTACAO("52", "Operação com Direito a Crédito - Vinculada Exclusivamente a Receita de Exportação"),
    /** Operação com Direito a Crédito - Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno */
    CREDITO_RECEITAS_TRIBUTADAS_E_NAO_TRIBUTADAS_MERCADO_INTERNO("53", "Operação com Direito a Crédito - Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno"),
    /** Operação com Direito a Crédito - Vinculada a Receitas Tributadas no Mercado Interno e de Exportação */
    CREDITO_RECEITAS_TRIBUTADAS_MERCADO_INTERNO_E_EXPORTACAO("54", "Operação com Direito a Crédito - Vinculada a Receitas Tributadas no Mercado Interno e de Exportação"),
    /** Operação com Direito a Crédito - Vinculada a Receitas Não-Tributadas no Mercado Interno e de Exportação */
    CREDITO_RECEITAS_NAO_TRIBUTADAS_MERCADO_INTERNO_E_EXPORTACAO("55", "Operação com Direito a Crédito - Vinculada a Receitas Não-Tributadas no Mercado Interno e de Exportação"),
    /** Operação com Direito a Crédito - Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno, e de Exportação */
    CREDITO_RECEITAS_TRIBUTADAS_NAO_TRIBUTADAS_E_EXPORTACAO("56", "Operação com Direito a Crédito - Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno, e de Exportação"),
    /** Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita Tributada no Mercado Interno */
    CREDITO_PRESUMIDO_RECEITA_TRIBUTADA_MERCADO_INTERNO("60", "Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita Tributada no Mercado Interno"),
    /** Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita Não Tributada no Mercado Interno */
    CREDITO_PRESUMIDO_RECEITA_NAO_TRIBUTADA_MERCADO_INTERNO("61", "Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita Não Tributada no Mercado Interno"),
    /** Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita de Exportação */
    CREDITO_PRESUMIDO_RECEITA_EXPORTACAO("62", "Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita de Exportação"),
    /** Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno */
    CREDITO_PRESUMIDO_RECEITAS_TRIBUTADAS_E_NAO_TRIBUTADAS_MERCADO_INTERNO("63", "Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno"),
    /** Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas no Mercado Interno e de Exportação */
    CREDITO_PRESUMIDO_RECEITAS_TRIBUTADAS_MERCADO_INTERNO_E_EXPORTACAO("64", "Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas no Mercado Interno e de Exportação"),
    /** Crédito Presumido - Operação de Aquisição Vinculada a Receitas Não-Tributadas no Mercado Interno e de Exportação */
    CREDITO_PRESUMIDO_RECEITAS_NAO_TRIBUTADAS_MERCADO_INTERNO_E_EXPORTACAO("65", "Crédito Presumido - Operação de Aquisição Vinculada a Receitas Não-Tributadas no Mercado Interno e de Exportação"),
    /** Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno, e de Exportação */
    CREDITO_PRESUMIDO_RECEITAS_TRIBUTADAS_NAO_TRIBUTADAS_E_EXPORTACAO("66", "Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno, e de Exportação"),
    /** Crédito Presumido - Outras Operações */
    CREDITO_PRESUMIDO_OUTRAS_OPERACOES("67", "Crédito Presumido - Outras Operações"),
    /** Operação de Aquisição sem Direito a Crédito */
    AQUISICAO_SEM_DIREITO_CREDITO("70", "Operação de Aquisição sem Direito a Crédito"),
    /** Operação de Aquisição com Isenção */
    AQUISICAO_COM_ISENCAO("71", "Operação de Aquisição com Isenção"),
    /** Operação de Aquisição com Alíquota Zero */
    AQUISICAO_COM_ALIQUOTA_ZERO("72", "Operação de Aquisição com Alíquota Zero"),
    /** Operação de Aquisição com Suspensão */
    AQUISICAO_COM_SUSPENSAO("73", "Operação de Aquisição com Suspensão"),
    /** Operação de Aquisição sem Incidência */
    AQUISICAO_SEM_INCIDENCIA("74", "Operação de Aquisição sem Incidência"),
    /** Operação de Aquisição por Substituição Tributária */
    AQUISICAO_SUBSTITUICAO_TRIBUTARIA("75", "Operação de Aquisição por Substituição Tributária"),
    /** Outras Operações de Entrada */
    OUTRAS_OPERACOES_ENTRADA("98", "Outras Operações de Entrada"),
    /** Outras Operações */
    OUTRAS_OPERACOES("99", "Outras Operações");

    private final String codigo;
    private final String descricao;

    CstPisCofins(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static CstPisCofins fromCodigo(String codigo) {
        for (CstPisCofins v : values()) {
            if (v.codigo.equals(codigo)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para CstPisCofins: " + codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
