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
    public List<Ship> getAllShipsByFilter(String name, String planet, ShipType shipType, Long after,
                                          Long before, Boolean isUsed, Double minSpeed, Double maxSpeed,
                                          Integer minCrewSize, Integer maxCrewSize, Double minRating,
                                          Double maxRating) {

        List<Ship> shipList = getAllShips();

        if (name != null) {
            shipList = shipList.stream()
                    .filter(ship -> ship.getName().contains(name))
                    .collect(Collectors.toList());
        }

        if (planet != null) {
            shipList = shipList.stream()
                    .filter(ship -> ship.getPlanet().contains(planet))
                    .collect(Collectors.toList());
        }

        if (minSpeed != null) {
            shipList = shipList.stream()
                    .filter(ship -> ship.getSpeed() >= minSpeed)
                    .collect(Collectors.toList());
        }

        if (maxSpeed != null) {
            shipList = shipList.stream()
                    .filter(ship -> ship.getSpeed() <= maxSpeed)
                    .collect(Collectors.toList());
        }

        if (minRating != null) {
            shipList = shipList.stream()
                    .filter(ship -> ship.getRating() >= minRating)
                    .collect(Collectors.toList());
        }

        if (maxRating != null) {
            shipList = shipList.stream()
                    .filter(ship -> ship.getRating() <= maxRating)
                    .collect(Collectors.toList());
        }

        if (shipType != null) {
            shipList = shipList.stream()
                    .filter(ship -> ship.getShipType().equals(shipType))
                    .collect(Collectors.toList());
        }

        if (after != null) {
            shipList = shipList.stream()
                    .filter(ship -> ship.getProdDate().getTime() >= after)
                    .collect(Collectors.toList());
        }

        if (before != null) {
            int year = new Date(before).getYear() + 1900;
            shipList = shipList.stream()
                    .filter(ship -> ship.getProdDate().getYear() + 1900 <= year)
                    .collect(Collectors.toList());
        }

        if (minCrewSize != null) {
            shipList = shipList.stream()
                    .filter(ship -> ship.getCrewSize() >= minCrewSize)
                    .collect(Collectors.toList());
        }

        if (maxCrewSize != null) {
            shipList = shipList.stream()
                    .filter(ship -> ship.getCrewSize() <= maxCrewSize)
                    .collect(Collectors.toList());
        }

        if (isUsed != null) {
            shipList = shipList.stream()
                    .filter(ship -> ship.isUsed().equals(isUsed))
                    .collect(Collectors.toList());
        }

        return shipList;
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

        if (!checkValidParams(ship)) throw new BadRequestException();

        if (ship.isUsed() == null) ship.setUsed(false);

        Double speed = Math.round(ship.getSpeed() * 100) * 1.0 / 100;

        ship.setRating(getRating(speed, ship.isUsed(), ship.getProdDate()));

        return shipRepository.save(ship);
    }

    @Override
    public Ship createNewShip(String name, String planet, ShipType shipType, Long prodDate,
                              Boolean isUsed, Double speed, Integer crewSize) {
        return null;
    }

    @Override
    public Ship updateShip(Long id, Ship ship) {

        if (!isValidId(id.toString())) throw new BadRequestException();
        if (!shipRepository.existsById(id)) throw new NotFoundException();

        Ship oldShip = getShipById(id.toString());

        String name = ship.getName();
        if (name != null) {
            if (name.length() > 50 || name.isEmpty()) throw new BadRequestException();
            oldShip.setName(name);
        }

        if (ship.getPlanet() != null) {
            if (ship.getPlanet().length() > 50 || ship.getPlanet().isEmpty()) throw new BadRequestException();
            oldShip.setPlanet(ship.getPlanet());
        }

        if (ship.getShipType() != null) {
            oldShip.setShipType(ship.getShipType());
        }

        if (ship.isUsed() != null) {
            oldShip.setUsed(ship.isUsed());
        }

        if (ship.getProdDate() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(ship.getProdDate());
            int prodDate = cal.get(Calendar.YEAR);
            if (prodDate < 2800 || prodDate > 3019) throw new BadRequestException();
            oldShip.setProdDate(ship.getProdDate());
        }

        Double speed = ship.getSpeed();
        if (speed != null) {
            if (speed < 0.01d || speed > 0.99d) throw new BadRequestException();
            oldShip.setSpeed(speed);
        }

        Integer crewSize = ship.getCrewSize();

        if (crewSize != null) {
            if (crewSize < 1 || crewSize > 9999) throw new BadRequestException();

            oldShip.setCrewSize(crewSize);
        }

        oldShip.setRating(getRating(oldShip.getSpeed(), oldShip.isUsed(), oldShip.getProdDate()));

        return shipRepository.save(oldShip);

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

        return getAllShipsByFilter(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRaring, maxRating).size();
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
