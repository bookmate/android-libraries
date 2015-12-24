package com.bookmate.libs.epub;

import android.text.TextUtils;
import android.util.Base64;

import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLDecoder;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;

/**
 * Created by khmelev on 29.04.14.
 */
public class EpubRemoteFile extends AbstractEpubFile {
    @SuppressWarnings("UnusedDeclaration")
    private static final String LOG_TAG = EpubRemoteFile.class.getSimpleName();

    private final String mSecret;
    private final Api api;

    private final String mContainer;
    private final String mOpf;
    private final String mNcx;
    private final HashMap<String, Long> mSizes;

    private DocumentBuilder mBuilder;
    private InputStream cachedStream;
    private String cachedStreamName;
    /**
     * we can't rely on cachedStream.available to test the stream size: it will become 0, after stream is read, so we need to store the size separately
     */
    private int cachedStreamSize;

    public interface Api {
        DocumentMetadata getDocumentMetadata();

        InputStream loadStreamFromServer(String path) throws IOException;
    }

    public EpubRemoteFile(String secret, Api api) {
        super();
        mSecret = secret;
        this.api = api;

        DocumentMetadata metadata = api.getDocumentMetadata();
        mContainer = decrypt(metadata.container);

//        try {
//            Log.i(LOG_TAG,new String( metadata.opf, "UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        mOpf = decrypt(metadata.opf);
        mNcx = decrypt(metadata.ncx);
        mSizes = metadata.sizes;

//        Log.d(LOG_TAG, "EpubRemoteFile(), opf: \n" + mOpf);

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            mBuilder = dbFactory.newDocumentBuilder();

            XPathFactory factory = XPathFactory.newInstance();
            mXPath = factory.newXPath();

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public long getSize(String name) {
        return mSizes.get(getFilePath(name));
    }


    private String getFilePath(String name) {
        String decodedName = name;
        try {
            decodedName = URLDecoder.decode(name, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return IOUtils.concatPath(getRootPath(), decodedName);
    }

    @Override
    public org.w3c.dom.Document getOpf() {
        org.w3c.dom.Document doc = null;
        try {
            doc = mBuilder.parse(new InputSource(new StringReader(mOpf)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    @Override
    public org.w3c.dom.Document getNcx() {
        org.w3c.dom.Document doc = null;
        try {
            doc = mBuilder.parse(new InputSource(new StringReader(mNcx)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * container.xml stores the name of OPF file.
     */
    @Override
    org.w3c.dom.Document getContainer() {
        org.w3c.dom.Document doc = null;
        try {
            doc = mBuilder.parse(new InputSource(new StringReader(mContainer)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    @Override
    public InputStream getStream(String name) throws IOException {
        loadStream(name);
        return cachedStream;
    }

    @Override
    public void loadStream(String name) {
        if (!isItemCached(name))
            cacheStream(name);
    }

    private void cacheStream(String name) {
        if (cachedStream != null)
            IOUtils.closeQuietly(cachedStream);

        try {
            cachedStream = api.loadStreamFromServer(getFilePath(name));
            cachedStreamName = name;
            cachedStreamSize = cachedStream == null ? 0 : cachedStream.available();
        } catch (IOException e) {
            cachedStream = null;
            cachedStreamName = null;
            cachedStreamSize = 0;
        }
    }


    /**
     * This method assumes that loadStream has been previously called.
     */
    @Override
    public boolean isItemStreamEmpty(String name) {
        return isItemCached(name) && cachedStreamSize == 0;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isItemCached(String name) {
        return TextUtils.equals(cachedStreamName, name) && cachedStream != null;
    }
}
