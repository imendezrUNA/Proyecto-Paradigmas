package com.una.paradigmas.service;

import com.una.paradigmas.model.Product;
import com.una.paradigmas.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    public List<Product>  getProducts(){
        return this.productRepository.findAll();
    }

    public ResponseEntity<Object> newProduct(Product product) {
        productRepository.findProductByName(product.getName());
        Optional<Product> res = productRepository.findProductByName(product.getName());
        HashMap<String, Object> msg = new HashMap<>();


        if(res.isPresent()){
            msg.put("message", "Product already exists");
            return new ResponseEntity<>(
                    msg,
                    HttpStatus.CONFLICT
            );

        }
        productRepository.save(product);
        msg.put("message", "Product created");
        msg.put("product", product);
        return new ResponseEntity<>(
                msg,
                HttpStatus.CREATED
        );
    }

    public ResponseEntity<Object> updateProduct(Product product) {
        Optional<Product> res = productRepository.findById(product.getId());
        HashMap<String, Object> msg = new HashMap<>();

        if(res.isEmpty()){
            msg.put("message", "Product not found");
            return new ResponseEntity<>(
                    msg,
                    HttpStatus.NOT_FOUND
            );
        }
        productRepository.save(product);
        msg.put("message", "Product updated");
        msg.put("product", product);
        return new ResponseEntity<>(
                msg,
                HttpStatus.OK
        );
    }

    public ResponseEntity<Object> deleteProduct(Long id) {
        Optional<Product> res = productRepository.findById(id);
        HashMap<String, Object> msg = new HashMap<>();

        if(res.isEmpty()){
            msg.put("message", "Product not found");
            return new ResponseEntity<>(
                    msg,
                    HttpStatus.NOT_FOUND
            );
        }
        productRepository.deleteById(id);
        msg.put("message", "Product deleted");
        return new ResponseEntity<>(
                msg,
                HttpStatus.OK
        );
    }
}
