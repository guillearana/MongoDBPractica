package es.guillearana;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import java.util.List;

public class MongoDBAirbnbCRUD {
    private static final String URI = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "sample_airbnb";
    private static final String COLLECTION_NAME = "listingsAndReviews";
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    public MongoDBAirbnbCRUD() {
        this.mongoClient = MongoClients.create(URI);
        this.database = mongoClient.getDatabase(DATABASE_NAME);
        this.collection = database.getCollection(COLLECTION_NAME);
    }

    public void insertListing(Document listing) {
        collection.insertOne(listing);
        System.out.println("--- Crear (Insertar) ---");
        System.out.println("Nuevo alojamiento insertado: " + listing.toJson());
    }

    public List<Document> findListingCiudad(String city) {
        List<Document> listings = collection.find(Filters.eq("address.market", city)).into(new java.util.ArrayList<>());
        System.out.println("--- Leer (Consultar) ---");
        if (listings.isEmpty()) {
            System.out.println("No se encontraron alojamientos en " + city);
        } else {
            listings.forEach(listing -> System.out.println(listing.toJson()));
        }
        return listings;
    }

    public void updateListingPrice(Object id, int newPrice) {
        collection.updateOne(Filters.eq("_id", id), Updates.set("price", newPrice));
        System.out.println("--- Actualizar ---");
        System.out.println("Alojamiento actualizado: " + collection.find(Filters.eq("_id", id)).first());
    }

    public void deleteListing(Object id) {
        collection.deleteOne(Filters.eq("_id", id));
        System.out.println("--- Eliminar ---");
        System.out.println("Alojamiento eliminado.");
    }

    public void close() {
        mongoClient.close();
    }

    public static void main(String[] args) {
        MongoDBAirbnbCRUD crud = new MongoDBAirbnbCRUD();

        Document newListing = new Document("name", "Apartamento en Gasteiz")
                .append("summary", "Un apartamento acogedor en el centro")
                .append("price", 80)
                .append("bedrooms", 2)
                .append("neighborhood_overview", "Ubicación céntrica con buenas conexiones");
        crud.insertListing(newListing);

        List<Document> foundListings = crud.findListingCiudad("Vitoria-Gasteiz");

        if (!foundListings.isEmpty()) {
            Document firstListing = foundListings.get(0);
            crud.updateListingPrice(firstListing.get("_id"), 90);
            crud.deleteListing(firstListing.get("_id"));
        }

        crud.close();
    }
}
