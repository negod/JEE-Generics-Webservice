/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.backede.webservice.service.client.restclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Optional;
import java.util.logging.Level;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joakim Backede ( joakim.backede@outlook.com )
 */
public interface SSLClient {

    final Logger log = LoggerFactory.getLogger(SSLClient.class);

    public String getKeyStorePath();

    public String getCertPath();

    public default Optional<Client> getSslClient() {
        FileInputStream certFileStream = null;
        FileInputStream keyFileStream = null;
        
        try {
            // load the certificate
            File certFile = new File(getCertPath());
            certFileStream = new FileInputStream(certFile);

            File keyFile = new File(getKeyStorePath());
            keyFileStream = new FileInputStream(keyFile);

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = certificateFactory.generateCertificate(certFileStream);

            // load the keystore that includes self-signed cert as a "trusted" entry
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(keyFileStream, "noipoiblapla".toCharArray());
            keyStore.setCertificateEntry("192.168.2.140", certificate);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "noipoiblapla".toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            keyStore.setCertificateEntry("192.168.2.140", certificate);

            trustManagerFactory.init(keyStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());

            // build the rest client
            ClientBuilder builder = ClientBuilder.newBuilder();
            builder.sslContext(sslContext);

            return Optional.ofNullable(builder.build());
        } catch (FileNotFoundException ex) {
            log.error("File not found when creating ssl client. FILEPATH: {} ERROR: {}", getKeyStorePath(), ex);
        } catch (CertificateException | KeyStoreException | NoSuchAlgorithmException | KeyManagementException | IOException ex) {
            log.error("Error when creating ssl client. ERROR: {}", ex);
        } catch (UnrecoverableKeyException ex) {
            java.util.logging.Logger.getLogger(SSLClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                certFileStream.close();
                keyFileStream.close();
            } catch (IOException ex) {
                log.error("Error when closing fileStram when building ssl client. ERROR: {}", ex);
            }
        }
        return Optional.empty();
    }

}
