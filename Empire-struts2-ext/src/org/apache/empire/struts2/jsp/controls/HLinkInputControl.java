/*
 * ESTEAM Software GmbH, 30.07.2007
 */
package org.apache.empire.struts2.jsp.controls;

import org.apache.empire.commons.StringUtils;
import org.apache.empire.struts2.html.HtmlWriter;
import org.apache.empire.struts2.html.HtmlWriter.HtmlTag;


public class HLinkInputControl extends TextInputControl
{

    @Override
    public void internalRenderText(HtmlWriter writer, ValueInfo vi)
    {
        String text = formatValue(vi);
        if (StringUtils.isEmpty(text))
            return;
        // Get HRef
        String href = text; 
        if (text.indexOf("://")<0)
            href = "http://" + text;
        // Show Link
        HtmlTag a = writer.startTag("a");
        a.addAttribute("href",  href);
        a.addAttribute("id",    vi.getId());
        a.addAttribute("class", vi.getCssClass());
        a.addAttribute("style", vi.getCssStyle());
        a.addAttribute("target", "_blank");
        a.beginBody(text);
        a.endTag();
    }

}