package com.nfsenacional.service;

import com.nfsenacional.NfseContext;
import com.nfsenacional.dto.http.ConsultaDpsResponse;
import com.nfsenacional.dto.http.DistribuicaoDfeResponse;
import com.nfsenacional.dto.http.RegistroEventoResponse;
import com.nfsenacional.dto.http.ResultadoConsultaAliquotasResponse;
import com.nfsenacional.dto.http.ResultadoConsultaConfiguracoesConvenioResponse;
import com.nfsenacional.dto.nfse.DpsData;
import com.nfsenacional.dto.nfse.NfseData;
import com.nfsenacional.dto.nfse.PedRegEventoData;
import com.nfsenacional.http.AdnClient;
import com.nfsenacional.http.NfseApiException;
import com.nfsenacional.http.SefinClient;
import com.nfsenacional.signer.NfseCertificate;
import com.nfsenacional.signer.XmlSigner;
import com.nfsenacional.xml.DpsXmlBuilder;
import com.nfsenacional.xml.EventosXmlBuilder;
import com.nfsenacional.xml.NfseXmlParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Fachada principal do SDK do lado do emissor — emissão/consulta/cancelamento de NFS-e, consulta
 * de DPS, registro/consulta de eventos, e os utilitários do ADN (distribuição de documentos,
 * parâmetros/alíquotas por município).
 * <p>
 * Porte de {@code Nfse\Service\ContribuinteService} (php-api).
 *
 * @author Renato
 */
public class ContribuinteService {

    private final NfseContext context;
    private final SefinClient sefinClient;
    private final AdnClient adnClient;

    public ContribuinteService(NfseContext context) {
        this.context = context;
        this.sefinClient = new SefinClient(context);
        this.adnClient = new AdnClient(context);
    }

    /** Emite uma NFS-e a partir de um DPS: monta o XML, assina, envia, interpreta o retorno. */
    public NfseData emitir(DpsData dps) {
        DpsXmlBuilder builder = new DpsXmlBuilder();
        String xml = builder.build(dps);

        NfseCertificate cert = makeCertificate();
        XmlSigner signer = new XmlSigner(cert);

        // Assina a tag 'infDPS'.
        String signedXml = signer.sign(xml, "infDPS");

        // Envelope (GZIP + Base64).
        String payload = gzipBase64(signedXml);

        // Transporte.
        com.nfsenacional.dto.http.EmissaoNfseResponse response = sefinClient.emitirNfse(payload);

        if (response.getErros() != null && !response.getErros().isEmpty()) {
            throw NfseApiException.responseError("Erro na emissão: " + response.getErros());
        }

        if (response.getNfseXmlGZipB64() == null || response.getNfseXmlGZipB64().isEmpty()) {
            throw NfseApiException.responseError("Resposta sem XML da NFS-e.");
        }

        String nfseXml = gunzipBase64(response.getNfseXmlGZipB64());

        return new NfseXmlParser().parse(nfseXml);
    }

    /** Consulta uma NFS-e já emitida pela chave de acesso. {@code null} se não encontrada. */
    public NfseData consultar(String chave) {
        com.nfsenacional.dto.http.ConsultaNfseResponse response;
        try {
            response = sefinClient.consultarNfse(chave);
        } catch (NfseApiException e) {
            return null;
        }

        if (response.getNfseXmlGZipB64() == null || response.getNfseXmlGZipB64().isEmpty()) {
            return null;
        }

        String nfseXml = gunzipBase64(response.getNfseXmlGZipB64());
        return new NfseXmlParser().parse(nfseXml);
    }

    public ConsultaDpsResponse consultarDps(String idDps) {
        return sefinClient.consultarDps(idDps);
    }

    public boolean verificarDps(String idDps) {
        return sefinClient.verificarDps(idDps);
    }

    public RegistroEventoResponse registrarEvento(String chaveAcesso, String eventoXmlGZipB64) {
        return sefinClient.registrarEvento(chaveAcesso, eventoXmlGZipB64);
    }

