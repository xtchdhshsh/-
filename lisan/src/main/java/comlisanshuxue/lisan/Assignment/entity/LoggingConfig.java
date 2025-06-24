package comlisanshuxue.lisan.Assignment.entity;

import comlisanshuxue.lisan.Assignment.Mapper.AssignmentMapper;
import comlisanshuxue.lisan.Assignment.Service.AssignmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    @Bean
    public Logger assignmentServiceLogger() {
        return LoggerFactory.getLogger(AssignmentService.class);
    }

    @Bean
    public Logger assignmentMapperLogger() {
        return LoggerFactory.getLogger(AssignmentMapper.class);
    }
}