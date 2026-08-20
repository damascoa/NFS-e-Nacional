package com.nfsenacional.signer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Assina uma tag específica de um XML (enveloped XMLDSig), do jeito exigido pelo Sistema Nacional
 * NFS-e: {@code infDPS} na emissão da DPS, {@code infPedReg} no registro de eventos.
 * <p>
 * Porte funcional de {@code Nfse\Signer\XmlSigner} (php-api) — o PHP monta a
 * {@code <Signature>} manualmente via DOM + OpenSSL; aqui usamos a API pública
 * {@code javax.xml.crypto.dsig} (XML Digital Signature, nativa do JDK desde o Java 6), que produz
 * o mesmo resultado (enveloped signature, C14N 1.0 "inclusive", RSA-SHA1 ou RSA-SHA256) seguindo o
 * padrão W3C, sem depender de classes internas do JDK.
 * <p>
 * <b>Ponto de atenção (ver TASKS.md, Etapa 3):</b> o {@code XmlSigner.php} de origem declara o
 * algoritmo de canonicalização como "inclusive" ({@code REC-xml-c14n-20010315}) na tag
 * {@code CanonicalizationMethod}, mas calcula o digest/assinatura chamando
 * {@code DOMNode::C14N(exclusive: true, ...)} — uma inconsistência sutil do código PHP. Esta
 * implementação é internamente consistente: declara e aplica C14N "inclusive" em ambos os pontos
 * (SignedInfo e Reference/Transform), que é o que a Sefin de fato valida (ela concilia a partir da
 * URI declarada). Ainda assim, **validar contra homologação real antes de ir pra produção**.
 *
 * @author Renato
 */
public class XmlSigner {

    public static final String ALGORITHM_SHA1 = DigestMethod.SHA1;
    public static final String ALGORITHM_SHA256 = DigestMethod.SHA256;

    private static final String SIGNATURE_METHOD_RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    private static final String SIGNATURE_METHOD_RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";

    private final NfseCertificate certificate;

    public XmlSigner(NfseCertificate certificate) {
        this.certificate = certificate;
    }

    /** Assina usando SHA-1 (padrão histórico do schema nacional) e atributo {@code Id}. */
    public String sign(String xmlContent, String tagName) {
        return sign(xmlContent, tagName, "Id", ALGORITHM_SHA1);
    }

    /**
     * @param xmlContent      XML de entrada (não assinado)
     * @param tagName         nome da tag a assinar (ex: {@code infDPS}, {@code infPedReg})
     * @param idAttributeName nome do atributo que identifica o nó (ex: {@code Id})
     * @param digestAlgorithm {@link #ALGORITHM_SHA1} ou {@link #ALGORITHM_SHA256}
     * @return XML assinado, sem quebras de linha (mesmo comportamento do PHP)
     */
    public String sign(String xmlContent, String tagName, String idAttributeName, String digestAlgorithm) {
        if (xmlContent == null || xmlContent.isEmpty()) {
            throw new NfseSignerException("Conteúdo XML vazio.");
        }

        try {
            Document doc = parse(xmlContent);

            NodeList nodes = doc.getElementsByTagName(tagName);
            if (nodes.getLength() == 0) {
                throw new NfseSignerException("Tag " + tagName + " não encontrada para assinatura.");
            }
            Element target = (Element) nodes.item(0);

            String idValue = target.getAttribute(idAttributeName);
            if (idValue == null || idValue.isEmpty()) {
                throw new NfseSignerException("Tag a ser assinada deve possuir um atributo '" + idAttributeName + "'.");
            }
            // Registra o atributo como tipo ID no DOM, pra resolução de "#idValue" na Reference funcionar.
            target.setIdAttribute(idAttributeName, true);

            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

            List<Transform> transforms = Arrays.asList(
                    fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null),
                    fac.newTransform(CanonicalizationMethod.INCLUSIVE, (TransformParameterSpec) null)
            );

            Reference reference = fac.newReference(
                    "#" + idValue,
                    fac.newDigestMethod(digestAlgorithm, null),
                    transforms,
                    null,
                    null
            );

            String signatureMethod = ALGORITHM_SHA256.equals(digestAlgorithm)
                    ? SIGNATURE_METHOD_RSA_SHA256
                    : SIGNATURE_METHOD_RSA_SHA1;

            SignedInfo signedInfo = fac.newSignedInfo(
                    fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                    fac.newSignatureMethod(signatureMethod, null),
                    Collections.singletonList(reference)
            );

            KeyInfoFactory kif = fac.getKeyInfoFactory();
            X509Certificate x509Certificate = certificate.getCertificate();
            X509Data x509Data = kif.newX509Data(Collections.singletonList(x509Certificate));
            KeyInfo keyInfo = kif.newKeyInfo(Collections.singletonList(x509Data));

            XMLSignature signature = fac.newXMLSignature(signedInfo, keyInfo);

            // Insere <Signature> como filho do pai do nó assinado — mesmo ponto onde o PHP faz
            // node.parentNode.appendChild(signatureNode).
            DOMSignContext signContext = new DOMSignContext(certificate.getPrivateKey(), target.getParentNode());

            signature.sign(signContext);

            return serialize(doc);
        } catch (NfseSignerException e) {
            throw e;
        } catch (Exception e) {
            throw new NfseSignerException("Falha ao assinar XML: " + e.getMessage(), e);
        }
    }

    private Document parse(String xmlContent) throws Exception {
        // Namespace-aware é obrigatório: a implementação de referência do JDK para
        // javax.xml.crypto.dsig (usada tanto aqui quanto por quem for validar a assinatura depois)
        // exige um Document DOM Level 2 namespace-aware — inclusive o XML real da NFS-e nacional
        // declara namespace default no elemento raiz.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(xmlContent.getBytes("UTF-8")));
    }

    private String serialize(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        StringWriter sw = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(sw));
        // Mesmo comportamento do PHP: remove quebras de linha/tab do XML final.
        return sw.toString().replace("\n", "").replace("\r", "").replace("\t", "");
    }
}
