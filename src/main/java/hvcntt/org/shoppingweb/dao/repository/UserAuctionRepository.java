package hvcntt.org.shoppingweb.dao.repository;import hvcntt.org.shoppingweb.dao.entity.Auction;import hvcntt.org.shoppingweb.dao.entity.User;import hvcntt.org.shoppingweb.dao.entity.UserAuction;import java.util.List;import java.util.Set;import org.springframework.data.domain.Page;import org.springframework.data.domain.Pageable;import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.data.jpa.repository.Query;import org.springframework.data.repository.query.Param;public interface UserAuctionRepository extends JpaRepository<UserAuction, String> {    List<UserAuction> findByUser(User user);    Page<UserAuction> findByAuction(Auction auction, Pageable pageable);    List<UserAuction> findByAuction(Auction auction);    List<UserAuction> findDistinctTop5ByAuctionOrderByPriceDesc(Auction auction);    UserAuction findFirstByAuctionOrderByPriceDesc(Auction auction);}