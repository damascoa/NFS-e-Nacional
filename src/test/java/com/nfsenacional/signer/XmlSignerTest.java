package com.nfsenacional.signer;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verificação isolada da Etapa 3 (TASKS.md): assina um XML de exemplo com um certificado de teste
 * e valida a assinatura resultante de volta, usando a própria API {@code javax.xml.crypto.dsig}
 * como "relying party" — não depende de acesso à Sefin real.
 */
class XmlSignerTest {

    private static final String XML_EXEMPLO =
            "<DPS xmlns=\"http://www.sped.fazenda.gov.br/nfse\" versao=\"1.00\">"
            + "<infDPS Id=\"DPS3550308212345678000199000010000000000000001\">"
            + "<tpAmb>2</tpAmb>"
            + "<dhEmi>2026-01-01T10:00:00-03:00</dhEmi>"
            + "</infDPS>"
            + "</DPS>";

    @Test
    void assinaEValidaAssinaturaDeVolta() throws Exception {
        NfseCertificate cert = new NfseCertificate(
                "src/test/resources/certificado-teste.p12", "senha123");

        XmlSigner signer = new XmlSigner(cert);
        String xmlAssinado = signer.sign(XML_EXEMPLO, "infDPS");

        assertTrue(xmlAssinado.contains("<Signature"), "deve conter a tag <Signature>");
        assertTrue(xmlAssinado.contains("<SignatureValue>"), "deve conter <SignatureValue>");
        assertTrue(xmlAssinado.contains("<X509Certificate>"), "deve conter o certificado embutido");
        assertTrue(!xmlAssinado.contains("\n"), "não deve ter quebras de linha (mesmo comportamento do PHP)");

        // Valida a assinatura de volta, como um "relying party" faria — mesmo parsing
        // (namespace-aware) usado no momento de assinar.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(
                new ByteArrayInputStream(xmlAssinado.getBytes("UTF-8")));

        // Precisa marcar o atributo Id como tipo ID de novo pra validação resolver a Reference "#...".
        NodeList infDps = doc.getElementsByTagName("infDPS");
        org.w3c.dom.Element infDpsEl = (org.w3c.dom.Element) infDps.item(0);
        infDpsEl.setIdAttribute("Id", true);

        NodeList sigNodes = doc.getElementsByTagName("Signature");
        assertTrue(sigNodes.getLength() == 1, "deve ter exatamente uma assinatura");

        DOMValidateContext valContext = new DOMValidateContext(cert.getCertificate().getPublicKey(), sigNodes.item(0));
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
        XMLSignature signature = fac.unmarshalXMLSignature(valContext);

        boolean valido = signature.validate(valContext);
        assertTrue(valido, "a assinatura deve validar contra o certificado público");
    }
}
