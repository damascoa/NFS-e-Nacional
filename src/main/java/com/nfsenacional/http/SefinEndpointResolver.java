package com.nfsenacional.http;

import com.nfsenacional.NfseContext;
import com.nfsenacional.dto.http.Endpoint;
import com.nfsenacional.enums.TipoAmbiente;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolve a URL base da Sefin a usar: padrão nacional, override por município (código IBGE), ou
 * endpoint customizado explícito no {@link NfseContext}.
 * <p>
 * Porte de {@code Nfse\Support\SefinEndpointResolver} (php-api).
 *
 * @author Renato
 */
public class SefinEndpointResolver {

    private static final Endpoint DEFAULT_ENDPOINT = Endpoint.builder()
            .production("https://sefin.nfse.gov.br/SefinNacional")
            .homologation("https://sefin.producaorestrita.nfse.gov.br/SefinNacional")
            .build();

    /** Municípios com infraestrutura própria, mas seguindo o mesmo contrato de API nacional (por código IBGE). */
    private static final Map<String, Endpoint> ENDPOINTS_POR_MUNICIPIO = new HashMap<>();

    static {
        ENDPOINTS_POR_MUNICIPIO.put("3511102", Endpoint.builder()
                .production("https://164.152.60.237/nota/nacional")
                .homologation("https://catanduva.prefeitura.rlz.com.br/nota/nacional")
                .build());
    }

    public String resolve(NfseContext context) {
        Endpoint endpoint = DEFAULT_ENDPOINT;

        String codigo = context.getCodigoMunicipio();
        if (codigo != null && ENDPOINTS_POR_MUNICIPIO.containsKey(codigo)) {
            endpoint = ENDPOINTS_POR_MUNICIPIO.get(codigo);
        }

        if (context.getEndpoint() != null) {
            endpoint = context.getEndpoint();
        }

        return context.getAmbiente() == TipoAmbiente.PRODUCAO
                ? endpoint.getProduction()
                : endpoint.getHomologation();
    }
}
