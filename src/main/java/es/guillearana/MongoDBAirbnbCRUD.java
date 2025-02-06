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

    public Document findListing(int maxPrice, int minBedrooms) {
        Document foundListing = collection.find(Filters.and(
                Filters.lte("price", maxPrice),
                Filters.gte("bedrooms", minBedrooms)
        )).first();
        System.out.println("--- Leer (Consultar) ---");
        System.out.println(foundListing != null ? foundListing.toJson() : "No se encontraron alojamientos.");
        return foundListing;
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

        Document foundListing = crud.findListing(100, 1);

        if (foundListing != null) {
            crud.updateListingPrice(foundListing.get("_id"), 90);
            crud.deleteListing(foundListing.get("_id"));
        }

        crud.close();
    }
}
