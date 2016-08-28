package product.pricing;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import product.pricing.exception.CatalogPricingGenericServerException;

@RestController
@SpringBootApplication
@RequestMapping("/v1/catalog/prices")
public class ProductPricingApplication {
    
    private static Logger logger = LoggerFactory.getLogger(ProductPricingApplication.class);
    
    public static void main(String[] args) {
        SpringApplication.run(ProductPricingApplication.class, args);
    }
    
    @Value("${product.catalog.service.uri}")
    private String catalogServiceUrl;
    
    @RequestMapping(method=RequestMethod.GET, value="/product/{id}")
    public String showPrice(@PathVariable("id") Long productId) {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = URI.create(catalogServiceUrl+productId);
        String productDetails = restTemplate.getForObject(uri, String.class);
        logger.info(" msg=catalog_service_call_details resp_body=" + productDetails);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(productDetails);
            JsonNode priceJsonNode = jsonNode.get("price");
            return priceJsonNode.toString();
        } catch (Exception e) {
            logger.error("json processing error", e);
            throw new CatalogPricingGenericServerException();
        }
    }
    
    @RequestMapping(method=RequestMethod.GET)
    public ResponseEntity<?> index() {
        String str = new String("catalog pricing service 1.0");
        return new ResponseEntity<>(str, HttpStatus.OK);
    }
    
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR,reason="server unable to process request")
    @ExceptionHandler(CatalogPricingGenericServerException.class)
    public void exceptionHandler() 
    {
    }

}
