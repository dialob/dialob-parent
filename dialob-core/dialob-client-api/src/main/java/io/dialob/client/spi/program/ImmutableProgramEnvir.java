package io.dialob.client.spi.program;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.dialob.client.api.DialobClient.ProgramEnvir;
import io.dialob.client.api.DialobClient.ProgramWrapper;
import io.dialob.client.api.DialobClientException;
import io.dialob.spi.Constants;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImmutableProgramEnvir implements ProgramEnvir {
  
  private final Map<String, List<ProgramWrapper>> byFormId;

  @Override
  public ProgramWrapper findByFormId(String formId) {
    if(!byFormId.containsKey(formId)) {
      final var ids = String.join(",", byFormId.keySet());
      throw new ProgramNotFoundFromEnvir("No program by formId: '" + formId + "', known forms: '" + ids + "'!");
    }
    final var values = byFormId.get(formId);
    if(values.size() > 1) {
      final var revs = String.join(",", values.stream().map(e -> e.getAst().get().getValue().getRev()).collect(Collectors.toList()));
      throw new ProgramHasMultipleRevs("Program by formId: '" + formId + "' requires rev because of multpile revisions in use: '" + revs + "'!"); 
    }
    return values.get(0);
  }

  @Override
  public ProgramWrapper findByFormIdAndRev(String formId, String formRev) {
    if(formRev == null) {
      return findByFormId(formId);
    }
    
    
    if(!byFormId.containsKey(formId)) {
      final var ids = String.join(",", byFormId.keySet());
      throw new ProgramNotFoundFromEnvir("No program by formId: '" + formId + "', known forms: '" + ids + "'!");
    }
    final var values = byFormId.get(formId);
    final Optional<ProgramWrapper> result;
    if(Constants.LATEST_REV.equals(formRev)) {
      final var fallback1 = values.stream().filter(r -> formRev.equals(r.getAst().get().getValue().getRev())).findFirst();
      if(fallback1.isEmpty()) {
        final var byCreated = values.stream().sorted((b, a) -> 
          a.getAst().get().getValue().getMetadata().getCreated().compareTo(b.getAst().get().getValue().getMetadata().getCreated())
        ).collect(Collectors.toList());
        
        result = Optional.ofNullable(byCreated.isEmpty() ? null : byCreated.get(0));
        
      } else {
        result = fallback1;        
      }
    } else {
      result = values.stream().filter(r -> formRev.equals(r.getAst().get().getValue().getRev())).findFirst();  
    }
    
    if(result.isEmpty()) {
      final var revs = String.join(",", values.stream().map(e -> e.getAst().get().getValue().getRev()).collect(Collectors.toList()));      
      throw new ProgramRevNotFound("Program by formId: '" + formId + "' revision: '" + formRev + "' not found, revisions in use: '" + revs + "'!");
    }

    return result.get();
  }

  @Override
  public List<ProgramWrapper> findAll() {
    return byFormId.values().stream().flatMap(e -> e.stream()).collect(Collectors.toList());
  }

  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    
    private final Map<String, List<ProgramWrapper>> byFormId = new HashMap<>();

   
    public Builder add(ProgramWrapper wrapper) {
      final var form = wrapper.getAst().get();
      
      final var revs = Optional.ofNullable(byFormId.get(form.getValue().getId())).orElseGet(() -> {
        final var values = new ArrayList<ProgramWrapper>();
        byFormId.put(form.getValue().getId(), values);
        return values;
      });
      revs.add(wrapper);
      return this;
    }
    
    public ProgramEnvir build() {
      return new ImmutableProgramEnvir(Collections.unmodifiableMap(byFormId));
    }
  }
  
  public static class ProgramNotFoundFromEnvir extends IllegalArgumentException implements DialobClientException {
    private static final long serialVersionUID = 6305063707279384796L;
    public ProgramNotFoundFromEnvir(String s) {
      super(s);
    }
  }
  
  public static class ProgramHasMultipleRevs extends IllegalArgumentException implements DialobClientException {
    private static final long serialVersionUID = 6305063707279384796L;
    public ProgramHasMultipleRevs(String s) {
      super(s);
    }
  }
  public static class ProgramRevNotFound extends IllegalArgumentException implements DialobClientException {
    private static final long serialVersionUID = 6305063707279384796L;
    public ProgramRevNotFound(String s) {
      super(s);
    }
  }
}
