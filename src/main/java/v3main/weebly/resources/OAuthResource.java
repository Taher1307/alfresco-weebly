package v3main.weebly.resources;

import java.io.*;
import java.util.*;
import java.math.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.*;

import javax.crypto.*;
import javax.crypto.spec.*;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.jboss.resteasy.annotations.jaxrs.QueryParam;

@Path("/weebly")
public class OAuthResource {

    @ConfigProperty(name="client.secret")
    String secretKey;

    @ConfigProperty(name="client.id")
    String clientId;


    @GET
    @Path("authorize")
    @Produces(MediaType.TEXT_PLAIN)
    public String authId(
        @QueryParam("user_id") @DefaultValue("userId") String userId,
        @QueryParam("site_id") @DefaultValue("siteId") String siteId,
        @QueryParam("timestamp") @DefaultValue("timestamp") String timestamp,
        @QueryParam("hmac") @DefaultValue("hmac") String hmac,
        @QueryParam("callback_url") @DefaultValue("callback_url") String callback_url
    ) {
        String line = "";

        try {
            String data = "user_id=["+userId+"]&timestamp=["+timestamp+"]&site_id=["+siteId+"]";
            String algorithm = "HmacSHA256";
    
            Mac algoHash = Mac.getInstance(algorithm);
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes("UTF-8"), algorithm);
            algoHash.init(secret_key);

            String finalHashBase64 = Base64.getEncoder().encodeToString(algoHash.doFinal(data.getBytes("UTF-8")));
            String finalHasHex = String.format("%032x", new BigInteger(1, algoHash.doFinal(data.getBytes("UTF-8"))));
            
            System.out.print("\n\nHmac\t "+hmac);
            System.out.println("\n\nfinalHashHex \t"+ finalHasHex);
            System.out.print("\n\nFinal hash\t "+finalHashBase64);


            String result = null;
            if (finalHashBase64.trim() == hmac.trim()) {
                String callback = callback_url+"?client_id=["+clientId+"]user_id=["+userId+"]&site_id=["+siteId+"]&redirect_uri=[localhost:8080/weebly/phase-two]";
                String[] url = {"curl", "-X",  "GET", "-H", "Accept: application/json", callback};
                ProcessBuilder processBuilder = new ProcessBuilder(url);
                Process p;
                StringBuilder builder = new StringBuilder();
                p = processBuilder.start();
                BufferedReader reader =  new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ( (line = reader.readLine()) != null) {
                        builder.append(line);
                        builder.append(System.getProperty("line.separator"));
                }
                result = builder.toString();

                System.out.print("\n\nWorking\t");

                line =  "Same";
            }
            line= "not same";


        } catch (Exception err) {
            System.out.print(err);
        }
        return line;
    }

    @GET
    @Path("phase-two")
    @Produces(MediaType.TEXT_PLAIN)
    public String authToken(
        @QueryParam("user_id") @DefaultValue("userId") String userId,
        @QueryParam("site_id") @DefaultValue("siteId") String siteId,
        @QueryParam("timestamp") @DefaultValue("timestamp") String timestamp,
        @QueryParam("authorization_code") @DefaultValue("authorization_code") String authorization_code,
        @QueryParam("callback_url") @DefaultValue("callback_url") String callback_url
    ){
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(callback_url);
            String json = "{ client_id:"+clientId+", client_secret:"+secretKey+", authorization_code:"+authorization_code+" }";
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = client.execute(httpPost);
            int responseFinal= response.getStatusLine().getStatusCode();
            client.close(); 

            return "Authorization Token sent";
        } catch (Exception err) {
            return err.toString();
        }
    }
}