<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="java.util.regex.Matcher" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="org.springframework.core.io.Resource" %>
<%@ page import="org.springframework.core.io.ClassPathResource" %>

<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>

<%
  ApplicationContext context =  WebApplicationContextUtils.getWebApplicationContext(application);
  Properties properties = (Properties)context.getBean("applicationProperties");
  if (properties != null) {
    StringBuffer url = request.getRequestURL();
    String uri = request.getRequestURI();
    String ctx = request.getContextPath();
    String baseUrl = url.substring(0, url.length() - uri.length() + ctx.length()) + "/";

    StringBuilder json = new StringBuilder("{");

    // General Application Section
    json.append("\"application\":{");
      json.append("\"lazyAuthenticate\":").append("true").append(",");
      json.append("\"acronym\":\"").append(properties.getProperty("project.acronym", "")).append("\"").append(",");
      json.append("\"version\":\"").append(properties.getProperty("application.version", "")).append("\"").append(",");
      json.append("\"buildNumber\":\"").append(properties.getProperty("build.number", "")).append("\"").append(",");
      json.append("\"environment\":\"").append(properties.getProperty("default.application.environment", "")).append("\"").append(",");
      json.append("\"polling\":{");
          json.append("\"audibleAlert\":{");
            json.append("\"unacknowledgedRofPolling\":\"").append(properties.getProperty("audible.alert.rof.polling", "")).append("\"").append(",");
            json.append("\"alertFrequency\":\"").append(properties.getProperty("audible.alert.frequency", "")).append("\"");
          json.append("},");
        json.append("\"mapTool\":{");
          json.append("\"incidentsPolling\":\"").append(properties.getProperty("maptool.incidents.polling", "")).append("\"").append(",");
          json.append("\"rofPolling\":\"").append(properties.getProperty("maptool.rof.polling", "")).append("\"").append(",");
          json.append("\"layerRefreshPolling\":\"").append(properties.getProperty("maptool.layer.refresh.polling", "")).append("\"");
        json.append("},");
        json.append("\"rof\":{");
          json.append("\"refresh\":\"").append(properties.getProperty("rof.refresh.polling", "")).append("\"");
        json.append("},");
      json.append("\"nrof\":{");
      json.append("\"refresh\":\"").append(properties.getProperty("nrof.refresh.polling", "")).append("\"");
      json.append("}");
      json.append("},");
      json.append("\"maxListPageSize\":{");
        json.append("\"incidents\":\"").append(properties.getProperty("wildfire.incidents.maximum.results", "")).append("\"").append(",");
        json.append("\"incidents-table\":\"").append(properties.getProperty("wildfire.incidents.table.maximum.results", "")).append("\"").append(",");
        json.append("\"rofs-table\":\"").append(properties.getProperty("wildfire.incidents.table.maximum.results", "")).append("\"").append(",");
        json.append("\"rofs\":\"").append(properties.getProperty("report.of.fires.maximum.results", "")).append("\"");
      json.append("},");
      json.append("\"baseUrl\":\"").append(baseUrl).append("\"");
    json.append("},");

    // REST API Section
    String incidentsUri = properties.getProperty("wfim-rest.url", "");
    if (incidentsUri.endsWith("/")) {
      incidentsUri = incidentsUri.substring(0, incidentsUri.length() - 1); //Strip off trailing slash, if it exists.
    }
    String orgunitUri = properties.getProperty("wforg-org-unit-rest.url", "");
    if (orgunitUri.endsWith("/")) {
      orgunitUri = orgunitUri.substring(0, orgunitUri.length() - 1); //Strip off trailing slash, if it exists.
    }
    String wfdmUri = properties.getProperty("wfdm-rest.url", "");
    if (wfdmUri.endsWith("/")) {
      wfdmUri = wfdmUri.substring(0, wfdmUri.length() - 1); //Strip off trailing slash, if it exists.
    }
    String causecodesUri = properties.getProperty("wfim-cause-codes-config.url", "");
    if (causecodesUri.endsWith("/")) {
      causecodesUri = causecodesUri.substring(0, causecodesUri.length() - 1); //Strip off trailing slash, if it exists.
    }
    json.append("\"rest\":{");
      json.append("\"incidents\":\"").append(incidentsUri).append("\"").append(",");
      json.append("\"orgunit\":\"").append(orgunitUri).append("\"").append(",");
      json.append("\"wfdm\":\"").append(wfdmUri).append("\"").append(",");
      json.append("\"causecodes\":\"").append(causecodesUri).append("\"");
    json.append("}");

    json.append("}");
    out.write(json.toString());
  } else {
      out.write("{}");
  }
%>
