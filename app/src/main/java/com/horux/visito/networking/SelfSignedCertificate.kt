package com.horux.visito.networking

import android.util.Base64
import android.util.Log
import org.spongycastle.asn1.x500.X500Name
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.Provider
import java.security.SecureRandom
import java.security.cert.CertificateEncodingException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Date

class SelfSignedCertificate @JvmOverloads constructor(
    fqdn: String,
    random: SecureRandom,
    bits: Int,
    notBefore: Date? = DEFAULT_NOT_BEFORE,
    notAfter: Date? = DEFAULT_NOT_AFTER
) {
    private val certificate: File
    private val privateKey: File
    private var cert: X509Certificate? = null
    private val key: PrivateKey
    /**
     * Creates a new instance.
     *
     * @param notBefore Certificate is not valid before this time
     * @param notAfter  Certificate is not valid after this time
     */
    /**
     * Creates a new instance.
     */
    @JvmOverloads
    constructor(
        notBefore: Date? = DEFAULT_NOT_BEFORE,
        notAfter: Date? = DEFAULT_NOT_AFTER
    ) : this("example.com", notBefore, notAfter)
    /**
     * Creates a new instance.
     *
     * @param fqdn      a fully qualified domain name
     * @param notBefore Certificate is not valid before this time
     * @param notAfter  Certificate is not valid after this time
     */
    /**
     * Creates a new instance.
     *
     * @param fqdn a fully qualified domain name
     */
    @JvmOverloads
    constructor(
        fqdn: String,
        notBefore: Date? = DEFAULT_NOT_BEFORE,
        notAfter: Date? = DEFAULT_NOT_AFTER
    ) : this(fqdn, SecureRandom(), DEFAULT_KEY_LENGTH_BITS, notBefore, notAfter)
    /**
     * Creates a new instance.
     *
     * @param fqdn      a fully qualified domain name
     * @param random    the [java.security.SecureRandom] to use
     * @param bits      the number of bits of the generated private key
     * @param notBefore Certificate is not valid before this time
     * @param notAfter  Certificate is not valid after this time
     */
    /**
     * Creates a new instance.
     *
     * @param fqdn   a fully qualified domain name
     * @param random the [java.security.SecureRandom] to use
     * @param bits   the number of bits of the generated private key
     */
    init {
        // Generate an RSA key pair.
        val keypair: KeyPair
        keypair = try {
            val keyGen = KeyPairGenerator.getInstance("RSA")
            keyGen.initialize(bits, random)
            keyGen.generateKeyPair()
        } catch (e: NoSuchAlgorithmException) {
            // Should not reach here because every Java implementation must have RSA key pair generator.
            throw Error(e)
        }
        val paths: Array<String>
        paths = try {
            // Try Bouncy Castle if the current JVM didn't have sun.security.x509.
            generateCertificate(fqdn, keypair, random, notBefore, notAfter)
        } catch (t2: Throwable) {
            Log.d(
                TAG,
                "Failed to generate a self-signed X.509 certificate using Bouncy Castle:",
                t2
            )
            throw CertificateException(
                "No provider succeeded to generate a self-signed certificate. See debug log for the root cause.",
                t2
            )
        }
        certificate = File(paths[0])
        privateKey = File(paths[1])
        key = keypair.private
        var certificateInput: FileInputStream? = null
        try {
            certificateInput = FileInputStream(certificate)
            cert = CertificateFactory.getInstance("X509")
                .generateCertificate(certificateInput) as X509Certificate
        } catch (e: Exception) {
            throw CertificateEncodingException(e)
        } finally {
            if (certificateInput != null) {
                try {
                    certificateInput.close()
                } catch (e: IOException) {
                    Log.w(TAG, "Failed to close a file: $certificate", e)
                }
            }
        }
    }

    /**
     * Returns the generated X.509 certificate file in PEM format.
     */
    fun certificate(): File {
        return certificate
    }

    /**
     * Returns the generated RSA private key file in PEM format.
     */
    fun privateKey(): File {
        return privateKey
    }

    /**
     * Returns the generated X.509 certificate.
     */
    fun cert(): X509Certificate? {
        return cert
    }

    /**
     * Returns the generated RSA private key.
     */
    fun key(): PrivateKey {
        return key
    }

    /**
     * Deletes the generated X.509 certificate file and RSA private key file.
     */
    fun delete() {
        safeDelete(certificate)
        safeDelete(privateKey)
    }

    companion object {
        private val TAG = SelfSignedCertificate::class.java.simpleName

        /**
         * Current time minus 1 year, just in case software clock goes back due to time synchronization
         */
        private val DEFAULT_NOT_BEFORE = Date(System.currentTimeMillis() - 86400000L * 365)

        /**
         * The maximum possible value in X.509 specification: 9999-12-31 23:59:59
         */
        private val DEFAULT_NOT_AFTER = Date(253402300799000L)

        /**
         * FIPS 140-2 encryption requires the key length to be 2048 bits or greater.
         * Let's use that as a sane default but allow the default to be set dynamically
         * for those that need more stringent security requirements.
         */
        private const val DEFAULT_KEY_LENGTH_BITS = 2048

        /**
         * FQDN to use if none is specified.
         */
        private const val DEFAULT_FQDN = "example.com"

        /**
         * 7-bit ASCII, as known as ISO646-US or the Basic Latin block of the
         * Unicode character set
         */
        private val US_ASCII = Charset.forName("US-ASCII")
        private val provider: Provider = BouncyCastleProvider()
        @Throws(Exception::class)
        private fun generateCertificate(
            fqdn: String,
            keypair: KeyPair,
            random: SecureRandom,
            notBefore: Date?,
            notAfter: Date?
        ): Array<String> {
            val key = keypair.private

            // Prepare the information required for generating an X.509 certificate.
            val owner = X500Name("CN=$fqdn")
            val builder: X509v3CertificateBuilder = JcaX509v3CertificateBuilder(
                owner, BigInteger(64, random), notBefore, notAfter, owner, keypair.public
            )
            val signer: ContentSigner =
                JcaContentSignerBuilder("SHA256WithRSAEncryption").build(key)
            val certHolder: X509CertificateHolder = builder.build(signer)
            val cert: X509Certificate =
                JcaX509CertificateConverter().setProvider(provider).getCertificate(certHolder)
            cert.verify(keypair.public)
            return newSelfSignedCertificate(fqdn, key, cert)
        }

        @Throws(IOException::class, CertificateEncodingException::class)
        private fun newSelfSignedCertificate(
            fqdn: String,
            key: PrivateKey,
            cert: X509Certificate
        ): Array<String> {
            val keyText = """
                -----BEGIN PRIVATE KEY-----
                ${Base64.encodeToString(key.encoded, Base64.DEFAULT)}
                -----END PRIVATE KEY-----
                
                """.trimIndent()
            val keyFile = File.createTempFile("keyutil_" + fqdn + '_', ".key")
            keyFile.deleteOnExit()
            var keyOut: OutputStream? = FileOutputStream(keyFile)
            keyOut = try {
                keyOut!!.write(keyText.toByteArray(US_ASCII))
                keyOut.close()
                null
            } finally {
                if (keyOut != null) {
                    safeClose(keyFile, keyOut)
                    safeDelete(keyFile)
                }
            }
            val certText = """
                -----BEGIN CERTIFICATE-----
                ${Base64.encodeToString(cert.encoded, Base64.DEFAULT)}
                -----END CERTIFICATE-----
                
                """.trimIndent()
            val certFile = File.createTempFile("keyutil_" + fqdn + '_', ".crt")
            certFile.deleteOnExit()
            var certOut: OutputStream? = FileOutputStream(certFile)
            certOut = try {
                certOut!!.write(certText.toByteArray(US_ASCII))
                certOut.close()
                null
            } finally {
                if (certOut != null) {
                    safeClose(certFile, certOut)
                    safeDelete(certFile)
                    safeDelete(keyFile)
                }
            }
            return arrayOf(certFile.path, keyFile.path)
        }

        private fun safeDelete(certFile: File) {
            if (!certFile.delete()) {
                Log.w(TAG, "Failed to delete a file: $certFile")
            }
        }

        private fun safeClose(keyFile: File, keyOut: OutputStream) {
            try {
                keyOut.close()
            } catch (e: IOException) {
                Log.w(TAG, "Failed to close a file: $keyFile", e)
            }
        }
    }
}
