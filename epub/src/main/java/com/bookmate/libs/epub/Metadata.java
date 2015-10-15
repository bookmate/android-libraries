package com.bookmate.libs.epub;

import android.text.TextUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by khmelev on 21.05.14.
 * http://www.idpf.org/epub/20/spec/OPF_2.0.1_draft.htm#Section2.2
 * Contains author, title (from metada section in OPF) and table of contents (from NCX).
 */
public class Metadata {
    @SuppressWarnings("UnusedDeclaration")
    private static final String LOG_TAG = Metadata.class.getSimpleName();
    private final EpubFile file;

    private ArrayList<NavPoint> tocList;

    public Metadata(EpubFile file) {
        this.file = file;
    }

    private XPath initXpath() {
        XPathFactory factory = XPathFactory.newInstance();
        return factory.newXPath();
    }

    public NavPoint[] parseChapters() {
        Document tocDocument = file.getNcx();
        tocList = new ArrayList<>();
        if (tocDocument == null)
            return null;

        NodeList navMaps = tocDocument.getElementsByTagName("navMap");

        for (int i = 0; i < navMaps.getLength(); i++) {
            Element navMap = (Element) navMaps.item(i);
            parseRecursively(navMap, 0);

        }

        NavPoint[] navPoints = new NavPoint[tocList.size()];
        for (int i = 0; i < tocList.size(); i++)
            navPoints[i] = tocList.get(i);

        return navPoints;
    }

    private void parseRecursively(Element navMap, int depth) {
        NodeList list = navMap.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            String name = list.item(i).getNodeName();
            if ("navPoint".equals(name) || "navList".equals(name) || "pageList".equals(name)) {
                tocList.add(createNavPoint((Element) list.item(i), depth));
                parseRecursively((Element) list.item(i), depth + 1);
            }
        }
    }

    private NavPoint createNavPoint(Element navPoint, int depth) {
        String id = navPoint.getAttribute("id");
        // get title from toc-file
        Element navLabel = (Element) navPoint.getElementsByTagName("navLabel").item(0);
        Element text = (Element) navLabel.getElementsByTagName("text").item(0);
        final String tocTitle = text.getTextContent();

        // get chapter-path
        Element content = (Element) navPoint.getElementsByTagName("content").item(0);
        String src = content.getAttribute("src");

        //        Log.d(LOG_TAG, "navPoint "+tocNavPoint);
        return new NavPoint(id, tocTitle, src, depth);
    }


    /**
     * Returns bookmark with format like "d=vgElOTYR&i=RoW6SOwD:0"
     */
    public String getBookmark(String epubName, int position) {
        if (tocList != null) {
            int dotIndex = tocList.get(position).getContent().indexOf(".");
            String item = tocList.get(position).getContent().substring(0, dotIndex);
//            Log.e("TAG", tocList.get(position).getContent() + "   " + item);
            return "d=" + epubName + "&i=" + item + ":0";
        } else
            return null;
    }

    /**
     * returns index of ncx item
     * MUST BE CALLED AFTER {@link #parseChapters}
     *
     * @param opfItemId - path to html file of item in opf
     */
    public int getItemIndex(String opfItemId) {
        XPath xPath = initXpath();
        try {
            // 1.find html file which links with given
            String pattern = "//manifest/item[@id=\"" + opfItemId + "\"]/@href";
            String opfItemPath = (String) xPath.evaluate(pattern, file.getOpf(), XPathConstants.STRING);

            // 2.find ncx itemId id by html file
            pattern = "//navPoint[content[@src=\'" + opfItemPath + "\']]/@id";
            String itemId = (String) xPath.evaluate(pattern, file.getNcx(), XPathConstants.STRING);

            // 3.find index of given ncx item

//            could not make it work, do it if you smart enough. Test carefully contents with nesting items
//            int itemIndex = (int) (double) xPath.evaluate("count(//navPoint[@id=\"" + itemId + "\"]/preceding::navPoint)", file.getNcx(), XPathConstants.NUMBER);

            int index = 0;
            for (NavPoint item : tocList) {
                if (TextUtils.equals(itemId, item.getId()))
                    break;
                else
                    index++;
            }
            return index;

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
