/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dialob.db.spi.spring;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * {@link EntityInformation} implementation that inspects fields for an annotation and looks up this field's value to
 * retrieve the id.
 *
 * @author Oliver Gierke
 */
public class ReflectionEntityInformation<T, ID extends Serializable> extends AbstractEntityInformation<T, ID> {

  private static final Class<Id> DEFAULT_ID_ANNOTATION = Id.class;

  private Field field;
  private Method method;

  /**
   * Creates a new {@link ReflectionEntityInformation} inspecting the given domain class for a field carrying the
   * {@link Id} annotation.
   *
   * @param domainClass must not be {@literal null}.
   */
  public ReflectionEntityInformation(Class<T> domainClass) {
    this(domainClass, DEFAULT_ID_ANNOTATION);
  }

  /**
   * Creates a new {@link ReflectionEntityInformation} inspecting the given domain class for a field carrying the given
   * annotation.
   *
   * @param domainClass must not be {@literal null}.
   * @param annotation must not be {@literal null}.
   */
  public ReflectionEntityInformation(Class<T> domainClass, final Class<? extends Annotation> annotation) {

    super(domainClass);
    Assert.notNull(annotation, "Annotation must not be null!");

    ReflectionUtils.doWithFields(domainClass, field -> {
      if (field.getAnnotation(annotation) != null) {
        ReflectionEntityInformation.this.field = field;
      }
    });

    ReflectionUtils.doWithMethods(domainClass, method -> {
      if (method.getAnnotation(annotation) != null && method.getReturnType() != Void.class) {
        ReflectionEntityInformation.this.method = method;
      }
    });

    if (this.field == null && this.method == null) {
      Assert.notNull(this.field, String.format("No field or method annotated with %s found!", annotation.toString()));
    }
    if (this.field != null) {
      ReflectionUtils.makeAccessible(field);
    }
    if (this.method != null) {
      ReflectionUtils.makeAccessible(method);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.data.repository.core.EntityInformation#getId(java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  @Nullable
  public ID getId(@NonNull Object entity) {
    if (field != null) {
      return (ID) ReflectionUtils.getField(field, entity);
    }
    return (ID) ReflectionUtils.invokeMethod(method, entity);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.data.repository.core.EntityInformation#getIdType()
   */
  @NonNull
  @SuppressWarnings("unchecked")
  public Class<ID> getIdType() {
    if (field != null) {
      return (Class<ID>) field.getType();
    }
    return (Class<ID>) method.getReturnType();
  }
}
