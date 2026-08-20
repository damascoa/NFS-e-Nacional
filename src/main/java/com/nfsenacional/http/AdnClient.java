package com.nfsenacional.http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nfsenacional.NfseContext;
import com.nfsenacional.dto.http.AliquotaDto;
import com.nfsenacional.dto.http.DistribuicaoDfeResponse;
import com.nfsenacional.dto.http.DistribuicaoNsuDto;
import com.nfsenacional.dto.http.MensagemProcessamentoDto;
import com.nfsenacional.dto.http.ParametrosConfiguracaoConvenioDto;
import com.nfsenacional.dto.http.ResultadoConsultaAliquotasResponse;
import com.nfsenacional.dto.http.ResultadoConsultaConfiguracoesConvenioResponse;
import com.nfsenacional.enums.TipoAmbiente;
import com.nfsenacional.enums.TipoNsu;
import com.nfsenacional.signer.NfseCertificate;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Cliente do ADN (Ambiente de Dados Nacional) — distribuição de documentos fiscais (NSU),
 * parâmetros/alíquotas/benefícios/regimes/retenções por município e DANFSe.
 * <p>
 * Domínio próprio (não é o mesmo da Sefin, e não segue override por município — sempre
 * {@code adn.nfse.gov.br}/{@code adn.producaorestrita.nfse.gov.br}). Mesma autenticação mTLS.
 * <p>
 * Porte de {@code Nfse\Http\Client\AdnClient} (php-api, 317 linhas).
 *
 * @author Renato
 */
public class AdnClient {

    private static final String URL_PRODUCTION = "https://adn.nfse.gov.br";
    private static final String URL_HOMOLOGATION = "https://adn.producaorestrita.nfse.gov.br";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final String baseUrl;
    private final Gson gson = new Gson();

    public AdnClient(NfseContext context) {
        this(context, certificateFrom(context));
    }

