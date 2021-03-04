package v3main.weebly.resources;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.*;
import io.smallrye.mutiny.Multi;
import io.quarkus.scheduler.Scheduled;
import javax.enterprise.context.ApplicationScoped;
import v3main.weebly.beans.Magento;

@Path("/get-data")
@ApplicationScoped 
public class GetMagentoData {

    @ConfigProperty(name="magento.admin.username", defaultValue = "")
    String magentoUserName;

    @ConfigProperty(name="magento.admin.password", defaultValue = "")
    String magentoPassword;

    @ConfigProperty(name="magento.get.access.token", defaultValue = "")
    String accessapi;
    
    @ConfigProperty(name="magento.get.orders", defaultValue = "")
    String magentoOrdersApi;

    @ConfigProperty(name="alfresco.node.api", defaultValue = "")
    String alfrescoAPI;

    @ConfigProperty(name="alfresco.auth.token", defaultValue = "")
    String alfrescoAuthToken;

    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool sqlCli;

    public String firstName = "";
    public String lastName = "";
    public String email = "";
    public String company = "";
    public String phone = "";
    public Integer orderId = 0;
    public Integer customerId = 0;
    public String orderDate = "";

    @Scheduled(cron = "0 */1 * * * ?")
    public void cron () {
        magentoData();
        return;
    }

    @GET
    public Multi<Magento> magentoData() {
        System.out.print("\n\nCRON WORKING\t");

        JSONObject jsonEntity = new JSONObject();
        jsonEntity.put("username", magentoUserName);
        jsonEntity.put("password", magentoPassword);

        //Get magento Bearer Token
        String bearerToken = Magento.getBearertoken(accessapi, jsonEntity.toString());

        //Get Magento Orders
        String orderData = Magento.getOrders(magentoOrdersApi, bearerToken);

        

        //construct response JSON
        JSONObject orderDataInJson = new JSONObject(orderData);
        JSONArray itemsArray = (JSONArray) orderDataInJson.getJSONArray("items");

        for (int i=0; i < itemsArray.length(); i++) {
            orderId = itemsArray.getJSONObject(i).getInt("entity_id");
            orderDate = itemsArray.getJSONObject(i).getString("created_at");
            customerId = itemsArray.getJSONObject(i).getInt("customer_id");
            firstName = itemsArray.getJSONObject(i).getJSONObject("billing_address").getString("firstname");
            lastName = itemsArray.getJSONObject(i).getJSONObject("billing_address").getString("lastname");
            email = itemsArray.getJSONObject(i).getJSONObject("billing_address").getString("email");
            // company = itemsArray.getJSONObject(i).getJSONObject("billing_address").getString("company");
            company = "company";
            phone = itemsArray.getJSONObject(i).getJSONObject("billing_address").getString("telephone");
            
            JSONObject modifiedObject = new JSONObject();
            modifiedObject.put("dl:contactFirstName", firstName);
            modifiedObject.put("dl:contactLastName", lastName);
            modifiedObject.put("dl:contactEmail", email);
            modifiedObject.put("dl:contactPhoneMobile", phone);
            modifiedObject.put("dl:contactCompany", company);
    
            JSONObject finalJsonObject = new JSONObject();
            finalJsonObject.put("name", "v3mdata");
            finalJsonObject.put("nodeType", "dl:contact");
            finalJsonObject.put("properties", modifiedObject);
    
        //     //Save Orders
            Magento.saveData(orderId, orderDate, customerId, firstName, lastName, phone, company, email, orderDataInJson, sqlCli);
    
        //     //Save Alfresco Data
        //     Magento.saveAlfrescoData(alfrescoAPI, finalJsonObject.toString(), alfrescoAuthToken);
        }
        return Magento.findAll(sqlCli);
    }
}