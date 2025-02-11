package com.gis.dto.car;

import com.gis.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CarResponse {
    String id;
    String licensePlate;
    String image;
    String description;
    VehicleType vehicleType;
}
