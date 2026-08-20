package com.nfsenacional.enums;

/**
 * Tipo de Imunidade do ISSQN
 * <p>Porte de {@code Nfse\Enums\TipoImunidade} (php-api).
 */
public enum TipoImunidade {
    /** Imunidade (tipo não informado na nota de origem) */
    NAO_INFORMADO(0, "Imunidade (tipo não informado na nota de origem)"),
    /** Patrimônio, renda ou serviços, uns dos outros (CF88, Art 150, VI, a) */
    PATRIMONIO_RENDA_SERVICOS(1, "Patrimônio, renda ou serviços, uns dos outros (CF88, Art 150, VI, a)"),
    /** Templos de qualquer culto (CF88, Art 150, VI, b) */
    TEMPLOS(2, "Templos de qualquer culto (CF88, Art 150, VI, b)"),
    /** Partidos políticos, sindicatos, instituições de educação e assistência social (CF88, Art 150, VI, c) */
    PARTIDOS_SINDICATOS_INSTITUICOES(3, "Partidos políticos, sindicatos, instituições de educação e assistência social (CF88, Art 150, VI, c)"),
    /** Livros, jornais, periódicos e o papel destinado a sua impressão (CF88, Art 150, VI, d) */
    LIVROS_JORNAIS_PERIODICOS(4, "Livros, jornais, periódicos e o papel destinado a sua impressão (CF88, Art 150, VI, d)"),
    /** Fonogramas e videofonogramas musicais (CF88, Art 150, VI, e) */
    FONOGRAMAS_VIDEOFONOGRAMAS(5, "Fonogramas e videofonogramas musicais (CF88, Art 150, VI, e)");

    private final int codigo;
    private final String descricao;

    TipoImunidade(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoImunidade fromCodigo(int codigo) {
        for (TipoImunidade v : values()) {
            if (v.codigo == codigo) {
                return v;
            }
        }
        throw new IllegalArgumentException("Código inválido para TipoImunidade: " + codigo);
    }

    @Override
    public String toString() {
        return String.valueOf(codigo);
    }
}
