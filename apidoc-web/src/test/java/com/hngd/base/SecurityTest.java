package com.hngd.base;

import java.util.LinkedList;
import java.util.List;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;

public class SecurityTest {

	public static void main(String[] args) {
		OpenAPI openApi=new OpenAPI();
		List<SecurityRequirement> security=new LinkedList<>();
		SecurityRequirement e=new SecurityRequirement();
		SecurityScheme ss=new SecurityScheme();
		ss.setDescription("oauth2 description");
		ss.setIn(In.HEADER);
		ss.setBearerFormat("JWT");
		ss.setType(Type.OAUTH2);
		OAuthFlows flows=new OAuthFlows();
		OAuthFlow implicit=new OAuthFlow();
		String authorizationUrl="http://1234";
		implicit.setAuthorizationUrl(authorizationUrl);
		implicit.setRefreshUrl("");
		Scopes scopes=new Scopes();
		scopes.addString("*", "*");
		implicit.setScopes(scopes);
		String tokenUrl="http://192.168.0.140:9255/oauth/token";
		implicit.setTokenUrl(tokenUrl);
		flows.setImplicit(implicit);
		ss.setFlows(flows);
		ss.setName("oauth2");
		e.addList("oauth2");
		security.add(e);
		openApi.setSecurity(security);
		openApi.setComponents(new Components());
		openApi.getComponents().addSecuritySchemes("oauth2", ss);
        Json.prettyPrint(openApi);
	}

}
