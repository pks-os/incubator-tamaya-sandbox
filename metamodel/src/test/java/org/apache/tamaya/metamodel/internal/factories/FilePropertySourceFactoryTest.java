/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.metamodel.internal.factories;

import org.apache.tamaya.spi.PropertySource;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * Created by atsticks on 18.04.17.
 */
public class FilePropertySourceFactoryTest {

    private static FilePropertySourceFactory f = new FilePropertySourceFactory();

    @Test
    public void getName() throws Exception {
        assertEquals("file", f.getName());
    }

    @Test
    public void create() throws Exception {
        Map<String,String> params = new HashMap<>();
        params.put("location", "src/test/resources/GLOBAL.properties");
        PropertySource ps = f.create(params);
        assertNotNull(ps);
    }

    @Test
    public void create_Error() throws Exception {
        Map<String,String> params = new HashMap<>();
        PropertySource ps = f.create(Collections.<String, String>emptyMap());
        assertNull("Should throw error for missing location.", ps);
    }

    @Test
    public void getType() throws Exception {
        assertEquals(PropertySource.class, f.getType());
    }

}