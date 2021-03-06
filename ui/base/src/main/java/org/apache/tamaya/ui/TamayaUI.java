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
package org.apache.tamaya.ui;

import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.ui.event.LogoutEvent;
import org.apache.tamaya.ui.event.NavigationEvent;
import org.apache.tamaya.ui.views.login.LoginEvent;
import org.apache.tamaya.ui.views.login.LoginView;

import java.io.File;
import java.util.logging.Logger;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("valo")
@Title("Tamaya")
public class TamayaUI extends UI {

    private static final Logger LOG = Logger.getLogger(TamayaUI.class.getName());

    /**
     * Not an instantiable class.
     */
    public TamayaUI(){
        super(new Panel());
        super.setPollInterval(2000);
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setupEventBus();
        if (CurrentUser.isLoggedIn()) {
            setContent(new ApplicationLayout(this));
        } else {
            setContent(new LoginView());
        }
    }

    @Subscribe
    public void userLoggedIn(
            LoginEvent event) {
        CurrentUser.set(event.getUser());
        setContent(new ApplicationLayout(this));
    }

    @Subscribe
    public void navigateTo(NavigationEvent evt) {
        if(getNavigator()==null){
            return;
        }
        if(evt.getViewName().isEmpty()){
            getNavigator().navigateTo("/home");

        }else {
            getNavigator().navigateTo(evt.getViewName());
        }
    }

    public static TamayaUI getCurrent() {
        return (TamayaUI) UI.getCurrent();
    }

    /**
     * The main entry point, use configuration as follows:
     * <pre>
     *     tamaya.server.contextPath: the context path, default=/tamaya
     *     tamaya.server.port: the port, default=8090
     *     tamaya.server.productionMode: vadiin production mode setting, default=false.
     * </pre>
     * @param args the args
     * @throws Exception if startup fails.
     */
    public static void main(String[] args) throws Exception {
        Configuration config = Configuration.current();
        String contextPath = config.getOrDefault("tamaya.server.contextPath", "/tamaya");
        String appBase = ".";
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(Integer.valueOf(config.getOrDefault("tamaya.server.port", Integer.class, 8090) ));

        // Define a web application context.
        Context context = tomcat.addWebapp(contextPath, new File(
                appBase).getAbsolutePath());
        // Add Vadiin servlet
        Wrapper wrapper = tomcat.addServlet(context, "vadiin-servlet",
                VaadinServlet.class.getName());
        wrapper.addInitParameter("ui",
                TamayaUI.class.getName());
        wrapper.addInitParameter("productionMode",config.getOrDefault("tamaya.server.productionMode", String.class,
                "false"));
        wrapper.addInitParameter("asyncSupported", "true");
        context.addServletMapping("/*", "vadiin-servlet");
        // bootstrap.addBundle(new AssetsBundle("/VAADIN", "/VAADIN", null, "vaadin"));
        tomcat.start();
        tomcat.getServer().await();
    }

    @Subscribe
    public void logout(LogoutEvent logoutEvent) {
        // Don't invalidate the underlying HTTP session if you are using it for something else
        VaadinSession.getCurrent().getSession().invalidate();
        VaadinSession.getCurrent().close();
        Page.getCurrent().reload();

    }

    private void setupEventBus() {
        org.apache.tamaya.ui.event.EventBus.register(this);
    }
}