package com.kms.challenges.rbh.customtags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * @author tkhuu.
 */
public class AuthenticateTag extends SimpleTagSupport {
    private String principleName;
    private String access;

    public String getPrincipleName() {
        return principleName;
    }

    public void setPrincipleName(String principleName) {
        this.principleName = principleName;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    @Override
    public void doTag() throws JspException, IOException {
        if (access == principleName) {
            getJspBody().invoke(getJspContext().getOut());
        }
    }

}
