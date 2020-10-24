package it.hxl.sql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JDBCConfig {
    private String driverClass;
    private String url;
    private String username;
    private String password;
}
