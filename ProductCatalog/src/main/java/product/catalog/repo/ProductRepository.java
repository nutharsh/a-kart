package product.catalog.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import product.catalog.entities.Product;
import product.catalog.entities.ProductTypeEnum;

public interface ProductRepository extends CrudRepository<Product, Long>{
    List<Product> findByType(@Param("type") ProductTypeEnum type);
}
