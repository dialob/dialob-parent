package io.dialob.client.spi.support;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;

/*-
 * #%L
 * stencil-persistence
 * %%
 * Copyright (C) 2021 Copyright 2021 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.resys.thena.docdb.api.models.Objects.TreeValue;
import io.resys.thena.docdb.api.models.Repo;
import io.resys.thena.docdb.spi.ClientState;

public class RepositoryToStaticData {
  private final ClientState state;

  public RepositoryToStaticData(ClientState state) {
    super();
    this.state = state;
  }

  public String print(Repo repo) {
    final Map<String, String> replacements = new HashMap<>();
    final Function<String, String> ID = (id) -> {
      if(replacements.containsKey(id)) {
        return replacements.get(id);
      }
      final var next = String.valueOf(replacements.size() + 1);
      replacements.put(id, next);
      return next;
    };

    final var ctx = state.withRepo(repo);
    
    StringBuilder result = new StringBuilder();

    result
    .append(System.lineSeparator())
    .append("Repo").append(System.lineSeparator())
    .append("  - id: ").append(ID.apply(repo.getId()))
    .append(", rev: ").append(ID.apply(repo.getRev())).append(System.lineSeparator())
    .append("    name: ").append(repo.getName())
    .append(System.lineSeparator());
    
    result
    .append(System.lineSeparator())
    .append("Refs").append(System.lineSeparator());
    
    ctx.query().refs()
    .find().onItem()
    .transform(item -> {
      result.append("  - ")
      .append(ID.apply(item.getCommit())).append(": ").append(item.getName())
      .append(System.lineSeparator());
      return item;
    }).collect().asList().await().indefinitely();

    
    result
    .append(System.lineSeparator())
    .append("Tags").append(System.lineSeparator());
    
    ctx.query().tags()
    .find().onItem()
    .transform(item -> {
      result.append("  - id: ").append(item.getName())
      .append(System.lineSeparator())
      .append("    commit: ").append(ID.apply(item.getCommit()))
      .append(", message: ").append(item.getMessage())
      .append(System.lineSeparator());
      
      return item;
    }).collect().asList().await().indefinitely();
    
    result
    .append(System.lineSeparator())
    .append("Commits").append(System.lineSeparator());
    
    ctx.query().commits()
    .find().onItem()
    .transform(item -> {
      result.append("  - id: ").append(ID.apply(item.getId()))
      .append(System.lineSeparator())
      .append("    tree: ").append(ID.apply(item.getTree()))
      .append(", parent: ").append(item.getParent().map(e -> ID.apply(e)).orElse(""))
      .append(", message: ").append(item.getMessage())
      .append(System.lineSeparator());
      
      return item;
    }).collect().asList().await().indefinitely();
    
    
    result
    .append(System.lineSeparator())
    .append("Trees").append(System.lineSeparator());
    
    ctx.query().trees()
    .find().onItem()
    .transform(src -> {
      
      final var items = new ArrayList<TreeValue>(src.getValues().values());
      items.sort(new Comparator<TreeValue>() {

        @Override
        public int compare(TreeValue o1, TreeValue o2) {
          return o1.getName().compareTo(o2.getName());
        }
        
      });
      
      result.append("  - id: ").append(ID.apply(src.getId())).append(System.lineSeparator());
      for(final var e : items) {
          result.append("    ")
            .append(ID.apply(e.getBlob()))
            .append(": ")
            .append(e.getName())
            .append(System.lineSeparator());
        
      }
      
      return src;
    }).collect().asList().await().indefinitely();
    
    
    
    result
    .append(System.lineSeparator())
    .append("Blobs").append(System.lineSeparator());
    
    
    List<String> blobs = new ArrayList<>();
    
    ctx.query().blobs()
    .find().onItem()
    .transform(item -> {
      final var log = new StringBuilder().append("  - ").append(ID.apply(item.getId())).append(": ")
        .append(replaceContent(item.getValue(), replacements))
        .append(System.lineSeparator());
      
      blobs.add(log.toString());
      return item;
    }).collect().asList().await().indefinitely();
    
    
    blobs.stream().sorted().forEach(e -> result.append(e));
    
    return result.toString();
  }
  
  public static String replaceContent(String text, Map<String, String> replacements) {
    String newText = text;
    for(Map.Entry<String, String> entry : replacements.entrySet()) {
      newText = newText.replaceAll(entry.getKey(), entry.getValue());
      final var version = newText.indexOf("\"version\":");
      if(version > 1) {
        final var trailer = newText.substring(version);
        final var next = trailer.indexOf(",") + 1;
        newText = newText.substring(0, version) + trailer.substring(next);
      }
    }
    if(newText.length() > 100) {
      newText = newText.substring(0, 100) + "... cropped";
    }
    
    return newText;
  }
  
  public static String toString(Class<?> type, String resource) {
    try {
      return new String(type.getClassLoader().getResourceAsStream(resource).readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
