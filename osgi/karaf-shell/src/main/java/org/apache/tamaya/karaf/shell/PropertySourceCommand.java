/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.karaf.shell;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

import java.io.IOException;

@Command(scope = "tamaya", name = "propertysource", description="Show the current Tamaya entries of a propertysource.")
@Service
public class PropertySourceCommand implements Action{

    @Argument(index = 0, name = "propertysource", description = "The target property source id.",
            required = false, multiValued = false)
    String propertysource = null;

    public Object execute() throws IOException {
        Configuration config = ConfigurationProvider.getConfiguration();
        if(propertysource!=null){
            PropertySource ps = config.getContext().getPropertySource(propertysource);
            if(ps==null){
                System.out.println("No such propertysource: " + propertysource);
            }else {
                System.out.println(StringUtil.format("ID:", 20) + ps.getName());
                System.out.println(StringUtil.format("Ordinal:", 20) + ps.getOrdinal());
                System.out.println(StringUtil.format("Class:", 20) + ps.getClass().getName());
                System.out.println("Properties:");
                System.out.print(StringUtil.format("  Key", 20));
                System.out.print(StringUtil.format("Value", 20));
                System.out.print(StringUtil.format("Source", 20));
                System.out.println(StringUtil.format("Meta-Entries", 20));
                System.out.println(StringUtil.printRepeat("-", 80));
                for(PropertyValue pv:ps.getProperties().values()) {
                    System.out.print("  " + StringUtil.format(pv.getKey(), 20));
                    System.out.print(StringUtil.format(pv.getValue(), 20));
                    System.out.print(StringUtil.format(pv.getSource(), 20));
                    System.out.println(StringUtil.format(pv.getMetaEntries().toString(), 80));
                }
            }
        }
        return null;
    }

}