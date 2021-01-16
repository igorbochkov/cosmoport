package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface ShipService {

    List<Ship> getAllShips();
    List<Ship> getAllShipsByFilter(String name, String planet, ShipType shipType, Long after, Long before,
                           Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                           Double minRating, Double maxRating, ShipOrder order, Integer pageNumber, Integer pageSiz);
    List<Ship> getShipListByOrderByPage(List<Ship> ships, ShipOrder order, Integer pageNumber, Integer pageSize);
    Ship createNewShip(Ship ship);
    Ship createNewShip(String name, String planet, ShipType shipType, Long prodDate,
                       Boolean isUsed, Double speed, Integer crewSize);
    Ship updateShip(Long id, Ship ship);

    Ship updateShip(Long id, String name, String planet, ShipType shipType, Long prodDate,
                    Boolean isUsed, Double speed, Integer crewSize);

    void deleteShipById(String id);

    Ship getShipById(String id);

    Integer getShipCount(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
                         Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                         Double minRaring, Double maxRating);
    double getRating(Double speed, Boolean isUsed, Date prodDate);
}
