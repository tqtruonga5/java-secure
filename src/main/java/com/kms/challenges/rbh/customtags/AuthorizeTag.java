package com.kms.challenges.rbh.customtags;

import com.kms.challenges.rbh.model.User;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * @author tkhuu.
 */
public class AuthorizeTag extends SimpleTagSupport {
    private static String ANONYMOUS = "ANONYMOUS";
    private static String AUTHORIZED = "AUTHORIZED";
    private String accessMode;
    @Override
    public void doTag() throws JspException, IOException {
        if (ANONYMOUS.equals(accessMode)) {
            if (isAnonymous()) {
                getJspBody().invoke(getJspContext().getOut());
            }
        }
        if (AUTHORIZED.equals(accessMode)) {
            if (!isAnonymous()) {
                getJspBody().invoke(getJspContext().getOut());
            }
        }
    }

    private boolean isAnonymous() throws JspException, IOException {
        User user = (User) getJspContext().getAttribute("user", PageContext.SESSION_SCOPE);
        return (user == null || user.getRole() == User.ROLE.ANNONYMOUS);
    }

    public String getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(String accessMode) {
        this.accessMode = accessMode;
    }
}
