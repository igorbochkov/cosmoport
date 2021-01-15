package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exceptions.BadRequestException;
import com.space.exceptions.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShipServiceImpl implements ShipService {

    private final ShipRepository shipRepository;

    @Autowired
    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public List<Ship> getAllShips() {
        return shipRepository.findAll();
    }

    @Override
    public List<Ship> getAllShipsByFilter(String name, String planet, ShipType shipType,
                                          Long after, Long before, Boolean isUsed, Double minSpeed, Double maxSpeed,
                                          Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating) {

        List<Ship> allShips = getAllShips();

        if (name != null) {
            allShips = allShips.stream().filter(ship -> ship.getName().contains(name)).collect(Collectors.toList());
        }
        if (planet != null) {
            allShips = allShips.stream().filter(ship -> ship.getPlanet().contains(planet)).collect(Collectors.toList());
        }
        if (shipType != null) {
            allShips = allShips.stream().filter(ship -> ship.getShipType().equals(shipType)).collect(Collectors.toList());
        }
        if (after != null) {
            allShips = allShips.stream().filter(ship -> ship.getProdDate().getTime() >= after).collect(Collectors.toList());
        }
        if (before != null) {
            int year = new Date(before).getYear() + 1900;
            allShips = allShips.stream().filter(ship -> ship.getProdDate().getYear() + 1900 <= year).collect(Collectors.toList());
        }
        if (isUsed != null) {
            allShips = allShips.stream().filter(ship -> ship.isUsed().equals(isUsed)).collect(Collectors.toList());
        }
        if (minSpeed != null) {
            allShips = allShips.stream().filter(ship -> ship.getSpeed() >= minSpeed).collect(Collectors.toList());
        }
        if (maxSpeed != null) {
            allShips = allShips.stream().filter(ship -> ship.getSpeed() <= maxSpeed).collect(Collectors.toList());
        }
        if (minCrewSize != null) {
            allShips = allShips.stream().filter(ship -> ship.getCrewSize() >= minCrewSize).collect(Collectors.toList());
        }
        if (maxCrewSize != null) {
            allShips = allShips.stream().filter(ship -> ship.getCrewSize() <= maxCrewSize).collect(Collectors.toList());
        }
        if (minRating != null) {
            allShips = allShips.stream().filter(ship -> ship.getRating() >= minRating).collect(Collectors.toList());
        }
        if (maxRating != null) {
            allShips.stream().filter(ship -> ship.getRating() <= maxRating).collect(Collectors.toList());
        }

        return allShips;
    }

    @Override
    public List<Ship> getShipListByOrderByPage(List<Ship> ships, ShipOrder order, Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 3;

        return ships.stream().
                skip(pageNumber * pageSize).
                limit(pageSize).
                sorted(getComparator(order)).
                collect(Collectors.toList());
    }

    @Override
    public Ship createNewShip(Ship ship) {
        return null;
    }

    @Override
    public Ship createNewShip(String name, String planet, ShipType shipType, Long prodDate,
                              Boolean isUsed, Double speed, Integer crewSize) {
        return null;
    }

    @Override
    public Ship updateShip(Long id, Ship ship) {
        return null;
    }

    @Override
    public Ship updateShip(Long id, String name, String planet, ShipType shipType, Long prodDate,
                           Boolean isUsed, Double speed, Integer crewSize) {
        return null;
    }

    @Override
    public void deleteShipById(String id) {
        if (!isValidId(id)) throw new BadRequestException();

        Long idDelete = Long.parseLong(id);

        if (!shipRepository.existsById(idDelete)) throw new NotFoundException();

        shipRepository.deleteById(idDelete);
    }

    @Override
    public Ship getShipById(String id) {

        if (!isValidId(id)) throw new BadRequestException();

        Long idGet = Long.parseLong(id);

        if (!shipRepository.existsById(idGet)) throw new NotFoundException();

        return shipRepository.findById(idGet).orElse(null);
    }

    @Override
    public Integer getShipCount(String name, String planet, ShipType shipType, Long after, Long before,
                                Boolean isUsed, Double minSpeed, Double maxSpeed,
                                Integer minCrewSize, Integer maxCrewSize, Double minRaring, Double maxRating) {
        return null;
    }

    @Override
    public double getRating(Double speed, Boolean isUsed, Date prodDate) {

        double k;
        if (isUsed) {
            k = 0.5;
        } else {
            k = 1;
        }

        double rating = (80 * speed * k) * 1.0 / (3019 - (prodDate.getYear() + 1900) + 1);
        rating = Math.round(rating * 100) * 1.0 / 100;

        return rating;
    }


    private Comparator<Ship> getComparator(ShipOrder order) {
        if (order == null) return Comparator.comparing(Ship::getId);

        switch (order.getFieldName()) {
            case "id":
                return Comparator.comparing(Ship::getId);
            case "speed":
                return Comparator.comparing(Ship::getSpeed);
            case "prodDate":
                return Comparator.comparing(Ship::getProdDate);
            case "rating":
                return Comparator.comparing(Ship::getRating);
        }

        return null;
    }

    private boolean isValidId(String id) {
        long idLong;
        try {
            idLong = Long.parseLong(id);
            if (idLong <= 0) return false;
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private boolean checkValidParams(Ship ship) {
        Calendar calendar1 = new GregorianCalendar();
        Calendar calendar2 = new GregorianCalendar();
        calendar1.set(2800, 0, 1);
        calendar2.set(3019, 11, 31);

        if (ship == null ||
                ship.getName() == null || ship.getPlanet() == null ||
                ship.getShipType() == null || ship.getProdDate() == null ||
                ship.getSpeed() == null || ship.getCrewSize() == null ||
                ship.getName().length() > 50 || ship.getPlanet().length() > 50 ||
                ship.getName().equals("") || ship.getPlanet().equals("") ||
                ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99 ||
                ship.getCrewSize() < 1 || ship.getCrewSize() > 9999 ||
                ship.getProdDate().before(calendar1.getTime()) ||
                ship.getProdDate().after(calendar2.getTime()) ||
                ship.getProdDate().getTime() < 0) {

            return false;

        } else return true;
    }

}
