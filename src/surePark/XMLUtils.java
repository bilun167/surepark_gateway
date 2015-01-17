package surePark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLUtils {

	/* 
	 * Returns request-name in the xml
	 */
	public static String getRequestName(String xml)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(xml)));		
		Node reqName=document.getElementsByTagName("request-name").item(0);
		return reqName.getTextContent();
	}
	
	/*
	 * Returns XML attached with the HTTP request
	 */
	public static StringBuffer readInputStream(InputStream inputStream)
			throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader(
				inputStream, "UTF-8");
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		StringBuffer requestXml = new StringBuffer();

		String line;
		while (null != (line = bufferedReader.readLine())) {
			requestXml.append(line)
					.append(System.getProperty("line.separator"));
		}

		bufferedReader.close();
		inputStreamReader.close();

		return requestXml;
	}
	
	/*
	 * Test method for parsing XML
	 * 
	 */
	public static String getRequestNameFull(String xml)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(xml)));
		
		NodeList nodeList = document.getDocumentElement().getChildNodes();
		Node reqName=document.getElementsByTagName("request-name").item(0);
		System.out.println(reqName.getTextContent());

		for (int i = 0; i < nodeList.getLength(); i++) {
		Node node = nodeList.item(i);
		System.out.println(i+"--"+node);
        if (node instanceof Element) {
        	        NodeList childNodes = node.getChildNodes();
        	        for (int j = 0; j < childNodes.getLength(); j++) {       	
        	          Node cNode = childNodes.item(j);
        	  		  System.out.println(i+"--"+j+"--"+cNode);
        	          if (cNode instanceof Element) {
        	            String content = cNode.getLastChild().getTextContent().trim();
        	            System.out.println(i+"--"+j+"--"+content);
        	          }        	
        	        }
		}
	}
		/*
		 * Do the parsing for reqname
		 */
		return reqName.getTextContent();
	}
}
