/**
 * Copyright (C) 2013 Kiril Arabadzhiyski
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.a9ski.gerrit;

import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.a9ski.gerrit.serlvets.UserServiceServlet;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.server.config.SitePaths;
import com.google.inject.Inject;
import com.google.inject.servlet.ServletModule;

class HttpModule extends ServletModule {

	private static final Logger log = LoggerFactory.getLogger(HttpModule.class);
	
	@Inject
	public HttpModule(@PluginName final String name, @GerritServerConfig final Config gerritConfig, final SitePaths sitePaths) {
		log.info(String.format("Create HttpModule with pluginName '%s'", name));
		
	}

	@Override
	protected void configureServlets() {
		serve("/userManagement*").with(UserServiceServlet.class);
	}
}
