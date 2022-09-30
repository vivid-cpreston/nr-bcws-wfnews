package ca.bc.gov.nrs.wfnews.spring;

import ca.bc.gov.nrs.wfone.common.webade.oauth2.authentication.WebadeOauth2AuthenticationProvider;
import ca.bc.gov.nrs.wfone.common.webade.oauth2.token.client.impl.TokenServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity(debug = false)
@Import({
	TokenServiceSpringConfig.class
})
public class SecuritySpringConfig extends WebSecurityConfigurerAdapter  {

	private static final Logger logger = LoggerFactory.getLogger(SecuritySpringConfig.class);


	public SecuritySpringConfig() {
		super(true);
		logger.info("<SecuritySpringConfig");

		logger.info(">SecuritySpringConfig");
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		WebadeOauth2AuthenticationProvider result;

    // Replace the tokenService here with an autowire to the config tokenServiceImpl
    TokenServiceImpl tokenService;

		String clientSecret= System.getenv("WEBADE_OAUTH2_WFNEWS_REST_CLIENT_SECRET");
		String tokenUrl= System.getenv("WEBADE-OAUTH2_TOKEN_URL");
		String checkTokenUrl= System.getenv("WEBADE-OAUTH2_TOKEN_CLIENT_URL");

		tokenService = new TokenServiceImpl(
				"WFNEWS-UI",
				clientSecret,
				checkTokenUrl,
				tokenUrl);
        
		result = new WebadeOauth2AuthenticationProvider(tokenService, "WFIM.*");

		return result;
	}

	@Bean
	public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver() {
		AuthenticationManagerResolver<HttpServletRequest> result;

		result = new AuthenticationManagerResolver<HttpServletRequest>() {

			@Override
			public AuthenticationManager resolve(HttpServletRequest httpServletRequest) {

				return new AuthenticationManager() {

					@Override
					public Authentication authenticate(Authentication authentication) throws AuthenticationException {

						return authenticationProvider().authenticate(authentication);
					}};
			}};

		return result;
	}

	@Bean
	AuthenticationEntryPoint authenticationEntryPoint() {
		BasicAuthenticationEntryPoint result;

		result = new BasicAuthenticationEntryPoint();
		result.setRealmName("wfim-incident-manager-war");

		return result;
	}

	@Override
	public void configure(WebSecurity web) throws Exception {

		/* web.ignoring()
				.antMatchers(HttpMethod.OPTIONS, "/**")
				.antMatchers(HttpMethod.GET, "/**"); */
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.cors().and().csrf().disable().authorizeRequests().anyRequest().permitAll();
	}

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();
    
    List<String> origins = new ArrayList<>();
    origins.add("*");

    configuration.setAllowedOrigins(origins);
    configuration.setAllowedMethods(Collections.unmodifiableList(Arrays.asList("HEAD", "GET", "POST", "OPTIONS")));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(origins);

    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}

