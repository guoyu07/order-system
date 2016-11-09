package cn.com.xbed.app.commons.util;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XMLReader {

	private Element root;

	/**
	 * 构造函数,要提供XML文件
	 * 
	 * @param xmlFile
	 * @throws DocumentException
	 */
	public XMLReader(File xmlFile) throws DocumentException {
		SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(xmlFile);
		root = doc.getRootElement();
	}

	public Element getRoot() {
		return root;
	}

	public Iterator getNode(String nodeName) {
		return root.elementIterator(nodeName);
	}

}
