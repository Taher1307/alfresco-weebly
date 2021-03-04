package v3main.weebly.beans;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Magento {

    public Long id = null;
    public String firstName = "";
    public String lastName = "";
    public String email = "";
    public String phone = "";
    public String company = "";
    public Integer orderId = 0;
    public String orderDate = "";
    public Integer customerId = 0;
    public Object fullResponse;

    public Magento(Long id, Integer orderId, String orderDate, Integer customerId, String firstName, String lastName, String email, String company, String phone, Object fullResponse) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.company = company;
        this.phone = phone;
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.customerId = customerId;
        this.fullResponse = fullResponse;
    }

    private static Magento from(Row row) {
        return new Magento(row.getLong("id"), row.getInteger("orderId"), row.getString("orderDate"), row.getInteger("customerId"), row.getString("firstName"), row.getString("lastName"),
            row.getString("email"), row.getString("company"), row.getString("phone"), row.getValue("fullResponse"));
    }

    public static Multi<Magento> findAll(MySQLPool sqlCli) {
        return sqlCli.query("SELECT * FROM magentoData ORDER BY id ASC").execute()
            .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
            .onItem().transform(Magento::from);
    }

    public static String saveData(
        Integer orderId,
        String orderDate,
        Integer customerid,
        String firstName,
        String lastName,
        String phone,
        String company,
        String email,
        JSONObject fullResponse,
        MySQLPool sqlCli        
        ) {
            try {
                sqlCli.query("CREATE TABLE IF NOT EXISTS magentoData (id SERIAL PRIMARY KEY, orderId INT, orderDate TEXT NOT NULL, customerId INT, firstName TEXT NOT NULL, lastName TEXT NOT NULL, email VARCHAR(50), company VARCHAR(50), phone VARCHAR(50), fullResponse JSON)").execute()
                    .flatMap(r -> sqlCli.query("INSERT INTO magentoData (orderId, orderDate, customerId, firstName,lastName,email,company,phone,fullResponse) VALUES ('" + orderId + "','" + orderDate + "','" + customerid + "','" + firstName + "','" + lastName + "','" + email +  "','" + company + "','" + phone + "','" + fullResponse + "')").execute())
                    .await().indefinitely();
            } catch (Exception e) {
                System.out.print(e);
            }
            return "\n\nData saved successfully";
    } 

    public static String getBearertoken(String siteUrl, String jsonEntity) {
        StringBuffer responseToken = new StringBuffer();
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(siteUrl);
            StringEntity entity = new StringEntity(jsonEntity);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = client.execute(httpPost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                responseToken.append(inputLine);
            }
            reader.close();
            client.close();

            System.out.print("\n\n" + inputLine);
        } catch (Exception e) {
            System.out.print(e);
        }
        return responseToken.toString().replaceAll("^\"|\"$", "");
    }

    public static String getOrders(String ordersApi, String bearerToken) {
        StringBuffer responseJson = new StringBuffer();

        try {
		    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String urlQuery = "searchCriteria[filterGroups][0][filters][0][field]='created_at'&searchCriteria[filterGroups][0][filters][0][value]="+LocalDateTime.now().format(dtf)+"&searchCriteria[filterGroups][0][filters][0][conditionType]='>'&searchCriteria[sortOrders][0][direction]='asc'&searchCriteria[sortOrders][0][field]='created_at'";
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(ordersApi+urlQuery);
            httpGet.setHeader("Authorization", "Bearer " + bearerToken);
            httpGet.setHeader("Accept", "*/*");
            httpGet.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = client.execute(httpGet);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                responseJson.append(inputLine);
            }
            reader.close();
            client.close();
        } catch (Exception e) {
            System.out.print(e);
        }
        return responseJson.toString();
    }

    public static String saveAlfrescoData(String alfrescoApi, String jsonEntity, String authToken) {
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(alfrescoApi);
            StringEntity entity = new StringEntity(jsonEntity);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Authorization", authToken);
            httpPost.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = client.execute(httpPost);
            client.close();
            System.out.print(response);
        } catch (Exception e) {
            System.out.print(e);
        }
        return "";
    }
}