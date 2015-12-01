package org.saucistophe.lpc.utils;

import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.MediaType;

/**
 * A repository class for every ueful constant.
 */
public class Constants
{
    public static final String ENCODING = "UTF-8";

    /**
     * Constants pertaining to characters.
     */
    public interface Character
    {
        public static String NEW_LINE = "\n";
    }
	
    /**
     * Constants pertaining to pictures and display.
     */
    public interface Display
    {
        public static double HAND_HEAD_RATIO = 0.8;
        public static int TICKS_PER_FRAME = 20;
    }

    /**
     * Constants pertaining to web.
     */
    public interface Web
    {
        public static int TEST_PORT = 9998;
        public static final String TEST_URL = "http://localhost:" + TEST_PORT + "/";
        public static int SERVICE_PORT = 9980;
        public static final String SERVICE_URL = "http://192.168.1.52:" + SERVICE_PORT + "/";
        public static final String APPLICATION_PRODUCED = MediaType.APPLICATION_JSON + ";charset=UTF-8";
        public static final String TEXT_PRODUCED = MediaType.TEXT_PLAIN + ";charset=UTF-8";
    }

    /**
     * Constants pertaining to Words.
     */
    public interface Words
    {
        public static final String LIAISON_STRING = "\u203F";
        public static final List<String> PUNCTUATIONS = Arrays.asList(
                new String[]
                {
                     "!", ":", ".", "…", ";", ",", "?",LIAISON_STRING
                });
    }

    /**
     * Constants pertaining to Authentification.
     */
    public interface Auth
    {
        public static final String HTPP_LOGIN = "cZC1dvuE3Q";
        public static final String HTPP_PASSWORD = "gnChPUK8yJ";
        public static final String KEY_SEPARATOR = "///";
        public static final String ALGORITHM = "RSA";
        public static final int RADIX = 32;
        public static final String SEPARATOR = "@°o°@";
    }

    /**
     * Constants pertaining to Webservices.
     */
    public interface Services
    {
        public static final String AUTH_PARAM = "auth";
        public static final String KEY_PATH = "key";
    }

    /**
     * Constants pertaining to Compression.
     */
    public interface Zip
    {
        public static final int BUFFER_SIZE = 16384;
    }
}
