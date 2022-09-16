package io.dialob.client.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;

import io.dialob.api.form.Form;
import io.dialob.client.api.DialobCache;
import io.dialob.client.api.DialobClient.ProgramEnvir;
import io.dialob.client.api.DialobClient.ProgramMessage;
import io.dialob.client.api.DialobClient.ProgramStatus;
import io.dialob.client.api.DialobClient.ProgramWrapper;
import io.dialob.client.api.DialobClient.TypesMapper;
import io.dialob.client.api.DialobClientConfig;
import io.dialob.client.api.DialobStore.BodySource;
import io.dialob.client.api.ImmutableProgramMessage;
import io.dialob.client.api.ImmutableProgramWrapper;
import io.dialob.client.spi.program.ImmutableProgramEnvir;
import io.dialob.client.spi.program.ProgramBuilderImpl;
import io.dialob.compiler.DialobProgramErrorsException;
import io.dialob.compiler.DialobProgramFromFormCompiler;
import io.dialob.program.DialobProgram;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DialobProgramEnvirFactory {
  private final DialobClientConfig config;
  private final TypesMapper mapper;
  private final DialobProgramFromFormCompiler compiler;
  private final DialobCache cache;
  
  private final List<String> visitedIds = new ArrayList<>();
  private final List<String> cachlessIds = new ArrayList<>();
  private final StringBuilder treelog = new StringBuilder();
  private final List<ProgramWrapper> wrappers = new ArrayList<>();
  
  private ProgramEnvir baseEnvir;
  
  public DialobProgramEnvirFactory(DialobClientConfig config) {
    super();
    this.mapper = config.getMapper();
    this.cache = config.getCache();
    this.compiler = config.getCompiler();
    this.config = config;
  }
  
  public DialobProgramEnvirFactory add(ProgramEnvir envir) {
    this.baseEnvir = envir;
    return this;
  }
  public DialobProgramEnvirFactory add(BodySource entity, boolean cachless) {
    if(cachless) {
      cachlessIds.add(entity.getId());
    }
    
    switch (entity.getBodyType()) {
    case FORM: visitWrapper(visitForm(entity));
    case TAG: visitTag(entity); break;
    default: throw new IllegalArgumentException("unknown command format type: '" + entity.getBodyType() + "'!");
    }
    
    visitedIds.add(entity.getId());
    return this;
  }
  
  public ProgramEnvir build() {
    final var envir = ImmutableProgramEnvir.builder();
    if(baseEnvir != null) {
      baseEnvir.findAll().stream()
        .filter(wrapper -> !visitedIds.contains(wrapper.getId()))
        .forEach(wrapper -> visitWrapper(wrapper));    
    }
    
    for(final var wrapper : wrappers) {
      visitTreeLog(wrapper);
      envir.add(wrapper);
    }
    

    if(LOGGER.isDebugEnabled()) {
      LOGGER.debug(new StringBuilder()
          .append("Envir status: " + treelog.length()).append(System.lineSeparator())
          .append(treelog.toString())
          .toString());
    }
    return envir.build();
  }
  
  private void visitWrapper(ProgramWrapper wrapper) {
    this.wrappers.add(wrapper);
  }
  
  private void visitTreeLog(ProgramWrapper wrapper) {
    if(LOGGER.isDebugEnabled()) {
      final String name = wrapper.getAst().map(w -> w.getName()).orElseGet(() -> wrapper.getId());
      treelog.append("  - ").append(name).append(": ").append(wrapper.getStatus()).append(System.lineSeparator());
      if(wrapper.getStatus() != ProgramStatus.UP) {
      
        for(final var error : wrapper.getErrors()) {
          treelog.append("    - ").append(error.getId()).append(": ").append(error.getMsg()).append(System.lineSeparator());
          if(error.getException() != null) {
            treelog.append("      ").append(ExceptionUtils.getStackTrace(error.getException())).append(System.lineSeparator());
          }
        }
      }
    }

  }
  
  private ProgramWrapper visitForm(BodySource src) {
    final var builder = ImmutableProgramWrapper.builder();
    builder.status(ProgramStatus.UP);
    
    Form ast = null;
    try {
      if(cachlessIds.contains(src.getId())) {
        ast = mapper.toForm(src.getValue());
      } else {
        final var cached = cache.getAst(src);
        if(cached.isPresent()) {
          ast = (Form) cached.get();
        } else {
          ast = mapper.toForm(src.getValue());
          cache.setAst(ast, src);
        }
      }
      
    } catch(Exception e) {
      LOGGER.error(new StringBuilder()
          .append(e.getMessage()).append(System.lineSeparator())
          .append("  - form source: ").append(this.mapper.toForm(src.getValue()))
          .toString(), e);
      builder.status(ProgramStatus.AST_ERROR).addAllErrors(visitException(e));
    }
    
    DialobProgram program = null;
    if(ast != null) {
      try {
        if(cachlessIds.contains(src.getId())) {
          program = new ProgramBuilderImpl(compiler).form(ast).build();
        } else {
          final var cached = cache.getProgram(src);
          if(cached.isPresent()) {
            program = cached.get();
          } else {
            program = new ProgramBuilderImpl(compiler).form(ast).build();
            cache.setProgram(program, src);
          }
        }
      } catch(Exception e) {
        LOGGER.error(new StringBuilder()
            .append(e.getMessage()).append(System.lineSeparator())
            .append("  - form source: ").append(this.mapper.toForm(src.getValue()))
            .toString(), e);
        

        builder.status(ProgramStatus.PROGRAM_ERROR).addAllErrors(visitException(e));
      }
    }
    return builder.id(src.getId())
        .ast(Optional.ofNullable(ast)).program(Optional.ofNullable(program))
        .source(src)
        .build(); 
  }
  
  private void visitTag(BodySource src) { 
  }
  
  private List<ProgramMessage> visitException(Exception e) {
    final var msgs = new ArrayList<ProgramMessage>(); 
    if(e instanceof DialobProgramErrorsException) {
      ((DialobProgramErrorsException) e).getErrors().stream().map(error -> 
        ImmutableProgramMessage.builder()
        .id("compiler-error")
        .src(error)
        .build()
      ).forEach(msgs::add);
      
    }
    
    msgs.add(ImmutableProgramMessage.builder()
      .id("exception")
      .msg(e.getMessage() == null ? "no-desc-available": e.getMessage().replaceAll("\"", "'"))
      .exception(e)
      .build());
    return msgs;
  }

  public DialobClientConfig getConfig() {
    return config;
  }
  
}
