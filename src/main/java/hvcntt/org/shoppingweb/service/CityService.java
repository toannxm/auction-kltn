package hvcntt.org.shoppingweb.service;import java.util.List;import hvcntt.org.shoppingweb.dao.entity.City;/** * Created by Nguyen on 12/04/2017. */public interface CityService {	List<City> getAll();    City findById(String cityId);}