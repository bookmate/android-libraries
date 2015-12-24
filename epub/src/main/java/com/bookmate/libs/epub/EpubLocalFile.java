package com.bookmate.libs.epub;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLDecoder;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by khmelev on 29.04.14.
 */
public class EpubLocalFile extends AbstractEpubFile {
    @SuppressWarnings("UnusedDeclaration")
    private static final String LOG_TAG = EpubLocalFile.class.getSimpleName();

    private ZipFile mZipFile;
    private String mSecret;

    private DocumentBuilder mBuilder;
    private XPath mXPath;

    private String mNcxPath;

    private org.w3c.dom.Document mOpf;
    private org.w3c.dom.Document mNcx;

    public EpubLocalFile(File file, String secret) throws IOException {
        super();
        mZipFile = new ZipFile(file);
        mSecret = secret;

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            mBuilder = dbFactory.newDocumentBuilder();

            XPathFactory factory = XPathFactory.newInstance();
            mXPath = factory.newXPath();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ZipEntry getEntry(String name) {
        return getEntry(name, false);
    }

    private ZipEntry getEntry(String name, boolean isRoot) {
        String decodedName = name;
        try {
            decodedName = URLDecoder.decode(name, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isRoot)
            return mZipFile.getEntry(decodedName);

        String filePath = IOUtils.concatPath(getRootPath(), decodedName);
        return mZipFile.getEntry(filePath);
    }

    @Override
    public InputStream getStream(String name) throws IOException {
        return getStream(name, false);
    }

    private InputStream getStream(String name, boolean isRoot) throws IOException {
        ZipEntry entry = getEntry(name, isRoot);
        return new BufferedInputStream(mZipFile.getInputStream(entry));
    }

    @Override
    public long getSize(String name) {
        ZipEntry entry = getEntry(name);
        return entry.getSize();
    }

    /**
     * container.xml stores the name of OPF file.
     */
    @Override
    org.w3c.dom.Document getContainer() {
        return getDocumentFromZip("META-INF/container.xml");
    }

    @Override
    public org.w3c.dom.Document getOpf() {
        if (mOpf != null)
            return mOpf;

        mOpf = getDocumentFromZip(getOpfPath());
        return mOpf;
    }

    public org.w3c.dom.Document getDocumentFromZip(String path) {
        InputStream stream = null;
        org.w3c.dom.Document document = null;
        try {
            stream = getStream(path, true);
            document = streamToDocument(stream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return document;
    }

    private String getNcxPath() {
        if (mNcxPath != null)
            return mNcxPath;
        try {
            org.w3c.dom.Document doc = getOpf();
            String ncxPath = mXPath.evaluate("//item[@media-type='application/x-dtbncx+xml']/@href", doc);
            mNcxPath = IOUtils.concatPath(getRootPath(), ncxPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mNcxPath;
    }

    @Override
    public org.w3c.dom.Document getNcx() {
        if (mNcx != null)
            return mNcx;
        mNcx = getDocumentFromZip(getNcxPath());
        return mNcx;
    }

    /**
     * this is needed for remote epub only for end of preview fragment calculation, so ignoring here
     */
    @Override
    public void loadStream(String name) {

    }

    /**
     * this is needed for remote epub only for end of preview fragment calculation, so ignoring here
     */
    @Override
    public boolean isItemStreamEmpty(String name) {
        return false;
    }

    private org.w3c.dom.Document streamToDocument(InputStream stream) throws IOException, SAXException {
        if (mSecret == null)
            return mBuilder.parse(stream);

        String xml = decrypt(IOUtils.toByteArray(stream));
        InputSource is = new InputSource(new StringReader(xml));
        return mBuilder.parse(is);
    }

    private String decrypt(byte[] data) {
        String decrypted = null;
        try {
            if (mSecret == null) {
                decrypted = new String(data, "UTF-8");
            } else {
                byte[] ivBytes = Arrays.copyOf(data, 16);
                byte[] contentBytes = Arrays.copyOfRange(data, 16, data.length);
                byte[] secretBytes = Base64.decode(mSecret, Base64.DEFAULT);
                AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
                SecretKeySpec newKey = new SecretKeySpec(secretBytes, "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
                decrypted = new String(cipher.doFinal(contentBytes), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decrypted;
    }

    /**
     * Load cover bitmap from epub file if presents, else null.
     *
     * @return cover bitmap or null
     */
    @Nullable
    public BitmapDrawable getCover(Resources resources) {
        String coverUrl = findCoverUrl();
        if (!TextUtils.isEmpty(coverUrl))
            try {
                return new BitmapDrawable(resources, getStream(coverUrl));
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }

    private String findCoverUrl() {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();

        try {
            String pattern = "//guide/reference[@type=\"cover\"]/@href";
            String coverUri = (String) xPath.evaluate(pattern, getOpf(), XPathConstants.STRING);

            if (coverUri.contains("html")) { //correct way is to check media-type of this file in manifest, but fuck it
                return (String) mXPath.evaluate("//img/@src", getDocumentFromZip(getRootPath() + coverUri), XPathConstants.STRING);
            } else {
                return coverUri;
            }

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return null;
    }

}
