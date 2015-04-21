package com.example.android.effectivenavigation.ui;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Esteban on 5/22/14.
 */
public class Httpconnection {


    /***
     *
     */
    public class HttpSend extends AsyncTask<Void, Void, String>{

        private List<NameValuePair> dataToSend;

        public HttpSend(List<NameValuePair> dataToSend) {
            setDataToSend(dataToSend);

        }

        protected String getASCIIContentFromEntity(HttpEntity entity)
                throws IllegalStateException, IOException {
            InputStream in = entity.getContent();
            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];
                n = in.read(b);
                if (n > 0)
                    out.append(new String(b, 0, n));
            }
            return out.toString();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            // HttpGet httpGet = new
            // HttpGet("http://www.cheesejedi.com/rest_services/get_big_cheese.php?puzzle=1");
            // HttpPost httpPost = new
            // HttpPost("http://130.239.41.93:8080/AAL2v1.0/resources/greeting/walk");
//			HttpPut httpPost = new HttpPut(
//					"http://130.239.41.93:8080/AAL2v1.4/resources/greeting/ping");

//            HttpPut httpPost = new HttpPut( "http://130.239.179.159:8080/Balance3.2/resources/balance/ping");

            HttpPut httpPost = new HttpPut("http://130.239.179.159:8080/Balance4.0/resources/balance/ping");


            //http://localhost:8080/Balance3.2/
//			HttpPut httpPost = new HttpPut(
//					"http://130.239.41.93:8080/AAL2v2.1/resources/balance/ping");


			/* funciona */
            // httpPost.setHeader("Accept", "text/plain");
            // httpPost.setHeader("Content-type",
            // "application/x-www-form-urlencoded");
			/* funciona */
            // HttpPut.setHeader("Content-type", "application/json");
            // httpPut.setHeader("Accept-Charset", "utf-8");
            try {
                // Add your data
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();

                //pairs.add(new BasicNameValuePair(pairsX.get(pairsX.size()-1).getName(),pairsX.get(0).getValue()));


                long timestamp = System.currentTimeMillis();


                //TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
                java.util.Date timest  = new java.util.Date((long) timestamp * 1000);

                String timstart = String.valueOf(timest);

                long nanoTime = System.nanoTime();
                System.out.println("nanotime:"+nanoTime);
                System.out.println("timestamp:"+timstart);
                System.out.println("Systimestmp:"+timestamp);

                pairs.add(new BasicNameValuePair("key1",""));
                pairs.add(new BasicNameValuePair("key2",""));
                pairs.add(new BasicNameValuePair("key3",""));
                pairs.add(new BasicNameValuePair("key4","userID"));//TODO SEND THE USER ID
                //pairs.add(new BasicNameValuePair("key5",String.valueOf(timestamp)));
                pairs.add(new BasicNameValuePair("key5",""));
                pairs.add(new BasicNameValuePair("key6","m1"));//TODO SEND THE KEY DEPNDG OF TYPE OF MEASUREMENT




                //httpPost.setEntity(new UrlEncodedFormEntity(pairs));

                httpPost.setEntity(new UrlEncodedFormEntity(getDataToSend()));

				/* funciona */
                // httpPost.setEntity(new StringEntity(obj.toString(),
                // "UTF-8"));
				/* funciona */
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // HttpGet httpGet = new
            // HttpGet("http://130.239.41.93:8080/AAL2v1.0/resources/greeting/create2");
            String text = null;
            try {
                // HttpResponse response = httpClient.execute(httpGet,
                // localContext);
                HttpResponse response = httpClient.execute(httpPost,
                        localContext);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);
                System.out.println(">Response:" + text);
            } catch (Exception e) {
                System.out.println("EROR:" + e.getLocalizedMessage());
                return e.getLocalizedMessage();
            }
            return text;
        }

        protected void onPostExecute(String results) {
            if (results != null) {
                //	EditText et = (EditText) findViewById(R.id.my_edit);
                //	et.setText(results);
            }
            //	Button b = (Button) findViewById(R.id.my_button);
            //	b.setClickable(true);
        }

        public List<NameValuePair> getDataToSend() {
            return dataToSend;
        }

        public void setDataToSend(List<NameValuePair> dataToSend) {
            this.dataToSend = dataToSend;
        }
    }


    /***
     *
     */
    public class HttpReceive extends AsyncTask<Void, Void, String>{

        private List<NameValuePair> dataToReceive;

        public List<NameValuePair> HttpReceive() {
            return getDataToReceive();

        }

        protected String getASCIIContentFromEntity(HttpEntity entity)
                throws IllegalStateException, IOException {
            InputStream in = entity.getContent();
            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];
                n = in.read(b);
                if (n > 0)
                    out.append(new String(b, 0, n));
            }
            return out.toString();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();





            //line
//            HttpGet httpGet = new HttpGet("http://130.239.41.93:8080/Balance3.2/resources/balance/users");


            //wireless
            //HttpGet httpGet = new HttpGet("http://130.239.179.159:8080/Balance3.2/resources/balance/users");
            HttpGet httpGet = new HttpGet("http://130.239.179.159:8080/Balance4.0/resources/balance/users");






            String text = null;
            try {
                HttpResponse response = httpClient.execute(httpGet,
                localContext);

                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);
                System.out.println(">Response:" + text);
            } catch (Exception e) {
                System.out.println("EROR:" + e.getLocalizedMessage());
                return e.getLocalizedMessage();
            }
            return text;
        }

        protected void onPostExecute(String results) {
            if (results != null) {
                //	EditText et = (EditText) findViewById(R.id.my_edit);
                //	et.setText(results);
            }
            //	Button b = (Button) findViewById(R.id.my_button);
            //	b.setClickable(true);
        }

        public List<NameValuePair> getDataToReceive() {
            return dataToReceive;
        }

        public void setDataToReceive(List<NameValuePair> dataToReceive) {
            this.dataToReceive = dataToReceive;
        }
    }




     public void postData() {
     // Create a new HttpClient and Post Header
     HttpClient httpclient = new DefaultHttpClient();
     //HttpPost httppost = new HttpPost("http://130.239.179.159:8080/Balance3.2/resources/balance/userdata");
     HttpPost httppost = new HttpPost("http://130.239.179.159:8080/Balance4.0/resources/balance/userdata");
     HttpContext localContext = new BasicHttpContext();

     try {
     // Add your data
     List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
     nameValuePairs.add(new BasicNameValuePair("id", "12345"));
     nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

     // Execute HTTP Post Request
     HttpResponse response = httpclient.execute(httppost);


     System.out.println("PASA");

     } catch (ClientProtocolException e) {
     // TODO Auto-generated catch block
     } catch (IOException e) {
     // TODO Auto-generated catch block
     }

     try {
     // HttpResponse response = httpClient.execute(httpGet,
     // localContext);
     HttpResponse response = httpclient.execute(httppost,
     localContext);
     HttpEntity entity = response.getEntity();
     String text = getASCIIContentFromEntity(entity);

     System.out.println("--> * * HAHAHA* * ->Response:" + text);




     } catch (Exception e) {
     System.out.println("EROR:" + e.getLocalizedMessage());
     //return e.getLocalizedMessage();
     }

     }

    protected String getASCIIContentFromEntity(HttpEntity entity)
            throws IllegalStateException, IOException {
        InputStream in = entity.getContent();
        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n > 0) {
            byte[] b = new byte[4096];
            n = in.read(b);
            if (n > 0)
                out.append(new String(b, 0, n));
        }
        return out.toString();
    }


}
