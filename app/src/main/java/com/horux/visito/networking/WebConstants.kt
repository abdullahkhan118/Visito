package com.horux.visito.networking

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLException
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException

object WebConstants {
    const val STRING_WEATHER_API_KEY = "ab1e19de29fc494db3f185340222001"
    const val STRING_WEATHER_BASE_URL = "http://api.weatherapi.com/v1/"
    const val STRING_TOM_TOM_BASE_URL = "https://api.tomtom.com/"
    const val STRING_TOM_TOM_API_KEY = "1oAirRLOnqYjvPSblLJzCl47fccUktb3"
    const val STRING_RADAR_API_BASE_URL = "https://api.radar.io/v1/"
    const val STRING_RADAR_API_KEY = "prj_live_pk_4d53dfe3361cf12b5f653ae8acd3d6898be109d2"
    const val STRING_MODES_KEY = "car"
    const val STRING_UNITS_KEY = "metric"
    const val STRING_SEARCH_URL = "search/autocomplete?"
    const val STRING_PLACES_URL = "search/places?"
    const val STRING_GEOFENCE_URL = "search/geofences?"
    const val STRING_ROUTE_URL = "route/distance?"
    const val STRING_CONTEXT_URL = "context?"
    const val HEADER_AUTHORIZATION = "Authorization"
    const val PARAM_COORDINATES = "coordinates"
    const val PARAM_NEAR = "near"
    const val PARAM_LIMIT = "limit"
    const val PARAM_CATEGORIES = "categories"
    const val PARAM_QUERY = "query"
    const val PARAM_TAGS = "tags"
    const val PARAM_METADATA = "metadata[offers]"
    const val PARAM_RADIUS = "radius"
    const val PARAM_ORIGIN = "origin"
    const val PARAM_DESTINATION = "destination"
    const val PARAM_MODES = "modes"
    const val PARAM_UNITS = "units"
    private var retrofit: Retrofit? = null
    private var retrofitTomTom: Retrofit? = null
    private var retrofitWeather: Retrofit? = null
    val retrofitInstance: Retrofit?
        get() {
            if (retrofit != null) return retrofit
            val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            val gson = Gson()
            retrofit = Retrofit.Builder()
                .baseUrl(STRING_TOM_TOM_BASE_URL)
                .client(buildClient(builder))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            return retrofit
        }
    val retrofitTomTomInstance: Retrofit?
        get() {
            if (retrofitTomTom != null) {
                return retrofitTomTom
            }
            val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            val gson = Gson()
            retrofitTomTom = Retrofit.Builder()
                .baseUrl(STRING_TOM_TOM_BASE_URL)
                .client(buildClient(builder))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            return retrofitTomTom
        }
    val retrofitWeatherInstance: Retrofit?
        get() {
            if (retrofitWeather != null) {
                return retrofitWeather
            }
            val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            val gson = Gson()
            retrofitWeather = Retrofit.Builder()
                .baseUrl(STRING_WEATHER_BASE_URL)
                .client(buildClient(builder))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            return retrofitWeather
        }

    private fun buildClient(builder: OkHttpClient.Builder): OkHttpClient {
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        builder.addInterceptor(logging);
        val certificate: SelfSignedCertificate
        try {
            certificate = WebConstants.certificate
            val trustManager: X509TrustManager = object : X509TrustManager {
                @Throws(java.security.cert.CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                    chain[0] = certificate.cert()!!
                }

                @Throws(java.security.cert.CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                    chain[0] = certificate.cert()!!
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf(certificate.cert()!!)
                }
            }
            builder.sslSocketFactory(
                SSLSocketFactory.getDefault() as SSLSocketFactory,
                trustManager
            )
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: SSLException) {
            e.printStackTrace()
        } catch (e: java.security.cert.CertificateException) {
            e.printStackTrace()
        }
        return builder.build()
    }

    @get:Throws(
        CertificateException::class,
        SSLException::class,
        java.security.cert.CertificateException::class
    )
    val certificate: SelfSignedCertificate
        get() = SelfSignedCertificate(com.horux.visito.BuildConfig.APPLICATION_ID)
}
