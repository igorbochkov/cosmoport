package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/rest/ships")
public class ShipController {

    private final ShipServiceImpl shipService;

    @Autowired
    public ShipController(ShipServiceImpl shipService) {
        this.shipService = shipService;
    }

    @GetMapping
    public List<Ship> getAllShips(@RequestParam(name = "name", required = false) String name,
                                  @RequestParam(name = "planet", required = false) String planet,
                                  @RequestParam(name = "shipType", required = false) ShipType shipType,
                                  @RequestParam(name = "after", required = false) Long after,
                                  @RequestParam(name = "before", required = false) Long before,
                                  @RequestParam(name = "isUsed", required = false) Boolean isUsed,
                                  @RequestParam(name = "minSpeed", required = false) Double minSpeed,
                                  @RequestParam(name = "maxSpeed", required = false) Double maxSpeed,
                                  @RequestParam(name = "minCrewSize", required = false) Integer minCrewSize,
                                  @RequestParam(name = "maxCrewSize", required = false) Integer maxCrewSize,
                                  @RequestParam(name = "minRating", required = false) Double minRating,
                                  @RequestParam(name = "maxRating", required = false) Double maxRating,
                                  @RequestParam(name = "order", required = false) ShipOrder order,
                                  @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                  @RequestParam(name = "pageSize", required = false) Integer pageSize) {

        List<Ship> allShips = shipService.getAllShipsByFilter(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating,maxRating);

        return shipService.getShipListByOrderByPage(allShips,order, pageNumber, pageSize);
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
