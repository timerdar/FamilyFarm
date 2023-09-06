package com.timerdar.FamilyFarm.tablesControllers;

import com.timerdar.FamilyFarm.DatabaseController;
import com.timerdar.FamilyFarm.models.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.NoSuchElementException;

@RestController
public class ProductsTableController extends DatabaseController {

    @PostMapping("/products")
    public ResponseEntity<String> addNewProduct(@RequestBody Product product) throws IllegalArgumentException {
        String query = "insert into products(product_name, price)" +
                " values(?, ?)";
        int rowsAffected = 0;

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(1, product.getProductName());
            preparedStatement.setFloat(2, product.getPrice());

            rowsAffected = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Product name must be string and price must be float");
        }
        return ResponseEntity.ok(product.toString());
    }

    @PostMapping("/products/price")
    public ResponseEntity<String> changePrice(@RequestBody Product newProduct) {
        String query = "update products set price = ? where product_name = ?";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.setString(2, newProduct.getProductName());
            preparedStatement.setFloat(1, newProduct.getPrice());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException("New price must be greater than 0");
        }
        return ResponseEntity.ok(newProduct.toString());
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<String> getProductById(@PathVariable int productId) throws SQLException {
        String query = "select product_name, price from products where product_id = " + productId;
        Product product = new Product();

        try (Statement statement = getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                product = new Product(resultSet.getString("product_name"), resultSet.getFloat("price"));
            } else {
                throw new NoSuchElementException("No product with id = " + productId);
            }
            return ResponseEntity.ok(product.toString());
        }
    }

    @GetMapping("/products")
    public ResponseEntity<String> getAllProducts() throws SQLException{
        String query = "select product_name, price from products";

        ArrayList<String> data = new ArrayList<>();

        try(Statement statement = getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                Product product = new Product(resultSet.getString(1), resultSet.getFloat(2));
                data.add( "\n" + product.toString());
            }
        }
        return ResponseEntity.ok("{\"products\":" + data.toString() + "\n}");
    }
}
