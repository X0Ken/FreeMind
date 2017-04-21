package freemind.main;

/*
 * XHTMLWriter -- A simple XHTML document writer
 * 
 * (C) 2004 Richard "Shred" Koerber
 *   http://www.shredzone.net/
 *
 * This is free software. You can modify and use it at will.
 */

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLWriter;

/**
 * Create a new XHTMLWriter which is able to save a HTMLDocument as XHTML.
 * <p>
 * The result will be a valid XML file, but it is not granted that the file will
 * really be XHTML 1.0 transitional conformous. The basic purpose of this class
 * is to give an XSL processor access to plain HTML files.
 * 
 * @author Richard "Shred" K�rber
 */
public class FixedHTMLWriter extends HTMLWriter {
	final private MutableAttributeSet convAttr = new SimpleAttributeSet();

	/**
	 * Create a new XHTMLWriter that will write the entire HTMLDocument.
	 * 
	 * @param writer
	 *            Writer to write to
	 * @param doc
	 *            Source document
	 */
	public FixedHTMLWriter(Writer writer, HTMLDocument doc) {
		this(writer, doc, 0, doc.getLength());
	}

	/**
	 * Create a new XHTMLWriter that will write a part of a HTMLDocument.
	 * 
	 * @param writer
	 *            Writer to write to
	 * @param doc
	 *            Source document
	 * @param pos
	 *            Starting position
	 * @param len
	 *            Length
	 */
	public FixedHTMLWriter(Writer writer, HTMLDocument doc, int pos, int len) {
		super(writer, doc, pos, len);
	}

	/**
	 * Create an older style of HTML attributes. This will convert character
	 * level attributes that have a StyleConstants mapping over to an HTML
	 * tag/attribute. Other CSS attributes will be placed in an HTML style
	 * attribute.
	 */
	private static void convertToHTML(AttributeSet from, MutableAttributeSet to) {
		if (from == null) {
			return;
		}
		Enumeration keys = from.getAttributeNames();
		String value = "";
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			if (key instanceof CSS.Attribute) {
				// default is to store in a HTML style attribute
				if (value.length() > 0) {
					value = value + "; ";
				}
				value = value + key + ": " + from.getAttribute(key);
			} else {
				to.addAttribute(key, from.getAttribute(key));
			}
		}
		if (value.length() > 0) {
			to.addAttribute(HTML.Attribute.STYLE, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.text.html.HTMLWriter#writeAttributes(javax.swing.text.
	 * AttributeSet)
	 */
	protected void writeAttributes(AttributeSet attr) throws IOException {
		// translate css attributes to html
		if (attr instanceof Element) {
			Element elem = (Element) attr;
			if (elem.isLeaf() || elem.getName().equalsIgnoreCase("p-implied")) {
				super.writeAttributes(attr);
				return;
			}
		}
		convAttr.removeAttributes(convAttr);
		convertToHTML(attr, convAttr);

		Enumeration names = convAttr.getAttributeNames();
		while (names.hasMoreElements()) {
			Object name = names.nextElement();
			if (name instanceof HTML.Tag || name instanceof StyleConstants
					|| name == HTML.Attribute.ENDTAG) {
				continue;
			}
			write(" " + name + "=\"" + convAttr.getAttribute(name) + "\"");
		}
	}

}