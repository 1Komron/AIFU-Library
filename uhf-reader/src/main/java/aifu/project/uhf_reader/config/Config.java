package aifu.project.uhf_reader.config;


import com.gg.reader.api.dal.GClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public GClient client(){
        return new GClient();
    }
}
