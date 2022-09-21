package io.dialob.client.spi.migration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;

import io.dialob.client.api.DialobClient;
import io.dialob.client.api.DialobDocument.FormReleaseDocument;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MigrationSupport {
  

  @Data
  @Builder
  public static class Migration {
    private FormReleaseDocument release;
    private String log;
  }
  
  
  private static String BEGIN = "-----BEGIN RELEASE JSON GZIP BASE64-----\r\n";
  private static String END =   "-----END RELEASE JSON GZIP BASE64-----";
  
  private final DialobClient.TypesMapper config;
  
  public FormReleaseDocument read(InputStream input) {
    try {
      var content = new String(input.readAllBytes(), StandardCharsets.UTF_8);
      content = content.substring(content.indexOf(BEGIN) + BEGIN.length(), content.indexOf(END)); 
      final var b64os = new Base64InputStream(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
      final var gzip = new GZIPInputStream(b64os);
      
      return config.readReleaseDoc(new String(gzip.readAllBytes(), StandardCharsets.UTF_8));
    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  
  public void write(Migration migration, OutputStream output) {
    try {
      try {
        
        final var byteArray = new ByteArrayOutputStream();
        final var b64os = new Base64OutputStream(byteArray);
        final var toCompress = config.toJson(migration.getRelease()).getBytes(StandardCharsets.UTF_8);
        final var zipStream = new GZIPOutputStream(b64os);
        zipStream.write(toCompress);
        zipStream.close();
        
        b64os.close();
        byteArray.close();
        final var compressed = byteArray.toByteArray();
        
        output.write(migration.getLog().getBytes(StandardCharsets.UTF_8));
        output.write((System.lineSeparator() + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
        output.write((BEGIN).getBytes(StandardCharsets.UTF_8));
        output.write(compressed);
        output.write((END + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
        output.flush();
      } finally {
        output.close();
      }
    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}