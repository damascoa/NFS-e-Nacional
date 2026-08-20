package com.nfsenacional.danfse;

import com.nfsenacional.dto.nfse.NfseData;
import com.nfsenacional.xml.NfseXmlParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica que o DANFSe é gerado com sucesso a partir de uma NFS-e real (mesma XML usada em
 * {@code NfseXmlParserRealDataTest}) — não compara byte a byte com o PDF de exemplo do PHP
 * (fontes/layout podem variar sutilmente entre engines), só garante que sai um PDF válido e
 * não-vazio a partir de dados reais passando por todo o pipeline: parse XML → DTOs → parâmetros → PDF.
 *
 * @author Renato
 */
class DanfseGeneratorTest {

    @Test
    void geraPdfValidoAPartirDeNfseReal() throws IOException {
        String xml;
        try (InputStream in = getClass().getResourceAsStream("/nfse-real-exemplo.xml")) {
            assertTrue(in != null, "Fixture nfse-real-exemplo.xml não encontrada em src/test/resources");
            xml = new String(lerTudo(in), StandardCharsets.UTF_8);
        }

        NfseData nfseData = new NfseXmlParser().parse(xml);

        byte[] pdf = new DanfseGenerator().gerarPdf(nfseData, Collections.emptyMap());

        assertTrue(pdf.length > 1000, "PDF gerado parece vazio demais: " + pdf.length + " bytes");
        String header = new String(pdf, 0, 5, StandardCharsets.ISO_8859_1);
        assertTrue(header.equals("%PDF-"), "Arquivo gerado não começa com o header de PDF: " + header);
    }

    private static byte[] lerTudo(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[8192];
        int lidos;
        while ((lidos = in.read(chunk)) != -1) {
            buffer.write(chunk, 0, lidos);
        }
        return buffer.toByteArray();
    }
}
