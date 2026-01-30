package org.iuh.fit;

public class Main {
    public static void main(String[] args) {
        XMLService xmlService = new XMLService();
        JSONService adapter = new XMLtoJSONAdapter(xmlService);

        adapter.sendJSON("{name:'Nguyễn Trần Gia Sĩ'}");
    }
}
