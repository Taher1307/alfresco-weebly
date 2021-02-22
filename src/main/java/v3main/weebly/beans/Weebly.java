package v3main.weebly.beans;

import io.smallrye.mutiny.Multi;

import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;

public class Weebly {
    public Long id;
    public String firstName;
    public String lastName;
    public String email;
    public String phone;
    public String company;


    public Weebly(Long id, String firstName, String lastName, String email, String company, String phone)  {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.company = company;
        this.phone = phone;
    }

    private static Weebly from(Row row) {
        return new Weebly(row.getLong("id"), row.getString("firstName"), row.getString("lastName"), row.getString("email"), row.getString("company"), row.getString("phone"));
    }
    
    public static Multi<Weebly> findAll(MySQLPool client) {
        return client.query("SELECT * FROM weeblydata ORDER BY id ASC").execute()
            .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
            .onItem().transform(Weebly::from);
    }

    public static String saveData(
        String firstName,
        String lastName,
        String phone,
        String company,
        String email,
        MySQLPool client
        ) {
            client.query("CREATE TABLE IF NOT EXISTS weeblydata (id SERIAL PRIMARY KEY, firstName TEXT NOT NULL, lastName TEXT NOT NULL, email VARCHAR(50), company VARCHAR(50), phone VARCHAR(50))").execute()
                .flatMap(r -> client.query("INSERT INTO weeblydata (firstName,lastName,email,company,phone) VALUES ('" + firstName + "','" + lastName + "','" + email +  "','" + company + "','" + phone + "')").execute())
                .await().indefinitely();
            return "Data saved successfully";
}
}
