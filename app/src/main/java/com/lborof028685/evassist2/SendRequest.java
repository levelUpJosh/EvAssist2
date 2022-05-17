package com.lborof028685.evassist2;

import java.io.*;
import java.net.*;

public class SendRequest {
    /**
     * Another hold over from past attempts,
     * I was attempting to query HERE maps here.
     *
     * @param args
     * @throws MalformedURLException
     * @throws IOException
     */
    public static void main(String[] args) throws MalformedURLException, IOException {
        URL reqURL = new URL("https://ev-v2.cc.api.here.com/ev/stations.json");
        HttpURLConnection request = (HttpURLConnection) (reqURL.openConnection());
        request.addRequestProperty("Authorization","Bearer ");
        request.setRequestMethod("GET");
        request.connect();
    }}
