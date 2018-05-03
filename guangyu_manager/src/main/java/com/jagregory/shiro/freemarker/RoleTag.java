package com.jagregory.shiro.freemarker;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.Map;

/**
 * <p>Equivalent to {@link org.apache.shiro.web.tags.RoleTag}</p>
 */
public abstract class RoleTag extends SecureTag {
	
    @SuppressWarnings("rawtypes")
    String getName(Map params) {
        return getParam(params, "name");
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException {
        boolean show = showTagBody(getName(params));
        if (show) {
            renderBody(env, body);
        }
    }

    protected abstract boolean showTagBody(String roleName);
}