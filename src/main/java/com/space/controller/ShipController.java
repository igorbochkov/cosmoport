package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/rest/ships")
public class ShipController {

    private final ShipServiceImpl shipService;

    @Autowired
    public ShipController(ShipServiceImpl shipService) {
        this.shipService = shipService;
    }

    @GetMapping
    public List<Ship> getAllShips(@RequestParam(required = false) String name,
                                  @RequestParam(required = false) String planet,
                                  @RequestParam(required = false) ShipType shipType,
                                  @RequestParam(required = false) Long after,
                                  @RequestParam(required = false) Long before,
                                  @RequestParam(required = false) Boolean isUsed,
                                  @RequestParam(required = false) Double minSpeed,
                                  @RequestParam(required = false) Double maxSpeed,
                                  @RequestParam(required = false) Integer minCrewSize,
                                  @RequestParam(required = false) Integer maxCrewSize,
                                  @RequestParam(required = false) Double minRating,
                                  @RequestParam(required = false) Double maxRating,
                                  @RequestParam(required = false) ShipOrder order,
                                  @RequestParam(required = false) Integer pageNumber,
                                  @RequestParam(required = false) Integer pageSize) {

        List<Ship> shipList = shipService.getAllShipsByFilter(name, planet, shipType, after, before,
                isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        return shipService.getShipListByOrderByPage(shipList, order, pageNumber, pageSize);
    }

    @GetMapping("/count")
    public int shipCount(@RequestParam(required = false) String name,
                         @RequestParam(required = false) String planet,
                         @RequestParam(required = false) ShipType shipType,
                         @RequestParam(required = false) Long after,
                         @RequestParam(required = false) Long before,
                         @RequestParam(required = false) Boolean isUsed,
                         @RequestParam(required = false) Double minSpeed,
                         @RequestParam(required = false) Double maxSpeed,
                         @RequestParam(required = false) Integer minCrewSize,
                         @RequestParam(required = false) Integer maxCrewSize,
                         @RequestParam(required = false) Double minRating,
                         @RequestParam(required = false) Double maxRating,
                         @RequestParam(required = false) ShipOrder order,
                         @RequestParam(required = false) Integer pageNumber,
                         @RequestParam(required = false) Integer pageSize) {

        return shipService.getShipCount(name, planet, shipType, after, before,
                isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

    }


    @PostMapping
    public Ship createNewShip(@RequestBody Ship ship) {
        return shipService.createNewShip(ship);
    }

    @PostMapping("/{id}")
    public Ship updateShip(@RequestBody Ship ship,
                           @PathVariable Long id) {
        return shipService.updateShip(id, ship);
    }

    @GetMapping("/{id}")
    public Ship getShipById(@PathVariable(name = "id") String id) {
        return shipService.getShipById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteShipById(@PathVariable(name = "id") String id) {
        shipService.deleteShipById(id);
    }
}
