package io.dialob.client.spi.test.migration;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import io.dialob.client.spi.test.migration.CreateMigrationDB.MigrationProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.resys.thena.docdb.spi.pgsql.sql.PgErrors;
import io.smallrye.mutiny.Uni;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Disabled
@Slf4j
@QuarkusTest
@TestProfile(MigrationProfile.class)
public class CreateMigrationDB {
  private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
  
  @Inject io.vertx.mutiny.pgclient.PgPool pgPool;
  
  public static class MigrationProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
      return Map.of(          
        "quarkus.datasource.devservices.enabled", "false",
        "quarkus.datasource.db-kind", "postgresql", 
        "quarkus.datasource.username", "postgres",
        "quarkus.datasource.password", "example",
        "quarkus.datasource.reactive.max-size", "3",
        "quarkus.datasource.reactive.url", "postgresql://localhost:5432/postgres"
      );
    }

    @Override
    public String getConfigProfile() {
      return "migration-profile";
    }
  }
  
  @Data @Builder
  public static class Sql {
    private String id;
    private String value;
  }
  
  public void setupTables(String ...versions) {

    try {
      for(final var version : versions) {
        final List<Sql> sqls = new ArrayList<>();
        final var resource = resolver.getResource("classpath:migration_postgresql/" + version);
        
        int index = 1;
        
        final var content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        final var splitOn = content.indexOf("@") > 1 ? "@" : ";"; 
        for(final var statement : content.split(splitOn)) {
          if(statement.trim().isEmpty()) {
            continue;
          }
          final var sql = Sql.builder().id(resource.getFilename().substring(0, 4) +  ": " + index++).value(statement).build();
          
          sqls.add(sql);
        }

        
        for(final var sql : sqls) {
          pgPool.
          preparedQuery(sql.getValue()).execute()
              .onItem().transformToUni(data -> Uni.createFrom().voidItem())
              .onFailure().invoke(e -> PgErrors.deadEnd("Can't migrate: " + sql.getId() + " sql:'" + sql.getValue() + "'!", e))
              .await().atMost(Duration.ofSeconds(5));
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  
  
  @Disabled
  @Test
  public void createDatabaseVersion() {
    //connect manually database, user, host -> psql -p 5432 -d postgres -U postgres -h localhost -f dbdump.sql
    
    setupTables(
        "V1_0__init.sql", 
        "V1_1__label.sql",
        "V1_2__tenant.sql", 
        "V1_3__delete-label.sql", 
        "V1_4__questionnaire_owner.sql", 
        "V1_5__raise_form_name_length_to_128.sql", 
        "V1_6__add_form_name_view.sql", 
        "V1_7__add_tag_comment_field.sql", 
        "V1_8__add_tag_type_field.sql", 
        "V1_9__extend_owner_column_to_64.sql"
        );

  }
}
