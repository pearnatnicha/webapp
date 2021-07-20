package io.muzoo.ssc.hw4.config;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ConfigProperties {
    private String databaseDriverClassName;
    private String databaseConnectionUrl;
    private String databaseDatabaseUsername;
    private String databasePassword;
}
