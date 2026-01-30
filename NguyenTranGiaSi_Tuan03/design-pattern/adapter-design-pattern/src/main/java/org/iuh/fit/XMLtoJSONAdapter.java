package org.iuh.fit;

public class XMLtoJSONAdapter implements JSONService {
    private XMLService xmlService;

    public XMLtoJSONAdapter(XMLService xmlService) {
        this.xmlService = xmlService;
    }

    @Override
    public void sendJSON(String jsonData) {
        String xmlData = "<xml>" + jsonData + "</xml>";
        xmlService.sendXML(xmlData);
    }
}

