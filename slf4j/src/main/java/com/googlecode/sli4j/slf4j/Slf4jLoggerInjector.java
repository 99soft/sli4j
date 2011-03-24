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
package com.googlecode.sli4j.slf4j;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.sli4j.core.AbstractLoggerInjector;

/**
 * {@code SLF4J} logger injector implementation.
 *
 * @author Simone Tripodi
 * @version $Id: Slf4jLoggerInjector.java 243 2010-06-05 14:20:53Z simone.tripodi $
 */
public final class Slf4jLoggerInjector extends AbstractLoggerInjector<Logger> {

    /**
     * Creates a new {@code SLF4J} Logger injector.
     *
     * @param field the logger field has to be injected.
     */
    public Slf4jLoggerInjector(Field field) {
        super(field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Logger createLogger(Class<?> klass) {
        return LoggerFactory.getLogger(klass);
    }

}
