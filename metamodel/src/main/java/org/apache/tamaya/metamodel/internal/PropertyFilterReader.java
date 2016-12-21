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
package org.apache.tamaya.metamodel.internal;

import org.apache.tamaya.metamodel.spi.ItemFactory;
import org.apache.tamaya.metamodel.spi.ItemFactoryManager;
import org.apache.tamaya.metamodel.spi.MetaConfigurationReader;
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySourceProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Metaconfiguration reader that reads the configuration filters to be used.
 */
public class PropertyFilterReader implements MetaConfigurationReader{

    private static final Logger LOG = Logger.getLogger(PropertyFilterReader.class.getName());

    @Override
    public void read(Document document, ConfigurationContextBuilder contextBuilder) {
        NodeList nodeList = document.getDocumentElement().getElementsByTagName("property-filters");
        if(nodeList.getLength()==0){
            LOG.finer("No property filters configured.");
            return;
        }
        if(nodeList.getLength()>1){
            LOG.warning("Multiple property-filters sections configured, onyl reading first...");
            return;
        }
        nodeList = nodeList.item(0).getChildNodes();
        for(int i=0;i<nodeList.getLength();i++){
            Node node = nodeList.item(i);
            try {
                if ("filter".equals(node.getNodeName())) {
                    String type = node.getAttributes().getNamedItem("type").getNodeValue();
                    ItemFactory<PropertyFilter> filterFactory = ItemFactoryManager.getInstance().getFactory(PropertyFilter.class, type);
                    if(filterFactory==null){
                        LOG.severe("No such property filter: " + type);
                        continue;
                    }
                    Map<String,String> params = ComponentConfigurator.extractParameters(node);
                    PropertyFilter filter = filterFactory.create(params);
                    if(filter!=null) {
                        ComponentConfigurator.configure(filter, params);
                        LOG.finer("Adding configured property filter: " + filter.getClass().getName());
                        contextBuilder.addPropertyFilters(filter);
                    }
                } else if ("default-filters".equals(node.getNodeName())) {
                    LOG.finer("Adding default property filters...");
                    contextBuilder.addDefaultPropertyFilters();
                }
            }catch(Exception e){
                LOG.log(Level.SEVERE, "Failed to read property filter configuration: " + node, e);
            }
        }
    }


}