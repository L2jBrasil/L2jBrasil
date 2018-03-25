/*
 * Copyright 2010 InC-Gaming, nBd. All rights reserved.
 */
package com.it.br.gameserver.xmlfactory;

import com.it.br.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nBd
 */
public final class XMLParser {
    private static Logger _log = LoggerFactory.getLogger(XMLParser.class);

    private final File _file;
    private final List<StatsSet> _sets;
    private final String _type;

    public XMLParser(File file, String type) {
        _file = file;
        _type = type;
        _sets = new ArrayList<>();
    }

    public List<StatsSet> parseDocument() {
        if (_file == null) {
            _log.warn( "XMLParser: Couldn't find the XML File!");
            return null;
        }
        parse();

        return _sets;
    }

    private Document parse() {
        Document doc;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            doc = factory.newDocumentBuilder().parse(_file);
        } catch (Exception e) {
            _log.error( "XMLParser: Error loading file " + _file, e);
            return null;
        }

        try {
            parseDocument(doc);
        } catch (Exception e) {
            _log.error( "XMLParser: Error in file " + _file, e);
            return null;
        }
        return doc;
    }

    private void parseDocument(Document doc) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if (_type.equalsIgnoreCase(d.getNodeName()))
                        parseItem(d);
                }
            }
        }
    }

    private void parseItem(Node n) {
        StatsSet set = new StatsSet();

        try {
            set.set("id", Integer.parseInt(n.getAttributes().getNamedItem("id").getNodeValue()));
        } catch (Exception e) {
            // Empty Catch
        }

        Node first = n.getFirstChild();
        for (n = first; n != null; n = n.getNextSibling()) {
            if ("set".equalsIgnoreCase(n.getNodeName()))
                parseBeanSet(n, set);
        }
        _sets.add(set);
    }

    private static void parseBeanSet(Node n, StatsSet set) {
        if (n == null)
            return;

        String name = n.getAttributes().getNamedItem("name").getNodeValue().trim();
        String value = n.getAttributes().getNamedItem("val").getNodeValue().trim();

        set.set(name, value);
    }
}