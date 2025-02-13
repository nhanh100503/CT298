package com.gis.service;

import com.gis.model.FreightRate;
import com.gis.model.Price;
import com.gis.model.VehicleType;
import com.gis.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceService {
    private final PriceRepository priceRepository;
    public double calculatePrice(double km, VehicleType vehicleType) {
        List<Price> prices = priceRepository.findByVehicleType(vehicleType);
        prices.sort(Comparator.comparing(p -> p.getFreightRate().getLower()));
        double totalPrice = 0;

        for (Price price : prices) {
            FreightRate rate = price.getFreightRate();
            double applicableKm = Math.min(km, rate.getUpper() - rate.getLower());
            totalPrice += applicableKm * price.getPrice();
            km -= applicableKm;
            if (km <= 0) break;
        }

        return totalPrice;
    }

}
