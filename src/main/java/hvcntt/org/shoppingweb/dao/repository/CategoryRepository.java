package hvcntt.org.shoppingweb.dao.repository;import hvcntt.org.shoppingweb.dao.entity.Category;import org.springframework.data.jpa.repository.JpaRepository;public interface CategoryRepository extends JpaRepository<Category, String> {    Category findByCategoryName(String categoryName);}