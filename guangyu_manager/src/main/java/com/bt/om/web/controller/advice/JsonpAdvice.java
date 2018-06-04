package com.bt.om.web.controller.advice;

import org.apache.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

import com.bt.om.util.ConfigUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by caiting on 2017/9/29.
 */
@ControllerAdvice(basePackages = "com.bt.om.web.controller.api")
public class JsonpAdvice extends AbstractJsonpResponseBodyAdvice {
    private Logger logger = Logger.getLogger(JsonpAdvice.class);
    public static Set<String> allow_access_domain_set = new HashSet<>();
   
    //允许跨域的域名列表
    private String allow_access_domain=ConfigUtil.getString("project.allow_access_domain");
    public JsonpAdvice(){    	
    	super("callback","jsonp");
        if(allow_access_domain!=null&& allow_access_domain.trim().length()>0){
            for(String domain:allow_access_domain.split(";")){
                allow_access_domain_set.add(domain);
            }
        }
        logger.info("project.allow_access_domain:"+allow_access_domain);
    }

    @Override
    protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType, MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {
        String origin = request.getHeaders().getOrigin();

        if(allow_access_domain_set.contains(origin)){
                response.getHeaders().add("Access-Control-Allow-Origin",origin);
        }
        response.getHeaders().add("Access-Control-Allow-Credentials","true");
        super.beforeBodyWriteInternal(bodyContainer, contentType, returnType, request, response);
    }
}
