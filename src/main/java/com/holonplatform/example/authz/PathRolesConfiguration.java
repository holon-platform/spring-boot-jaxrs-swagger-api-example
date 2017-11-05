package com.holonplatform.example.authz;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "example.authz")
public class PathRolesConfiguration {

	private Map<String, String> paths;

	public Map<String, String> getPaths() {
		return paths;
	}

	public void setPaths(Map<String, String> paths) {
		this.paths = paths;
	}

}
