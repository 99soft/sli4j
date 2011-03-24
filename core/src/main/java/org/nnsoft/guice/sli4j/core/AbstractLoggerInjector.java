/*
 *    Copyright 2010-2011 The 99 Software Foundation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.nnsoft.guice.sli4j.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.inject.MembersInjector;

/**
 * The abstract Logger injector implementation, takes care of injecting the
 * concrete Logger implementation to the logged filed.
 */
public abstract class AbstractLoggerInjector<L> implements MembersInjector<L> {

    /**
     * The logger field has to be injected.
     */
    private final Field field;

    /**
     * Creates a new Logger injector.
     *
     * @param field the logger field has to be injected.
     */
    public AbstractLoggerInjector(Field field) {
        this.field = field;
    }

    /**
     * {@inheritDoc}
     */
    public final void injectMembers(Object target) {
        if (Modifier.isFinal(this.field.getModifiers())) {
            return;
        }

        boolean wasAccessible = this.field.isAccessible();
        this.field.setAccessible(true);
        try {
            if (this.field.get(target) == null) {
                this.field.set(target, this.createLogger(this.field.getType()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Impossible to set logger to field '"
                    + field
                    + "', see nested exceptions", e);
        } finally {
            this.field.setAccessible(wasAccessible);
        }
    }

    /**
     * Creates a new Logger implementation for the specified Class.
     *
     * @return a new Logger implementation.
     */
    protected abstract L createLogger(Class<?> klass);

}
