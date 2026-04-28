package de.sollfrank.sharedlists.config;

import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.spring.SpringWebApplicationFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WicketConfig {

    @Bean
    public FilterRegistrationBean<WicketFilter> wicketFilter() {
        FilterRegistrationBean<WicketFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new WicketFilter());
        registration.addInitParameter(WicketFilter.APP_FACT_PARAM, SpringWebApplicationFactory.class.getName());
        registration.addInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");
        registration.addUrlPatterns("/*");
        registration.setName("wicket.sharedlists");
        return registration;
    }
}