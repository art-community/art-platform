package ru.art.platform.agent.communication

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.config.RequestConfig
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.ssl.SSLContextBuilder
import ru.art.entity.Value
import ru.art.http.client.communicator.HttpCommunicator.httpCommunicator
import ru.art.http.constants.HttpCommonConstants.HTTPS_SCHEME
import ru.art.http.constants.HttpCommonConstants.HTTP_SCHEME
import ru.art.http.constants.MimeToContentTypeMapper.applicationJsonUtf8
import ru.art.platform.agent.model.HttpProxyConfiguration
import ru.art.platform.api.model.resource.ProxyResource
import javax.net.ssl.SSLContext

object HttpProxyCommunicator {
    fun sendHttpRequest(url: String, request: Value, proxy: HttpProxyConfiguration) {
        val sslContext: SSLContext = SSLContextBuilder()
                .loadTrustMaterial(null) { _, _ -> true }
                .build()
        val credentialsProvider = BasicCredentialsProvider().apply {
            if (!proxy.userName.isNullOrBlank() && !proxy.password.isNullOrBlank()) {
                val credentials = UsernamePasswordCredentials(proxy.userName, proxy.password)
                setCredentials(AuthScope(proxy.host, proxy.port), credentials)
            }
        }
        httpCommunicator(url)
                .client(HttpClients.custom()
                        .setDefaultCredentialsProvider(credentialsProvider)
                        .setSSLContext(sslContext)
                        .setConnectionManager(PoolingHttpClientConnectionManager(RegistryBuilder.create<ConnectionSocketFactory>()
                                .register(HTTP_SCHEME, PlainConnectionSocketFactory.INSTANCE)
                                .register(HTTPS_SCHEME, SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
                                .build()
                        ))
                        .build())
                .config(RequestConfig.custom().setProxy(HttpHost(proxy.host, proxy.port)).build())
                .post()
                .produces(applicationJsonUtf8())
                .consumes(applicationJsonUtf8())
                .requestMapper<Value> { value -> value }
                .execute<Value, Any>(request)
    }

    fun sendHttpRequest(url: String, request: Value) {
        httpCommunicator(url)
                .post()
                .produces(applicationJsonUtf8())
                .consumes(applicationJsonUtf8())
                .requestMapper<Value> { value -> value }
                .execute<Value, Any>(request)
    }
}
