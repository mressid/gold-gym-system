package com.BackEnd.Master.GYM.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.BackEnd.Master.GYM.dto.customerDto;
import com.BackEnd.Master.GYM.entity.customer;
import com.BackEnd.Master.GYM.security.config.DefaultAvatar;
import com.BackEnd.Master.GYM.services.StorageService;

@Mapper(componentModel = "spring")
public abstract class customerMapper {

    @Autowired
    protected StorageService storageService;

    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "telephone", target = "telephone")
    @Mapping(source = "pack", target = "pack")
    @Mapping(source = "dateDebut", target = "dateDebut")
    @Mapping(source = "dateFin", target = "dateFin")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "profileImage", expression = "java(resolveProfileImage(entity))")
    public abstract customerDto map(customer entity);


    public abstract List<customerDto> map(List<customer> entities);


    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "telephone", target = "telephone")
    @Mapping(source = "pack", target = "pack")
    @Mapping(source = "dateDebut", target = "dateDebut")
    @Mapping(source = "dateFin", target = "dateFin")
    @Mapping(source = "userId", target = "user", ignore = true)
    @Mapping(source = "profileImage", target = "profileImage", ignore = true)
    public abstract customer unMap(customerDto dto);


    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "telephone", target = "telephone")
    @Mapping(source = "pack", target = "pack")
    @Mapping(source = "dateDebut", target = "dateDebut")
    @Mapping(source = "dateFin", target = "dateFin")
    @Mapping(source = "userId", target = "user", ignore = true)
    @Mapping(source = "profileImage", target = "profileImage", ignore = true)
    public abstract void updateEntityFromDto(@MappingTarget customer entity, customerDto dto);

    // Resolves the stored object key (or the default avatar key) to a full URL using the
    // currently configured MinIO host, so the host/port is never baked into stored data
    protected String resolveProfileImage(customer entity) {
        String key = entity.getProfileImage() != null ? entity.getProfileImage() : DefaultAvatar.key();
        return storageService.resolveUrl(key);
    }
}
