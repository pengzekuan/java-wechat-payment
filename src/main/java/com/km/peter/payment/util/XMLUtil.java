package com.km.peter.payment.util;

import org.json.XML;

import java.util.Map;

public class XMLUtil {

    public static String map2XmlString(Map<String, Object> map) {

        String xmlResult;

        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (String key : map.keySet()) {

            String value = "<![CDATA[" + map.get(key) + "]]>";
            sb.append("<").append(key).append(">").append(value).append("</").append(key).append(">");
        }
        sb.append("</xml>");
        xmlResult = sb.toString();

        return xmlResult;
    }


    public static Map<String, Object> xml2Map(String xml) {
        String rootNode = "xml";
        return (Map<String, Object>) XML.toJSONObject(xml).toMap().get(rootNode);
    }

}
