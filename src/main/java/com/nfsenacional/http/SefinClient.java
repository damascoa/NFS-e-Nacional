package com.nfsenacional.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.nfsenacional.NfseContext;
import com.nfsenacional.dto.http.ConsultaDpsResponse;
import com.nfsenacional.dto.http.ConsultaNfseResponse;
import com.nfsenacional.dto.http.EmissaoNfseResponse;
import com.nfsenacional.dto.http.MensagemProcessamentoDto;
import com.nfsenacional.dto.http.RegistroEventoResponse;
import com.nfsenacional.signer.NfseCertificate;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Cliente HTTP da Sefin Nacional — emissão/consulta de NFS-e e DPS, registro/consulta de eventos.
 * Autenticado por certificado digital A1 (mTLS), sem token.
 * <p>
 * Porte de {@code Nfse\Http\Client\SefinClient} (php-api) — o PHP usa cURL com
 * {@code CURLOPT_SSLCERT}; aqui o client certificate é montado via
 * {@link NfseCertificate#buildMtlsSslContext()} (JSSE nativo, sem `SSL_VERIFYPEER=0`: o servidor
 * é validado contra o trust store padrão da JVM).
 *
 * @author Renato
 */
public class SefinClient {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final String baseUrl;
    private final Gson gson = new Gson();

    public SefinClient(NfseContext context) {
        this(context, certificateFrom(context));
    }

