package v3main.weebly.resources;

import java.sql.Timestamp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.inject.Inject;

import org.json.*;

import java.io.*;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.lang.*;
import java.util.Date;
import java.util.Objects;
import java.time.*;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;


import org.json.JSONObject;

import io.smallrye.mutiny.Multi;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import javax.enterprise.context.ApplicationScoped;



import v3main.weebly.beans.*;
// import v3main.weebly.scheduler.Cron;

@Path("/get-data")
@ApplicationScoped 
public class GetWeeblyResource {

    @ConfigProperty(name="weebly.order.url")
    String weeblyUrl;

    @ConfigProperty(name="alfresco.node.api")
    String alfrescoAPI;

    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool sqlClient;

    // @Inject
    // Cron cron;

    public JSONArray finalArray = new JSONArray();
    public JSONObject modifiedObject = new JSONObject();
    public JSONObject finalJsonObject = new JSONObject();

    public String firstName = "";
    public String lastName = "";
    public String email = "";
    public String company = "";
    public String phone = "";

    @Scheduled(cron = "0 */1 * * * ?")
    public void cron (ScheduledExecution execution) {
        weeblyData();
        return;
    }

    @GET
    public Multi<Weebly> weeblyData() {
        System.out.print("\n\nCRON WORKING\t");

        // String[] url = {"curl", "-X",  "GET", "-H", "Accept: application/json", weeblyUrl};
        // ProcessBuilder processBuilder = new ProcessBuilder(url);
        // Process p;
        // StringBuilder builder = new StringBuilder();
        // p = processBuilder.start();
        // BufferedReader reader =  new BufferedReader(new InputStreamReader(p.getInputStream()));
        // while ( (line = reader.readLine()) != null) {
        //         builder.append(line);
        //         builder.append(System.getProperty("line.separator"));
        // }
        // result = builder.toString();

        
        JSONArray jarray = new JSONArray("[{\"user_id\":\"123456\",\"site_id\":\"987654321\",\"order_id\":\"963728582\",\"order_date\":1415061315,\"full_name\":\"Jane Doe \",\"order_status\":\"pending\",\"order_currency\":\"USD\",\"order_total\":2}]");
        
        for (int i=0; i < jarray.length(); i++) {
            // JSONArray jarr = (JSONArray) jarray.getJSONObject(i).getJSONArray("elements");
            String orderId = jarray.getJSONObject(i).getString("order_id");
            LocalDateTime date = LocalDateTime.now().minusHours(1);
            Date newDate = new Date(Timestamp.valueOf(date).getTime());
            Date resDate = new Date(new Timestamp(jarray.getJSONObject(i).getLong("order_date")).getTime());

            // String[] url1 = {"curl", "-X",  "GET", "-H", "Accept: application/json", weeblyUrl+"/"+orderId};
            // ProcessBuilder processBuilder = new ProcessBuilder(url1);
            // Process p;
            // StringBuilder builder = new StringBuilder();
            // p = processBuilder.start();
            // BufferedReader reader =  new BufferedReader(new InputStreamReader(p.getInputStream()));
            // while ( (line = reader.readLine()) != null) {
            //         builder.append(line);
            //         builder.append(System.getProperty("line.separator"));
            // }
            // result = builder.toString();


            JSONObject responseObject = new JSONObject("{\n\"user_id\": \"123456\",\n\"site_id\": \"987654321\",\n\"order_id\": \"997590262\",\n\"order_date\": 1443305684,\n\"full_name\": \"Jane Doe\",\n\"is_marketing_updates_subscribed\": false,\n\"order_status\": \"pending\",\n\"order_currency\": \"USD\",\n\"order_item_tax_total\": 8.50,\n\"order_tax_total\": 8.50,\n\"order_tax_rates\": {\n\"total_tax\": 8.50,\n\"rates\": [\n{\n\"name\": \"State\",\n\"rate\": 0.075,\n\"amount\":7.5\n},\n{\n\"name\": \"City\",\n\"rate\": 0.010,\n\"amount\": 1.0\n}\n]\n},\n\"order_shipping_total\": 3,\n\"order_shipping_taxes_total\": 0,\n\"order_shipping_subtotal\": 3,\n\"order_shipping_method\": \"USPS\",\n\"order_subtotal\": 3,\n\"order_total\": 6,\n\"order_notes\": null,\n\"created_date\": 1442892536,\n\"updated_date\": 1443548303,\n\"items\": [\n{\n\"discounted_price\": 2,\n\"track_inventory\": true,\n\"order_id\": \"997590262\",\n\"order_item_id\": \"1\",\n\"site_product_id\": \"2\",\n\"site_product_sku_id\": 2,\n\"name\": \"T-shirt\",\n\"short_description\": \"100% Cotton\",\n\"quantity\": 1,\n\"product_type\": \"physical\",\n\"download_limit_type\": null,\n\"download_units_remaining\": null,\n\"price\": 10,\n\"sale_price\": 8,\n\"total_price\": 8,\n\"weight\": 1,\n\"weight_unit\": \"lb\",\n\"sku\": null,\n\"options\": {\n\"Color\": \"red\",\n\"Size\": \"small\"\n},\n\"returned\": null,\n\"discounts\": [\n{\n\"discount_type\": \"markdown\",\n\"price\": 2,\n\"discount\": 0\n}\n],\n\"original_product_url\": \"http://mystore.weebly.com/store/p2/tshirt.html\",\n\"user_id\": \"123456\",\n\"site_id\": \"987654321\",\n\"created_date\": 1442892536,\n\"updated_date\": 1442892536\n},\n{\n\"discounted_price\": 1,\n\"track_inventory\": true,\n\"order_id\": \"997590262\",\n\"order_item_id\": \"2\",\n\"site_product_id\": \"1\",\n\"site_product_sku_id\": 1,\n\"name\": \"Sticker\",\n\"short_description\": \"Iron on sticker\",\n\"quantity\": 1,\n\"product_type\": \"physical\",\n\"download_limit_type\": null,\n\"download_units_remaining\": null,\n\"price\": 1,\n\"sale_price\": null,\n\"total_price\": 1,\n\"weight\": 1,\n\"weight_unit\": \"lb\",\n\"sku\": \" ghjktyu t\",\n\"options\": {\n\"color\": \"rainbow\"\n},\n\"returned\": null,\n\"discounts\": [],\n\"original_product_url\": \"http://mystore.weebly.com/store/p1/sticker.html\",\n\"user_id\": \"123456\",\n\"site_id\": \"987654321\",\n\"created_date\": 1443295500,\n\"updated_date\": 1443295500\n}\n],\n\"shipments\": [\n{\n\"order_id\": \"997590262\",\n\"order_shipment_id\": 1,\n\"shipping_provider\": \"USPS\",\n\"weight\": 2,\n\"weight_unit\": \"lb\",\n\"height\": 3,\n\"width\": 4,\n\"depth\": 5,\n\"full_name\": \"APITest JAVA\",\n\"email\": \"apitestfromjava@email.com\",\n\"phone\": \"555-555-5555\",\n\"business_name\": \"Information technology\",\n\"street\": \"460 Bryant St\",\n\"street2\": null,\n\"city\": \"San Francisco\",\n\"region\": \"CA\",\n\"country\": \"US\",\n\"postal_code\": \"94107\",\n\"shipment_tax_total\": 0,\n\"shipment_tax_rates\": null,\n\"price\": 3,\n\"shipment_total\": 3,\n\"charge_taxes_on_shipping\": false,\n\"title\": \"USPS\",\n\"shipment_date\": null,\n\"tracking_number\": 123331,\n\"status\": \"shipped\",\n\"user_id\": \"123456\",\n\"site_id\": \"987654321\",\n\"created_date\": 1443305683,\n\"updated_date\": 1443547605,\n\"transactions\": [\n{\n\"order_id\": \"997590262\",\n\"order_shipment_id\": 1,\n\"order_shipment_transaction_id\": 1,\n\"tx_id\": \"123331\",\n\"tx_type\": \"USPS\",\n\"tx_date\": 1443547605,\n\"tx_status\": \"shipped\",\n\"user_id\": \"123456\",\n\"site_id\": \"987654321\",\n\"created_date\": 1443547605,\n\"updated_date\": 1443547605\n},\n{\n\"order_id\": \"997590262\",\n\"order_shipment_id\": 1,\n\"order_shipment_transaction_id\": 2,\n\"tx_id\": \"123331\",\n\"tx_type\": \"USPS\",\n\"tx_date\": 1443548303,\n\"tx_status\": \"label_returned\",\n\"user_id\": \"123456\",\n\"site_id\": \"987654321\",\n\"created_date\": 1443548303,\n\"updated_date\": 1443548303\n}\n]\n}\n],\n\"billings\": [\n{\n\"order_id\": \"997590262\",\n\"order_billing_id\": 1,\n\"gateway\": \"Stripe\",\n\"full_name\": \"Jim Doe\",\n\"email\": \"jim.doe@email.com\",\n\"phone\": null,\n\"business_name\": null,\n\"street\": null,\n\"street2\": null,\n\"city\": null,\n\"region\": null,\n\"country\": null,\n\"postal_code\": \"94107\",\n\"user_id\": \"123456\",\n\"site_id\": \"987654321\",\n\"created_date\": 1443305683,\n\"updated_date\": 1443305683,\n\"transactions\": [\n{\n\"order_id\": \"997590262\",\n\"order_billing_id\": 1,\n\"order_billing_transaction_id\": 1,\n\"method\": \"purchase\",\n\"status\": \"success\",\n\"amount\": 600,\n\"currency\": \"USD\",\n\"tx_fee\": 0,\n\"tx_id\": \"ch_16pQwKKRCoO7IeaF6wfR7gBB\",\n\"tx_message\": \"Transaction approved\",\n\"cc_type\": \"Visa\",\n\"cc_last_4\": \"4242\",\n\"user_id\": \"123456\",\n\"site_id\": \"987654321\",\n\"created_date\": 1443305683,\n\"updated_date\": 1443305684\n}\n]\n}\n],\n\"coupons\": []\n}");

            JSONArray shipmentDetails = (JSONArray) responseObject.getJSONArray("shipments");

            for ( int j=0; j < shipmentDetails.length(); j++) {

                String fullname = shipmentDetails.getJSONObject(j).getString("full_name");
                String[] firstNameArray = fullname.split(" ", 1);
                String[] lastNameArray = fullname.split(" ", 2);
                for(String str: firstNameArray) {
                    firstName = str;
                }
                for (String str1: lastNameArray){
                    lastName = str1;
                }
        
                email = shipmentDetails.getJSONObject(j).getString("email");
                phone = shipmentDetails.getJSONObject(j).getString("phone");
                company = shipmentDetails.getJSONObject(j).getString("business_name");

                modifiedObject.put("dl:contactFirstName", firstName);
                modifiedObject.put("dl:contactLastName", lastName);
                modifiedObject.put("dl:contactEmail", email);                
                modifiedObject.put("dl:contactPhoneMobile", phone);                
                modifiedObject.put("dl:contactCompany", company);

                finalJsonObject.put("name", "v3mdata");
                finalJsonObject.put("nodeType", "dl:contact");
                finalJsonObject.put("properties", modifiedObject);
                
                try {
                    CloseableHttpClient client = HttpClientBuilder.create().build();
                    HttpPost httpPost = new HttpPost(alfrescoAPI);
                    String json = finalJsonObject.toString();
                    StringEntity entity = new StringEntity(json);
                    httpPost.setEntity(entity);
                    httpPost.setHeader("Accept", "*/*");
                    httpPost.setHeader("Authorization", "Basic YWRtaW46YWRtaW4=");
                    httpPost.setHeader("Content-type", "application/json");
                    CloseableHttpResponse response = client.execute(httpPost);
                    client.close(); 
                } catch (Exception e) {
                    //TODO: handle exception
                    System.out.print("\n\n Exception \t"+e);
                }
            };

            Weebly.saveData(firstName, lastName, phone, company, email, sqlClient);
            
        };

        // cron.get();

        return Weebly.findAll(sqlClient);
    }
}
