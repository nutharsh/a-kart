package product.catalog.service;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import product.catalog.entities.Product;
import product.catalog.entities.ProductTypeEnum;
import product.catalog.exception.CatalogServiceGenericServerException;
import product.catalog.repo.ProductRepository;

@RestController
@RequestMapping("/v1/catalog/products")
public class ProductCatalogService {
    private static Logger logger = LoggerFactory.getLogger(ProductCatalogService.class);
    
    private final ProductRepository catalogRepository;
    
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody Product input) {
        Product savedProduct = this.catalogRepository.save(input);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedProduct.getId()).toUri());
        return new ResponseEntity<>(savedProduct, httpHeaders, HttpStatus.CREATED);
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long productId) {
        boolean exists = catalogRepository.exists(productId);
        if(!exists) {
            product.catalog.exception.Error error = new product.catalog.exception.Error("product with id=" + productId + " not found");
            ResponseEntity<product.catalog.exception.Error> resp = new ResponseEntity<product.catalog.exception.Error>(error, HttpStatus.NOT_FOUND);
            return resp;
        }
        return new ResponseEntity<>(catalogRepository.findOne(productId), HttpStatus.OK);
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/query")
    public List<Product> getByType(@RequestParam("type") String type) {
      try {
        return catalogRepository.findByType(ProductTypeEnum.valueOf(type));
      }
      catch (Exception ex) {
          logger.error(" msg=error_while_performing_find_by_type ", ex);
          throw new RuntimeException();
      }
      
    }
    
    
    
    @RequestMapping(method=RequestMethod.GET)
    public List<Product> listAll() {
        List<Product> products = new LinkedList<>();
      try {
          Iterable<Product> all = catalogRepository.findAll();
          for(Product product : all) {
              products.add(product);
          }
        return products;
      }
      catch (Exception ex) {
          logger.error(" msg=error_while_performing_find_by_type ", ex);
          throw new RuntimeException();
      }
      
    }
    
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR,reason="system error")
    @ExceptionHandler(CatalogServiceGenericServerException.class)
    public void exceptionHandler() 
    {
    }
    
    @Autowired
    public ProductCatalogService(ProductRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }
    
    @RequestMapping(method=RequestMethod.DELETE, path="/{id}", produces="application/json")
    ResponseEntity<?> deleteProduct(@PathVariable("id") Long productId) {
        boolean exists = catalogRepository.exists(productId);
        if(!exists) {
            product.catalog.exception.Error error = new product.catalog.exception.Error("product with id=" + productId + " not found");
            ResponseEntity<product.catalog.exception.Error> resp = new ResponseEntity<product.catalog.exception.Error>(error, HttpStatus.NOT_FOUND);
            return resp;
        }
        
        catalogRepository.delete(productId);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
