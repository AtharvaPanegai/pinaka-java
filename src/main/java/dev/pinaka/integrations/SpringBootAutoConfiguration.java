package dev.pinaka.integrations;

import dev.pinaka.Pinaka;
import dev.pinaka.PinakaConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
@EnableConfigurationProperties(PinakaProperties.class)
public class SpringBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PinakaFilter pinakaFilter(PinakaProperties props) {
        PinakaConfig config = PinakaConfig.builder(props.getApiKey(), props.getService())
                .environment(props.getEnvironment())
                .release(props.getRelease())
                .maxLogLines(props.getMaxLogLines())
                .debug(props.isDebug())
                .enabled(props.isEnabled())
                .build();
        Pinaka.init(config);
        return new PinakaFilter();
    }
}
