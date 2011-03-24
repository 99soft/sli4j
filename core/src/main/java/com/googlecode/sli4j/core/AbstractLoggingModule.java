/*
 *    Copyright 2010 The sli4j Team
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
package com.googlecode.sli4j.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import com.google.inject.Binder;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.MoreTypes;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Abstract module implementation of Logging module that simplifies Logger
 * building and injection.
 *
 * Subclasses have to specify the Logger and the relative
 * {@link AbstractLoggerInjector} types.
 *
 * @param <L> the Logger type has to be injected.
 * @author Simone Tripodi
 * @version $Id: AbstractLoggingModule.java 233 2010-06-05 14:06:59Z simone.tripodi $
 */
public class AbstractLoggingModule<L> extends TypeLiteral<L> implements Module, TypeListener {

    /**
     * The types matcher for whom the Logger injection has to be performed.
     */
    private final Matcher<? super TypeLiteral<?>> matcher;

    /**
     * The concrete Logger type.
     */
    private final Class<?> loggerClass;

    /**
     * The {@link AbstractLoggerInjector} constructor, instances will be created
     * at runtime.
     */
    private final Constructor<? extends MembersInjector<L>> logInjectorConstructor;

    /**
     * Creates a new Logger injection module.
     *
     * @param <LI> the concrete {@link AbstractLoggerInjector} 
     * @param matcher types matcher for whom the Logger injection has to be
     *        performed.
     * @param logInjectorClass the {@link AbstractLoggerInjector} constructor.
     */
    public <LI extends AbstractLoggerInjector<L>> AbstractLoggingModule(Matcher<? super TypeLiteral<?>> matcher, Class<LI> loggerInjectorClass) {
        if (matcher == null) {
            throw new IllegalArgumentException("Parameter 'matcher' must not be null");
        }
        if (loggerInjectorClass == null) {
            throw new IllegalArgumentException("Parameter 'loggerInjectorClass' must not be null");
        }

        this.matcher = matcher;
        this.loggerClass = MoreTypes.getRawType(this.getType());
        try {
            this.logInjectorConstructor = loggerInjectorClass.getConstructor(Field.class);
        } catch (SecurityException e) {
            throw new RuntimeException("Impossible to access to '"
                    + loggerInjectorClass.getName()
                    + "("
                    + Field.class.getName()
                    + ")' public constructor due to security violation", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Class '"
                    + loggerInjectorClass.getName()
                    + "' doesn't have a public construcor with <"
                    + Field.class.getName()
                    + "> parameter type", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void configure(Binder binder) {
        binder.bindListener(this.matcher, this);
    }

    /**
     * {@inheritDoc}
     */
    public final <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        this.hear(type.getRawType(), encounter);
    }

    @SuppressWarnings("unchecked")
    private <I> void hear(Class<?> klass, TypeEncounter<I> encounter) {
        if (Object.class == klass) {
            return;
        }

        for (Field field : klass.getDeclaredFields()) {
            if (this.loggerClass == field.getType()) {
                try {
                    encounter.register((MembersInjector<? super I>) this.logInjectorConstructor.newInstance(field));
                } catch (Exception e) {
                    throw new RuntimeException("Impossible to register '"
                            + this.logInjectorConstructor.getName()
                            + "' for field '"
                            + field
                            + "', see nested exception", e);
                }
            }
        }

        this.hear(klass.getSuperclass(), encounter);
    }

}
