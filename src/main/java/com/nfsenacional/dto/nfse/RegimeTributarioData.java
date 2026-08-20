package com.nfsenacional.dto.nfse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.nfsenacional.enums.OpcaoSimplesNacional;
import com.nfsenacional.enums.RegimeApuracaoSN;
import com.nfsenacional.enums.RegimeEspecialTributacao;

/**
 * Porte de {@code Nfse\Dto\Nfse\RegimeTributarioData} (php-api).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegimeTributarioData {

    /** Opção pelo Simples Nacional. 1 - Não Optante 2 - Optante - Microempreendedor Individual (MEI) 3 - Optante - Microempresa ou Empresa de Pequeno Porte (ME/EPP) */
    private OpcaoSimplesNacional opcaoSimplesNacional;

    /** Regime de apuração dos tributos (SN). Obrigatório se opSimpNac = 3. 1 - Regime de apuração dos tributos federais e municipal pelo SN 2 - Regime de apuração dos tributos federais pelo SN e municipal pelo regime normal (ISSQN) */
    private RegimeApuracaoSN regimeApuracaoTributosSn;

    /** Regime Especial de Tributação. 0 - Nenhum 1 - Ato Cooperado (Cooperativa) 2 - Estimativa 3 - Microempresa Municipal 4 - Notário ou Registrador 5 - Profissional Autônomo 6 - Sociedade de Profissionais */
    private RegimeEspecialTributacao regimeEspecialTributacao;

}
