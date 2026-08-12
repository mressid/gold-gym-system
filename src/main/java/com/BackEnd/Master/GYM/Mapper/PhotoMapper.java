package com.BackEnd.Master.GYM.Mapper;

import com.BackEnd.Master.GYM.dto.PhotoDto;
import com.BackEnd.Master.GYM.entity.Photo;
import com.BackEnd.Master.GYM.services.StorageService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PhotoMapper {

    @Autowired
    protected StorageService storageService;

    @Mapping(source = "album.id", target = "albumId")
    @Mapping(target = "imageName", expression = "java(storageService.resolveUrl(entity.getImageName()))")
    public abstract PhotoDto map(Photo entity);

    public abstract List<PhotoDto> map(List<Photo> entities);

    @Mapping(target = "album", ignore = true)
    @Mapping(target = "imageName", ignore = true)
    public abstract Photo unMap(PhotoDto dto);

    @Mapping(target = "album", ignore = true)
    @Mapping(target = "imageName", ignore = true)
    public abstract void updateEntityFromDto(@MappingTarget Photo entity, PhotoDto dto);
}
