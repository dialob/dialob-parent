package io.dialob.client.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.dialob.api.form.Form;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.client.api.DialobClient;
import io.dialob.client.api.DialobClientException;
import io.dialob.client.api.DialobComposerDocument.FormDocument;
import io.dialob.client.api.DialobComposerDocument.FormRevision;
import io.dialob.client.api.ImmutableFormDocument;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class DialobTypesMapperImpl implements DialobClient.TypesMapper {
  private final ObjectMapper objectMapper; 


  @Override
  public String toJson(Object anyObject) {
    try {
      return objectMapper.writeValueAsString(anyObject);
    } catch (IOException e) {
      throw new DialobJsonException(e.getMessage(), e);
    }
  }

  @SuppressWarnings({ "unchecked" })
  @Override
  public Map<String, Serializable> toMap(Object entity) {
    try {
      if(entity instanceof String) {
        return objectMapper.readValue((String) entity, Map.class);
      }
      
      return objectMapper.convertValue(entity, Map.class);
    } catch (Exception e) {
      throw new DialobJsonException(e.getMessage(), e);
    }
  }
  
  @SuppressWarnings({ "unchecked" })
  @Override
  public Map<String, Serializable> toMap(JsonNode entity) {
    try {
      return objectMapper.convertValue(entity, Map.class);
    } catch (Exception e) {
      throw new DialobJsonException(e.getMessage(), e);
    }
  }
  
  @Override
  public Object toType(Object value, Class<?> toType) {
    try {
      return objectMapper.convertValue(value, toType);
    } catch (Exception e) {
      throw new DialobJsonException(e.getMessage(), e);
    }
  }

  @Override
  public FormDocument toForm(String entity) {
    try {
      return ImmutableFormDocument.builder().value(objectMapper.readValue(entity, Form.class)).build();
    } catch (Exception e) {
      throw new DialobJsonException(e.getMessage(), e);
    }
  }

  @Override
  public FormRevision toFormRev(String entity) {
    try {
      return objectMapper.readValue(entity, FormRevision.class);
    } catch (Exception e) {
      throw new DialobJsonException(e.getMessage(), e);
    }
  }
  @Override
  public FormDocument toForm(InputStream entity) {
    try {
      return ImmutableFormDocument.builder().value(objectMapper.readValue(entity, Form.class)).build();
    } catch (Exception e) {
      throw new DialobJsonException(e.getMessage(), e);
    }
  }
  
  @Override
  public Questionnaire toQuestionnaire(InputStream entity) {
    try {
      return objectMapper.readValue(entity, Questionnaire.class);
    } catch (Exception e) {
      throw new DialobJsonException(e.getMessage(), e);
    }
  }
  
  @Override
  public String toString(InputStream entity) {
    try {
      return new String(entity.readAllBytes(), StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new DialobJsonException(e.getMessage(), e);
    }
  }
  
  public static class DialobJsonException extends RuntimeException implements DialobClientException {

    private static final long serialVersionUID = -7154685569622201632L;

    public DialobJsonException(String message, Throwable cause) {
      super(message, cause);
    }

    public DialobJsonException(String message) {
      super(message);
    }
  }
}