    public AdnClient(NfseContext context, NfseCertificate certificate) {
        this.baseUrl = context.getAmbiente() == TipoAmbiente.PRODUCAO ? URL_PRODUCTION : URL_HOMOLOGATION;

        javax.net.ssl.SSLContext sslContext = certificate.buildMtlsSslContext(context.getTrustStorePath(), context.getTrustStorePassword());
        this.httpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), certificate.trustManager(context.getTrustStorePath(), context.getTrustStorePassword()))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    private static NfseCertificate certificateFrom(NfseContext context) {
        return context.getCertificateContent() != null
                ? NfseCertificate.fromContent(context.getCertificateContent(), context.getCertificatePassword())
                : new NfseCertificate(context.getCertificatePath(), context.getCertificatePassword());
    }

    // ===== ADN Contribuinte =====

    public DistribuicaoDfeResponse baixarDfeContribuinte(int nsu, String cnpjConsulta, boolean lote) {
        HttpUrl.Builder url = HttpUrl.parse(baseUrl + "/contribuintes/DFe/" + nsu).newBuilder();
        if (cnpjConsulta != null && !cnpjConsulta.isEmpty()) {
            url.addQueryParameter("cnpjConsulta", cnpjConsulta);
        }
        if (!lote) {
            url.addQueryParameter("lote", "false");
        }
        return mapDistribuicaoResponse(get(url.build().toString()));
    }

    public JsonElement consultarEventosContribuinte(String chaveAcesso) {
        return get(baseUrl + "/contribuintes/NFSe/" + chaveAcesso + "/Eventos");
    }

    // ===== ADN Município =====

    public DistribuicaoDfeResponse baixarDfeMunicipio(int nsu, TipoNsu tipoNsu, boolean lote) {
        HttpUrl.Builder url = HttpUrl.parse(baseUrl + "/municipios/DFe/" + nsu).newBuilder();
        if (tipoNsu != null) {
            url.addQueryParameter("tipoNSU", tipoNsu.toString());
        }
        if (!lote) {
            url.addQueryParameter("lote", "false");
        }
        return mapDistribuicaoResponse(get(url.build().toString()));
    }

    // ===== ADN Recepção =====

    public JsonElement enviarLote(String xmlZipB64) {
        JsonObject body = new JsonObject();
        JsonArray lote = new JsonArray();
        lote.add(xmlZipB64);
        body.add("LoteXmlGZipB64", lote);
        return post(baseUrl + "/adn/DFe", body);
    }

    // ===== ADN Parâmetros Municipais =====

    public ResultadoConsultaConfiguracoesConvenioResponse consultarParametrosConvenio(String codigoMunicipio) {
        JsonObject response = getAsObject(baseUrl + "/parametrizacao/" + codigoMunicipio + "/convenio");

        ParametrosConfiguracaoConvenioDto parametros = null;
        if (response.has("parametrosConvenio") && response.get("parametrosConvenio").isJsonObject()) {
            JsonObject p = response.getAsJsonObject("parametrosConvenio");
            parametros = ParametrosConfiguracaoConvenioDto.builder()
                    .aderenteAmbienteNacional(intOrNull(p, "aderenteAmbienteNacional"))
                    .aderenteEmissorNacional(intOrNull(p, "aderenteEmissorNacional"))
                    .situacaoEmissaoPadraoContribuintesRFB(intOrNull(p, "situacaoEmissaoPadraoContribuintesRFB"))
                    .aderenteMAN(intOrNull(p, "aderenteMAN"))
                    .permiteAproveitametoDeCreditos(boolOrNull(p, "permiteAproveitametoDeCreditos"))
                    .build();
        }

        return ResultadoConsultaConfiguracoesConvenioResponse.builder()
                .mensagem(strOrNull(response, "mensagem"))
                .parametrosConvenio(parametros)
                .build();
    }

    public ResultadoConsultaAliquotasResponse consultarAliquota(String codigoMunicipio, String codigoServico, String competencia) {
        String url = baseUrl + "/parametrizacao/" + codigoMunicipio + "/" + encode(codigoServico) + "/" + encode(competencia) + "/aliquota";
        return mapAliquotaResponse(getAsObject(url));
    }

    public ResultadoConsultaAliquotasResponse consultarHistoricoAliquotas(String codigoMunicipio, String codigoServico) {
        String url = baseUrl + "/parametrizacao/" + codigoMunicipio + "/" + codigoServico + "/historicoaliquotas";
        return mapAliquotaResponse(getAsObject(url));
    }

    public JsonElement consultarBeneficio(String codigoMunicipio, String numeroBeneficio, String competencia) {
        String url = baseUrl + "/parametrizacao/" + codigoMunicipio + "/" + numeroBeneficio + "/" + encode(competencia) + "/beneficio";
        return get(url);
    }

    public JsonElement consultarRegimesEspeciais(String codigoMunicipio, String codigoServico, String competencia) {
        String url = baseUrl + "/parametrizacao/" + codigoMunicipio + "/" + codigoServico + "/" + encode(competencia) + "/regimes_especiais";
        return get(url);
    }

    public JsonElement consultarRetencoes(String codigoMunicipio, String competencia) {
        String url = baseUrl + "/parametrizacao/" + codigoMunicipio + "/" + encode(competencia) + "/retencoes";
        return get(url);
    }

    // ===== ADN DANFSe =====

    /**
     * @deprecated a API oficial de geração de DANFSe do ambiente nacional será descontinuada em
     * 01/07/2026 (nota técnica NT-008-SE-CGNFSE-DANFSE-20260505) — a emissão do documento auxiliar
     * passará a ser responsabilidade do sistema emissor.
     */
    @Deprecated
    public String obterDanfse(String chaveAcesso) {
        Request request = new Request.Builder().url(baseUrl + "/danfse/" + chaveAcesso).get().build();
        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw buildException(response.code(), body);
            }
            return body;
        } catch (IOException e) {
            throw NfseApiException.requestError(e.getMessage(), 0);
        }
    }

    // ===== mapeamento =====

    private DistribuicaoDfeResponse mapDistribuicaoResponse(JsonElement responseEl) {
        JsonObject response = responseEl.getAsJsonObject();

        List<DistribuicaoNsuDto> listaNsu = new ArrayList<>();
        if (response.has("LoteDFe") && response.get("LoteDFe").isJsonArray()) {
            response.getAsJsonArray("LoteDFe").forEach(el -> {
                JsonObject item = el.getAsJsonObject();
                listaNsu.add(DistribuicaoNsuDto.builder()
                        .nsu(intOrNull(item, "NSU"))
                        .chaveAcesso(strOrNull(item, "ChaveAcesso"))
                        .dfeXmlGZipB64(strOrNull(item, "ArquivoXml"))
                        .build());
            });
        }

        Integer ultimoNsu = intOrNull(response, "UltimoNSU");
        Integer maiorNsu = intOrNull(response, "MaiorNSU");

        if (ultimoNsu == null && !listaNsu.isEmpty()) {
            int max = listaNsu.stream().mapToInt(n -> n.getNsu() != null ? n.getNsu() : 0).max().orElse(0);
            ultimoNsu = max;
            maiorNsu = max;
        }

        return DistribuicaoDfeResponse.builder()
                .tipoAmbiente(strOrNull(response, "TipoAmbiente"))
                .versaoAplicativo(strOrNull(response, "VersaoAplicativo"))
                .dataHoraProcessamento(strOrNull(response, "DataHoraProcessamento"))
                .ultimoNsu(ultimoNsu)
                .maiorNsu(maiorNsu)
                .alertas(mapMensagens(response, "Alertas"))
                .erros(mapMensagens(response, "Erros"))
                .listaNsu(listaNsu)
                .build();
    }

    private ResultadoConsultaAliquotasResponse mapAliquotaResponse(JsonObject response) {
        Map<String, List<AliquotaDto>> aliquotas = new LinkedHashMap<>();
        if (response.has("aliquotas") && response.get("aliquotas").isJsonObject()) {
            JsonObject obj = response.getAsJsonObject("aliquotas");
            for (String servico : obj.keySet()) {
                List<AliquotaDto> lista = new ArrayList<>();
                if (obj.get(servico).isJsonArray()) {
                    obj.getAsJsonArray(servico).forEach(el -> {
                        JsonObject item = el.getAsJsonObject();
                        lista.add(AliquotaDto.builder()
                                .incidencia(strOrNull(item, "Incidencia"))
                                .aliquota(doubleOrNull(item, "Aliq"))
                                .dataInicio(strOrNull(item, "DtIni"))
                                .dataFim(strOrNull(item, "DtFim"))
                                .build());
                    });
                }
                aliquotas.put(servico, lista);
            }
        }

        return ResultadoConsultaAliquotasResponse.builder()
                .mensagem(strOrNull(response, "mensagem"))
                .aliquotas(aliquotas)
                .build();
    }

    private List<MensagemProcessamentoDto> mapMensagens(JsonObject response, String field) {
        List<MensagemProcessamentoDto> lista = new ArrayList<>();
        if (!response.has(field) || !response.get(field).isJsonArray()) {
            return lista;
        }
        response.getAsJsonArray(field).forEach(el -> {
            JsonObject m = el.getAsJsonObject();
            lista.add(MensagemProcessamentoDto.builder()
                    .mensagem(firstNonNull(strOrNull(m, "Mensagem"), strOrNull(m, "mensagem")))
                    .codigo(firstNonNull(strOrNull(m, "Codigo"), strOrNull(m, "codigo")))
                    .descricao(firstNonNull(strOrNull(m, "Descricao"), strOrNull(m, "descricao")))
                    .complemento(firstNonNull(strOrNull(m, "Complemento"), strOrNull(m, "complemento")))
                    .build());
        });
        return lista;
    }

    // ===== HTTP baixo nível =====

    private JsonElement get(String url) {
        Request request = new Request.Builder().url(url)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw buildException(response.code(), body);
            }
            return gson.fromJson(body, JsonElement.class);
        } catch (IOException e) {
            throw NfseApiException.requestError(e.getMessage(), 0);
        }
    }

    private JsonObject getAsObject(String url) {
        JsonElement el = get(url);
        return el != null && el.isJsonObject() ? el.getAsJsonObject() : new JsonObject();
    }

    private JsonElement post(String url, JsonObject body) {
        RequestBody requestBody = RequestBody.create(gson.toJson(body), JSON);
        Request request = new Request.Builder().url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            String respBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw buildException(response.code(), respBody);
            }
            return gson.fromJson(respBody, JsonElement.class);
        } catch (IOException e) {
            throw NfseApiException.requestError(e.getMessage(), 0);
        }
    }

    private NfseApiException buildException(int statusCode, String responseBody) {
        List<MensagemProcessamentoDto> erros = new ArrayList<>();
        try {
            JsonObject decoded = gson.fromJson(responseBody, JsonObject.class);
            if (decoded != null) {
                erros = mapMensagens(decoded, "erros");
            }
        } catch (Exception ignored) {
            // corpo não é JSON válido.
        }
        String message = !responseBody.isEmpty() ? "Erro na requisição: " + responseBody : "Falha na requisição ao ADN";
        return NfseApiException.requestError(message, statusCode, responseBody, erros);
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return value;
        }
    }

    private String strOrNull(JsonObject obj, String field) {
        return (obj != null && obj.has(field) && !obj.get(field).isJsonNull()) ? obj.get(field).getAsString() : null;
    }

    private Integer intOrNull(JsonObject obj, String field) {
        return (obj != null && obj.has(field) && !obj.get(field).isJsonNull()) ? obj.get(field).getAsInt() : null;
    }

    private Double doubleOrNull(JsonObject obj, String field) {
        return (obj != null && obj.has(field) && !obj.get(field).isJsonNull()) ? obj.get(field).getAsDouble() : null;
    }

    private Boolean boolOrNull(JsonObject obj, String field) {
        return (obj != null && obj.has(field) && !obj.get(field).isJsonNull()) ? obj.get(field).getAsBoolean() : null;
    }

    private String firstNonNull(String a, String b) {
        return a != null ? a : b;
    }
}
