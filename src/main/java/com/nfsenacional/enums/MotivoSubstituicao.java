package com.nfsenacional.enums;

/**
 * Motivo da substituição de NFS-e Baseado no schema: TSMotivoSubst
 * <p>Porte de {@code Nfse\Enums\MotivoSubstituicao} (php-api).
 */
public enum MotivoSubstituicao {
    /** Desenquadramento de NFS-e do Simples Nacional */
    DESENQUADRAMENTO_SIMPLES_NACIONAL("01", "Desenquadramento de NFS-e do Simples Nacional"),
    /** Enquadramento de NFS-e no Simples Nacional */
    ENQUADRAMENTO_SIMPLES_NACIONAL("02", "Enquadramento de NFS-e no Simples Nacional"),
    /** Inclusão Retroativa de Imunidade/Isenção para NFS-e */
    INCLUSAO_RETROATIVA_IMUNIDADE_ISENCAO("03", "Inclusão Retroativa de Imunidade/Isenção para NFS-e"),
    /** Exclusão Retroativa de Imunidade/Isenção para NFS-e */
    EXCLUSAO_RETROATIVA_IMUNIDADE_ISENCAO("04", "Exclusão Retroativa de Imunidade/Isenção para NFS-e"),
    /** Rejeição de NFS-e pelo tomador ou pelo intermediário se responsável pelo recolhimento do tributo */
    REJEICAO_NFS("05", "Rejeição de NFS-e pelo tomador ou pelo intermediário se responsável pelo recolhimento do tributo"),
    /** Outros */
    OUTROS("99", "Outros");

    private final String codigo;
    private final String descricao;

    MotivoSubstituicao(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static MotivoSubstituicao fromCodigo(String codigo) {
        for (MotivoSubstituicao v : values()) {
            if (v.codigo.equals(codigo)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para MotivoSubstituicao: " + codigo);
    }

    @Override
    public String toString() {
        return codigo;
    }
}
