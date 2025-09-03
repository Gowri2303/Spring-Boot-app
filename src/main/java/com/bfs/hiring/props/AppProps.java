package com.bfs.hiring.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProps {
    private String name;
    private String regNo;
    private String email;
    private boolean forceBearerPrefix = true;

    private FinalQuery finalQuery = new FinalQuery();

    @Data
    public static class FinalQuery {
        private String odd;
        private String even;
    }
}