    /** Monta o XML do evento a partir do DTO, assina e registra. */
    public RegistroEventoResponse registrarEventoData(PedRegEventoData evento) {
        EventosXmlBuilder builder = new EventosXmlBuilder();
        String xml = builder.buildPedRegEvento(evento);

        NfseCertificate cert = makeCertificate();
        XmlSigner signer = new XmlSigner(cert);

        // Assina a tag 'infPedReg'.
        String signedXml = signer.sign(xml, "infPedReg");

        String payload = gzipBase64(signedXml);

        return registrarEvento(evento.getInfPedReg().getChaveNfse(), payload);
    }

    /** Atalho para cancelamento de NFS-e (Evento 101101). */
    public RegistroEventoResponse cancelar(PedRegEventoData evento) {
        // Garante o código do evento de cancelamento.
        evento.getInfPedReg().setTipoEvento("101101");
        return registrarEventoData(evento);
    }

    public RegistroEventoResponse consultarEvento(String chaveAcesso, int tipoEvento, int numSeqEvento) {
        return sefinClient.consultarEvento(chaveAcesso, tipoEvento, numSeqEvento);
    }

    public List<Object> listarEventos(String chaveAcesso) {
        return sefinClient.listarEventos(chaveAcesso);
    }

    public List<Object> listarEventos(String chaveAcesso, Integer tipoEvento) {
        return tipoEvento != null
                ? sefinClient.listarEventosPorTipo(chaveAcesso, tipoEvento)
                : sefinClient.listarEventos(chaveAcesso);
    }

    /**
     * @deprecated a API oficial de geração de DANFSe do ambiente nacional será descontinuada em
     * 01/07/2026 — ver {@link AdnClient#obterDanfse(String)}.
     */
    @Deprecated
    public String downloadDanfse(String chaveAcesso) {
        return adnClient.obterDanfse(chaveAcesso);
    }

    // ===== ADN Contribuinte =====

    public DistribuicaoDfeResponse baixarDfe(int nsu, String cnpjConsulta, boolean lote) {
        return adnClient.baixarDfeContribuinte(nsu, cnpjConsulta, lote);
    }

    public com.google.gson.JsonElement consultarEventos(String chaveAcesso) {
        return adnClient.consultarEventosContribuinte(chaveAcesso);
    }

    public ResultadoConsultaConfiguracoesConvenioResponse consultarParametrosConvenio(String codigoMunicipio) {
        return adnClient.consultarParametrosConvenio(codigoMunicipio);
    }

    public ResultadoConsultaAliquotasResponse consultarAliquota(String codigoMunicipio, String codigoServico, String competencia) {
        return adnClient.consultarAliquota(codigoMunicipio, codigoServico, competencia);
    }

    public ResultadoConsultaAliquotasResponse consultarHistoricoAliquotas(String codigoMunicipio, String codigoServico) {
        return adnClient.consultarHistoricoAliquotas(codigoMunicipio, codigoServico);
    }

    public com.google.gson.JsonElement consultarBeneficio(String codigoMunicipio, String numeroBeneficio, String competencia) {
        return adnClient.consultarBeneficio(codigoMunicipio, numeroBeneficio, competencia);
    }

    public com.google.gson.JsonElement consultarRegimesEspeciais(String codigoMunicipio, String codigoServico, String competencia) {
        return adnClient.consultarRegimesEspeciais(codigoMunicipio, codigoServico, competencia);
    }

    public com.google.gson.JsonElement consultarRetencoes(String codigoMunicipio, String competencia) {
        return adnClient.consultarRetencoes(codigoMunicipio, competencia);
    }

    private NfseCertificate makeCertificate() {
        return context.getCertificateContent() != null
                ? NfseCertificate.fromContent(context.getCertificateContent(), context.getCertificatePassword())
                : new NfseCertificate(context.getCertificatePath(), context.getCertificatePassword());
    }

    private String gzipBase64(String content) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
                gzip.write(content.getBytes(StandardCharsets.UTF_8));
            }
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Falha ao compactar (gzip) XML: " + e.getMessage(), e);
        }
    }

    private String gunzipBase64(String base64GzipContent) {
        try {
            byte[] compressed = Base64.getDecoder().decode(base64GzipContent);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = gzip.read(buffer)) != -1) {
                    baos.write(buffer, 0, read);
                }
            }
            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao descompactar (gunzip) XML: " + e.getMessage(), e);
        }
    }
}
