/**
 * Copyright (c) 2011-2012, JGraph Ltd
 */
package com.mxgraph.examples.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.softtech.dsl.model.definitions.Application;
import com.softtech.dsl.model.definitions.Classification;
import com.softtech.dsl.model.definitions.Entity;
import com.softtech.dsl.model.definitions.PrimitiveTypes;

/**
 * Servlet implementation class SaveServlet.
 * 
 * The SaveDialog in Dialogs.js implements the user interface. Editor.saveFile
 * in Editor.js implements the request to the server. Note that this request is
 * carried out in a separate iframe in order to allow for the response to be
 * handled by the browser. (This is required in order to bring up a native Save
 * dialog and save the file to the local filestyem.) Finally, the code in this
 * servlet echoes the XML and sends it back to the client with the required
 * headers (see Content-Disposition in RFC 2183).
 */
public class EchoServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5308353652899057537L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getContentLength() < Constants.MAX_REQUEST_SIZE) {
			String filename = request.getParameter("filename");
			String xml = request.getParameter("xml");

			if (filename == null) {
				filename = "export";
			}

			if (xml != null && xml.length() > 0) {
				String format = request.getParameter("format");

				if (format == null) {
					format = "xml";
				}

				if (!filename.toLowerCase().endsWith("." + format)) {
					filename += "." + format;
				}

				// Decoding is optional (no plain text values allowed)
				if (xml != null && xml.startsWith("%3C")) {
					xml = URLDecoder.decode(xml, "UTF-8");
				}

				/*
				 * response.setContentType("text/plain");
				 * response.setHeader("Content-Disposition", "attachment; filename=\"" +
				 * filename + "\"; filename*=UTF-8''" + filename);
				 */

				response.setContentType("Content-type: text/zip");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);
				response.setStatus(HttpServletResponse.SC_OK);
				OutputStream out = response.getOutputStream();

				Application app;
				try {

					app = generateApplication(xml);

					new DownloadManager().DownloadApiFiles(out, app);
				} catch (XPathExpressionException | SAXException | ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				out.flush();
				out.close();
				/*
				 * out.write(xml.getBytes("UTF-8")); out.flush(); out.close();
				 */
			} else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} else {
			response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
		}
	}

	private Document getDocument(String xml) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setIgnoringElementContentWhitespace(true);

		DocumentBuilder builder = factory.newDocumentBuilder();

		return builder.parse(new InputSource(new StringReader(xml)));
	}

	private Application generateApplication(String xml)
			throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		Application app = null;

		Document doc = getDocument(xml);

		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("//mxCell[contains(@style,\"type=entity\")]");
		NodeList entities = (NodeList) expr.evaluate(doc.getChildNodes(), XPathConstants.NODESET);

		ArrayList<Entity> dslEntities = new ArrayList<>();

		for (int i = 0; i < entities.getLength(); i++) {
			Element element = (Element) entities.item(i);

			XPathExpression attExpr = xpath.compile("//mxCell[@parent=\"" + element.getAttribute("id") + "\"]");
			NodeList attributes = (NodeList) attExpr.evaluate(doc.getChildNodes(), XPathConstants.NODESET);

			String value = element.getAttribute("value").trim();

			HashMap<String, Entity> dslChildEntities = new HashMap<String, Entity>();

			for (int j = 0; j < attributes.getLength(); j++) {
				Element attribute = (Element) attributes.item(j);

				String attvalue = attribute.getAttribute("value").trim().replace("+", "");
				String[] values = attvalue.split(":");

				dslChildEntities.put(values[0], getDataType(values[1]));
			}

			Entity dslEntity = new Entity(new Classification(value, value, value, null), dslChildEntities);
			dslEntities.add(dslEntity);
		}

		app = new Application(new Classification("SampleFromBrowser", "", "", null), dslEntities);

		return app;
	}

	private Entity getDataType(String typeStr) {

		switch (typeStr.toLowerCase()) {
		case "string":
			return PrimitiveTypes.StringTypeEntity;
		case "int":
		case "integer":
		case "int32":
			return PrimitiveTypes.Int32TypeEntity;
		case "datetime":
			return PrimitiveTypes.DateTimeTypeEntity;
		default:
			return PrimitiveTypes.StringTypeEntity;
		}
	}

}
