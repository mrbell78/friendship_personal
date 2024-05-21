/**
 * @author Kayum Hossan
 * Date: 26-03-2012
 * Description: Server Communication, Get feed from server, get status update from facebook,
 * send HTTP post request to a web server
 * Last Update: 5th May 2012
 */

package ngo.friendship.satellite.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;

import android.content.Context;
import android.util.Log;
import android.webkit.URLUtil;

// TODO: Auto-generated Javadoc

/**
 * The Class WebServerCommunication.
 */
public class WebServerCommunication {

    /** The cookie jar. */
    static CookieStore cookieJar;

    /**
     * Make a <b>POST</b> request to web server.
     *
     * @param urlPath The API path to which the request will be made
     * @param readfromServer The flag, used to determine whether to read the server response or not
     * @return Server response if <b>readfromServer</b> is true. Empty string otherwise
     * @throws Exception the exception
     */
    public static String sendHTTPPostRequestToServer(String urlPath, boolean readfromServer) throws Exception {
        String data = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(urlPath);

        HttpConnectionParams.setConnectionTimeout(client.getParams(), Constants.CONNECTION_TIME_OUT); // Timeout
        HttpConnectionParams.setSoTimeout(client.getParams(), Constants.CONNECTION_TIME_OUT);


        HttpResponse response = null;
        HttpEntity entity = null;
        InputStream is = null;
        BufferedReader reader = null;

        if (cookieJar == null)
            cookieJar = ((DefaultHttpClient) client).getCookieStore();
        else
            ((DefaultHttpClient) client).setCookieStore(cookieJar);


        //httppost.setEntity(new UrlEncodedFormEntity(postData));

        response = client.execute(httppost);

        if (readfromServer) {

            entity = response.getEntity();

            is = entity.getContent();

            reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            data = sb.toString();
        }
        return data.trim();
    }

    /**
     * Make a <b>GET</b> request to web server.
     *
     * @param urlPath The API path to which the request will be made
     * @param encoding The encoding used to read the server data
     * @param readfromServer The flag, used to determine whether to read the server response or not
     * @return Server response if <b>readfromServer</b> is true. Empty string otherwise
     * @throws Exception the exception
     */
    public static String sendHTTPGETRequestToServer(String urlPath, String encoding, boolean readfromServer) throws Exception {
        String data = "";

        HttpClient client = new DefaultHttpClient();
        HttpGet httppost = new HttpGet(urlPath);
        HttpConnectionParams.setConnectionTimeout(client.getParams(), Constants.CONNECTION_TIME_OUT); // Timeout
        HttpConnectionParams.setSoTimeout(client.getParams(), Constants.CONNECTION_TIME_OUT);


        HttpResponse response = null;
        HttpEntity entity = null;
        InputStream is = null;
        BufferedReader reader = null;

        if (cookieJar == null)
            cookieJar = ((DefaultHttpClient) client).getCookieStore();
        else
            ((DefaultHttpClient) client).setCookieStore(cookieJar);


        //httppost.setEntity(new UrlEncodedFormEntity(postData));

        response = client.execute(httppost);

        if (readfromServer) {

            entity = response.getEntity();

            data = EntityUtils.toString(entity, encoding);

        }
        return data.trim();
    }

    /**
     * Check if a gateway is working .
     *
     * @param url The gateway URL
     * @return The response status code
     * @throws Exception the exception
     */
    public static int checkGateway(String url) throws Exception {
        HttpClient client = new DefaultHttpClient();
        HttpGet httppost = new HttpGet(url);
        HttpConnectionParams.setConnectionTimeout(client.getParams(), Constants.CONNECTION_TIME_OUT); // Timeout
        HttpConnectionParams.setSoTimeout(client.getParams(), Constants.CONNECTION_TIME_OUT);
        HttpResponse response = null;

        if (cookieJar == null)
            cookieJar = ((DefaultHttpClient) client).getCookieStore();
        else
            ((DefaultHttpClient) client).setCookieStore(cookieJar);

        response = client.execute(httppost);
        return response.getStatusLine().getStatusCode();
    }

    /**
     * Make a <b>POST</b> request to web server.
     *
     * @param urlPath The API path to which the request will be made
     * @param postData The data to be post.
     * @param readfromServer The flag, used to determine whether to read the server response or not
     * @return Server response if <b>readfromServer</b> is true. Empty string otherwise
     * @throws Exception the exception
     */
    public static String sendHTTPPostRequestToServer(String urlPath, List<NameValuePair> postData, boolean readfromServer) throws Exception {
        String data = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(urlPath);
        HttpConnectionParams.setConnectionTimeout(client.getParams(), Constants.CONNECTION_TIME_OUT); // Timeout
        HttpConnectionParams.setSoTimeout(client.getParams(), Constants.CONNECTION_TIME_OUT);


        HttpResponse response = null;
        HttpEntity entity = null;
        InputStream is = null;
        BufferedReader reader = null;

        httppost.setEntity(new UrlEncodedFormEntity(postData));

        if (cookieJar == null)
            cookieJar = ((DefaultHttpClient) client).getCookieStore();
        else
            ((DefaultHttpClient) client).setCookieStore(cookieJar);

        response = client.execute(httppost);

        if (readfromServer) {

            entity = response.getEntity();

            is = entity.getContent();

            reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            data = sb.toString();
        }
        return data.trim();
    }

