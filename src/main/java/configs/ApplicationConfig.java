package configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    DatasourceConfig.class,
    ThymeleafConfig.class
})
public class ApplicationConfig {


}


