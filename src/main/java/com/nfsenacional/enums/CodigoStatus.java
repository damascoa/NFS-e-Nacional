package com.nfsenacional.enums;

/**
 * Código de Status da NFS-e Baseado no schema: cStat
 * <p>Porte de {@code Nfse\Enums\CodigoStatus} (php-api).
 */
public enum CodigoStatus {
    /** NFS-e Gerada */
    NFSE_GERADA(100, "NFS-e Gerada"),
    /** NFS-e de Substituição Gerada */
    NFSE_SUBSTITUICAO_GERADA(101, "NFS-e de Substituição Gerada"),
    /** NFS-e de Decisão Judicial */
    NFSE_DECISAO_JUDICIAL(102, "NFS-e de Decisão Judicial"),
    /** NFS-e Avulsa */
    NFSE_AVULSA(103, "NFS-e Avulsa"),
    /** NFS-e MEI */
    NFSE_MEI(107, "NFS-e MEI");

    private final int codigo;
    private final String descricao;

    CodigoStatus(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static CodigoStatus fromCodigo(int codigo) {
        for (CodigoStatus v : values()) {
            if (v.codigo == codigo) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para CodigoStatus: " + codigo);
    }

    @Override
    public String toString() {
        return String.valueOf(codigo);
    }
}
