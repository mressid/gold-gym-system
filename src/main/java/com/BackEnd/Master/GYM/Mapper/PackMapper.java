package com.BackEnd.Master.GYM.Mapper;

import com.BackEnd.Master.GYM.dto.PackDto;
import com.BackEnd.Master.GYM.entity.Pack;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // "spring" allows you to @Autowired this mapper
public interface PackMapper {

    // Converts Entity to DTO (MapStruct automatically matches all fields)
    PackDto entityToDto(Pack pack);

    // Converts DTO to Entity (MapStruct automatically matches all fields)
    Pack dtoToEntity(PackDto packDto);
}