    /**
     * Download a file from <b>fileUrl</b>, save into <b>savePath</b> with given <b>fileName</b>.
     *
     * @param fileUrl The URL of the file
     * @param savePath The local path where the file to be saved
     * @param fileName The file name
     * @return Return 'Successful' or <b>null</b>
     */
    public static String downLoadFile(String fileUrl, String savePath, String fileName) {
        RandomAccessFile file = null;
        if (!URLUtil.isNetworkUrl(fileUrl)) {
            return null;
        }

        InputStream is = null;
        URL url = null;
        try {
            url = new URL(fileUrl);
        } catch (MalformedURLException e) {
            return null;
        }
        URLConnection urlConnection = null;
        try {
            urlConnection = url.openConnection();
            //urlConnection.setConnectTimeout(60000);
            //urlConnection.setReadTimeout(60000);
            urlConnection.connect();
        } catch (Exception e) {

            return null;
        }


        int contentLength = urlConnection.getContentLength();
        if (contentLength < 1) {
            return null;
        }

        File fl = new File(savePath);
        if (!fl.exists())
            fl.mkdirs();

        try {
            file = new RandomAccessFile(savePath + "/" + fileName, "rw");
        } catch (FileNotFoundException e) {
            return null;
        }
        try {
            file.seek(0);
            is = urlConnection.getInputStream();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }

        byte buf[] = new byte[128];

        do {
            int numread = 0;
            try {
                numread = is.read(buf);
            } catch (Exception e) {
                return null;
            }
            if (numread <= 0)
                break;
            try {
                file.write(buf, 0, numread);

            } catch (IOException e) {
                return null;
            }
        } while (true);
        try {
            file.close();
        } catch (IOException e) {
            return null;
        }
        return "Successful";
    }

    /**
     * Get the file size which will be download.
     *
     * @param urlPath The URL of the file
     * @return The size in Byte
     */
    public static int getDownloadedFileSize(String urlPath) {
        URL url = null;
        try {
            url = new URL(urlPath);
        } catch (MalformedURLException e) {
        }
        URLConnection urlConnection = null;
        try {
            HttpURLConnection httpurlConnection = (HttpURLConnection) (urlConnection = url.openConnection());
            if (httpurlConnection.getResponseCode() >= 400 && httpurlConnection.getResponseCode() <= 500) {
                return -1;
            }
            urlConnection.connect();

        } catch (Exception e) {
            Log.e("File", e.toString());
        }

        return urlConnection.getContentLength();
    }

    /**
     * Send Multipart data to server. Mostly used in transaction api. Which is used to insert/update data into server database
     *
     * @param uploadUrl The API URL
     * @param entityBuilder The Multipart data to be sent to server
     * @return WebResponseInfo object which contains parsed server response
     * @throws ClientProtocolException the client protocol exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static ResponseData uploadMultipartData(Context ctx, String uploadUrl, MultipartEntityBuilder entityBuilder) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(uploadUrl);
        try {
            httppost.setHeader(KEY.app_version, "" + Utility.getVersionCode(ctx));
            String authValue = "Bearer " + AppPreference.getString(ctx, KEY.TOKEN,"");;
            Log.e("authValue",""+authValue);
            httppost.setHeader(KEY.AUTHORIZATION, authValue);
        } catch (Exception e) {
            e.printStackTrace();
        }


        httppost.setEntity(entityBuilder.build());


        // DEBUG
        System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity resEntity = response.getEntity();

        // DEBUG
//		Log.e("Data Upload Status Code", ""+response.getStatusLine().getStatusCode() );
        ResponseData webResponseDataInfo = new ResponseData();
        webResponseDataInfo.setWebResponseStatusCode(response.getStatusLine().getStatusCode());
        if (resEntity != null) {
            webResponseDataInfo.setData(EntityUtils.toString(resEntity));
//			System.out.println( EntityUtils.toString( resEntity ) );
        } // end if

        if (resEntity != null) {
            resEntity.consumeContent();
        } // end if

        httpclient.getConnectionManager().shutdown();
        return webResponseDataInfo;
    }


    /**
     * Send Multipart data to server. Mostly used in getting data from server without any modification of server
     *
     * @param apiUrl The API URL
     * @param entityBuilder The Multipart data to be sent to server
     * @return Server response
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static String sendMultipartData(String apiUrl, MultipartEntityBuilder entityBuilder) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(apiUrl);

        httppost.setEntity(entityBuilder.build());

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity resEntity = response.getEntity();

        // DEBUG
        Log.e("Data Upload Status Code", "" + response.getStatusLine().getStatusCode());

        InputStream is = null;
        BufferedReader reader = null;
        String data = null;
        if (response.getStatusLine().getStatusCode() == 200) {

            is = resEntity.getContent();

            reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            data = sb.toString();
        }
        httpclient.getConnectionManager().shutdown();
        return data;
    }

}
