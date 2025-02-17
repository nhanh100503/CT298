package com.gis.mapper;

import com.gis.dto.drive.DriveResponse;
import com.gis.model.Drive;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DriveMapper {
    DriveResponse toDriveResponse(Drive drive);
}
