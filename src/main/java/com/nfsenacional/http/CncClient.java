package com.nfsenacional.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nfsenacional.NfseContext;
import com.nfsenacional.dto.http.MensagemProcessamentoDto;
import com.nfsenacional.enums.TipoAmbiente;
import com.nfsenacional.signer.NfseCertificate;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Cliente do CNC (Cadastro Nacional de Contribuintes) — consulta e atualização cadastral.
 * <p>
 * Domínio próprio ({@code adn.nfse.gov.br/cnc}), mesma autenticação mTLS dos demais clientes.
 * As respostas não têm DTO fixo no PHP de origem (retornam {@code array} genérico) — aqui viram
 * {@link JsonElement}, mantendo a mesma flexibilidade sem inventar um formato.
 * <p>
 * Porte de {@code Nfse\Http\Client\CncClient} (php-api).
 *
 * @author Renato
 */
public class CncClient {

    private static final String URL_PRODUCTION = "https://adn.nfse.gov.br/cnc";
    private static final String URL_HOMOLOGATION = "https://adn.producaorestrita.nfse.gov.br/cnc";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final String baseUrl;
    private final Gson gson = new Gson();

    public CncClient(NfseContext context) {
        this(context, certificateFrom(context));
    }

    public CncClient(NfseContext context, NfseCertificate certificate) {
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

    /** CNC Consulta — dados atuais de um contribuinte. */
    public JsonElement consultarContribuinte(String cpfCnpj) {
        return get("/consulta/cad/" + cpfCnpj);
    }

    /** CNC Município — alterações no cadastro nacional via NSU. */
    public JsonElement baixarAlteracoesCadastro(int nsu) {
        return get("/municipio/cad/" + nsu);
    }

    /** CNC Recepção — cadastra ou atualiza um contribuinte no CNC. */
    public JsonElement atualizarContribuinte(JsonObject dados) {
        return post("", dados);
    }

    private JsonElement get(String endpoint) {
        Request request = new Request.Builder().url(baseUrl + endpoint)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        return execute(request);
    }

    private JsonElement post(String endpoint, JsonObject data) {
        RequestBody body = RequestBody.create(gson.toJson(data), JSON);
        Request request = new Request.Builder().url(baseUrl + endpoint)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        return execute(request);
    }

    private JsonElement execute(Request request) {
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

    private NfseApiException buildException(int statusCode, String responseBody) {
        List<MensagemProcessamentoDto> erros = new ArrayList<>();
        try {
            JsonObject decoded = gson.fromJson(responseBody, JsonObject.class);
            if (decoded != null && decoded.has("erros") && decoded.get("erros").isJsonArray()) {
                decoded.getAsJsonArray("erros").forEach(el -> {
                    JsonObject m = el.getAsJsonObject();
                    erros.add(MensagemProcessamentoDto.builder()
                            .mensagem(strOrNull(m, "Mensagem", "mensagem"))
                            .codigo(strOrNull(m, "Codigo", "codigo"))
                            .descricao(strOrNull(m, "Descricao", "descricao"))
                            .complemento(strOrNull(m, "Complemento", "complemento"))
                            .build());
                });
            }
        } catch (Exception ignored) {
            // corpo não é JSON válido.
        }
        String message = !responseBody.isEmpty() ? "Erro na requisição: " + responseBody : "Falha na requisição ao CNC";
        return NfseApiException.requestError(message, statusCode, responseBody, erros);
    }

    private String strOrNull(JsonObject obj, String... fields) {
        for (String field : fields) {
            if (obj.has(field) && !obj.get(field).isJsonNull()) {
                return obj.get(field).getAsString();
            }
        }
        return null;
    }
}