    public SefinClient(NfseContext context, NfseCertificate certificate) {
        SefinEndpointResolver resolver = new SefinEndpointResolver();
        this.baseUrl = trimTrailingSlash(resolver.resolve(context)) + "/";

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

    private static String trimTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public EmissaoNfseResponse emitirNfse(String dpsXmlGZipB64) {
        JsonObject body = new JsonObject();
        body.addProperty("dpsXmlGZipB64", dpsXmlGZipB64);
        JsonObject response = post("nfse", body);

        return EmissaoNfseResponse.builder()
                .tipoAmbiente(intOrNull(response, "tipoAmbiente"))
                .versaoAplicativo(strOrNull(response, "versaoAplicativo"))
                .dataHoraProcessamento(strOrNull(response, "dataHoraProcessamento"))
                .idDps(strOrNull(response, "idDps"))
                .chaveAcesso(strOrNull(response, "chaveAcesso"))
                .nfseXmlGZipB64(strOrNull(response, "nfseXmlGZipB64"))
                .alertas(mapMensagens(response, "alertas"))
                .erros(mapMensagens(response, "erros"))
                .build();
    }

    public ConsultaNfseResponse consultarNfse(String chaveAcesso) {
        JsonObject response = get("nfse/" + chaveAcesso);
        return ConsultaNfseResponse.builder()
                .tipoAmbiente(intOrNull(response, "tipoAmbiente"))
                .versaoAplicativo(strOrNull(response, "versaoAplicativo"))
                .dataHoraProcessamento(strOrNull(response, "dataHoraProcessamento"))
                .chaveAcesso(strOrNull(response, "chaveAcesso"))
                .nfseXmlGZipB64(strOrNull(response, "nfseXmlGZipB64"))
                .build();
    }

    public ConsultaDpsResponse consultarDps(String idDps) {
        JsonObject response = get("dps/" + idDps);
        return ConsultaDpsResponse.builder()
                .tipoAmbiente(intOrNull(response, "tipoAmbiente"))
                .versaoAplicativo(strOrNull(response, "versaoAplicativo"))
                .dataHoraProcessamento(strOrNull(response, "dataHoraProcessamento"))
                .idDps(strOrNull(response, "idDps"))
                .chaveAcesso(strOrNull(response, "chaveAcesso"))
                .build();
    }

    public RegistroEventoResponse registrarEvento(String chaveAcesso, String eventoXmlGZipB64) {
        JsonObject body = new JsonObject();
        body.addProperty("pedidoRegistroEventoXmlGZipB64", eventoXmlGZipB64);
        JsonObject response = post("nfse/" + chaveAcesso + "/eventos", body);
        return toRegistroEventoResponse(response);
    }

    public RegistroEventoResponse consultarEvento(String chaveAcesso, int tipoEvento, int numSeqEvento) {
        JsonObject response = get("nfse/" + chaveAcesso + "/eventos/" + tipoEvento + "/" + numSeqEvento);
        return toRegistroEventoResponse(response);
    }

    /** @return {@code true} se a DPS existir (HTTP 200), {@code false} se não existir (HTTP 404). */
    public boolean verificarDps(String idDps) {
        Request request = new Request.Builder().url(baseUrl + "dps/" + idDps).head().build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 404) {
                return false;
            }
            if (!response.isSuccessful()) {
                throw NfseApiException.requestError("Falha ao verificar DPS: HTTP " + response.code(), response.code());
            }
            return true;
        } catch (java.io.IOException e) {
            throw NfseApiException.requestError("Falha ao verificar DPS: " + e.getMessage(), 0);
        }
    }

    public List<Object> listarEventos(String chaveAcesso) {
        return getAsList("nfse/" + chaveAcesso + "/eventos");
    }

    public List<Object> listarEventosPorTipo(String chaveAcesso, int tipoEvento) {
        return getAsList("nfse/" + chaveAcesso + "/eventos/" + tipoEvento);
    }

    private RegistroEventoResponse toRegistroEventoResponse(JsonObject response) {
        return RegistroEventoResponse.builder()
                .tipoAmbiente(intOrNull(response, "tipoAmbiente"))
                .versaoAplicativo(strOrNull(response, "versaoAplicativo"))
                .dataHoraProcessamento(strOrNull(response, "dataHoraProcessamento"))
                .eventoXmlGZipB64(strOrNull(response, "eventoXmlGZipB64"))
                .build();
    }

    private List<MensagemProcessamentoDto> mapMensagens(JsonObject response, String field) {
        List<MensagemProcessamentoDto> lista = new ArrayList<>();
        if (response == null || !response.has(field) || !response.get(field).isJsonArray()) {
            return lista;
        }
        response.getAsJsonArray(field).forEach(el -> {
            JsonObject m = el.getAsJsonObject();
            lista.add(MensagemProcessamentoDto.builder()
                    .mensagem(strOrNull(m, "mensagem"))
                    .codigo(strOrNull(m, "codigo"))
                    .descricao(strOrNull(m, "descricao"))
                    .complemento(strOrNull(m, "complemento"))
                    .build());
        });
        return lista;
    }

    private JsonObject post(String endpoint, JsonObject jsonBody) {
        RequestBody body = RequestBody.create(gson.toJson(jsonBody), JSON);
        Request request = new Request.Builder()
                .url(baseUrl + endpoint)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        return execute(request);
    }

    private JsonObject get(String endpoint) {
        Request request = new Request.Builder()
                .url(baseUrl + endpoint)
                .get()
                .addHeader("Accept", "application/json")
                .build();
        return execute(request);
    }

    private List<Object> getAsList(String endpoint) {
        Request request = new Request.Builder()
                .url(baseUrl + endpoint)
                .get()
                .addHeader("Accept", "application/json")
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw buildException(response.code(), responseBody);
            }
            Type listType = new TypeToken<List<Object>>() {}.getType();
            List<Object> lista = gson.fromJson(responseBody, listType);
            return lista != null ? lista : new ArrayList<>();
        } catch (java.io.IOException e) {
            throw NfseApiException.requestError(e.getMessage(), 0);
        }
    }

    private JsonObject execute(Request request) {
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw buildException(response.code(), responseBody);
            }
            if (responseBody.trim().isEmpty()) {
                return new JsonObject();
            }
            return gson.fromJson(responseBody, JsonObject.class);
        } catch (java.io.IOException e) {
            throw NfseApiException.requestError(e.getMessage(), 0);
        }
    }

    private NfseApiException buildException(int statusCode, String responseBody) {
        List<MensagemProcessamentoDto> erros = new ArrayList<>();
        try {
            JsonObject decoded = gson.fromJson(responseBody, JsonObject.class);
            erros = mapMensagens(decoded, "erros");
        } catch (Exception ignored) {
            // corpo não é JSON válido — segue sem detalhar erros estruturados.
        }
        return NfseApiException.requestError(
                "Falha na requisição à Sefin: HTTP " + statusCode + (responseBody.isEmpty() ? "" : "\nResposta: " + responseBody),
                statusCode, responseBody, erros);
    }

    private String strOrNull(JsonObject obj, String field) {
        return (obj != null && obj.has(field) && !obj.get(field).isJsonNull()) ? obj.get(field).getAsString() : null;
    }

    private Integer intOrNull(JsonObject obj, String field) {
        return (obj != null && obj.has(field) && !obj.get(field).isJsonNull()) ? obj.get(field).getAsInt() : null;
    }
}
