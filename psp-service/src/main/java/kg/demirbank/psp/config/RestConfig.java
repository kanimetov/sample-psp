package kg.demirbank.psp.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import kg.demirbank.psp.security.SignatureInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class RestConfig {
    
    @Value("${operator.timeout.connection:5000}")
    private int connectionTimeout;
    
    @Value("${operator.timeout.read:30000}")
    private int readTimeout;
    
    @Value("${operator.timeout.write:30000}")
    private int writeTimeout;
    
    @Value("${operator.timeout.response:60000}")
    private long responseTimeout;
    
    private final SignatureInterceptor signatureInterceptor;
    
    public RestConfig(SignatureInterceptor signatureInterceptor) {
        this.signatureInterceptor = signatureInterceptor;
    }
    
    @Bean
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                // Connection timeout: max time to establish a connection
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                // Response timeout: max time to wait for a complete response
                .responseTimeout(Duration.ofMillis(responseTimeout))
                // Configure read and write timeouts
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS))
                );
        
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(signatureInterceptor);
    }
}
