package com.BackEnd.Master.GYM.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.BackEnd.Master.GYM.dto.AppUserDto;
import com.BackEnd.Master.GYM.entity.AppUsers;
import com.BackEnd.Master.GYM.security.config.DefaultAvatar;
import com.BackEnd.Master.GYM.services.StorageService;

@Mapper(componentModel = "spring")
public abstract class AppUserMapper {

    @Autowired
    protected StorageService storageService;

     // Mapping user -> UserDto
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "displayName", target = "displayName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "telephone", target = "telephone")
    @Mapping(target = "motDePasse", ignore = true)
    @Mapping(source = "role.roleName", target = "roleName")
    @Mapping(target = "profileImage", expression = "java(resolveProfileImage(entity))")
    public abstract AppUserDto map(AppUsers entity);


    // Mapping List<User> -> List<AppUserDto>
    public abstract List<AppUserDto> map(List<AppUsers> entities);

    // Mapping AppUserDto -> User
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "displayName", target = "displayName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "telephone", target = "telephone")
    @Mapping(source = "motDePasse", target = "motDePasse")
    @Mapping(source = "roleName", target = "role", ignore = true)
    @Mapping(source = "profileImage", target = "profileImage", ignore = true)
    public abstract AppUsers unMap(AppUserDto dto);

    // Mapping AppUserDto -> User pour mise à jour
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "displayName", target = "displayName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "telephone", target = "telephone")
    @Mapping(source = "motDePasse", target = "motDePasse")
    @Mapping(source = "roleName", target = "role", ignore = true)
    @Mapping(source = "profileImage", target = "profileImage", ignore = true)
    public abstract void updateEntityFromDto(@MappingTarget AppUsers entity, AppUserDto dto);

    // Resolves the stored object key (or the default avatar key) to a full URL using the
    // currently configured MinIO host, so the host/port is never baked into stored data
    protected String resolveProfileImage(AppUsers entity) {
        String key = entity.getProfileImage() != null ? entity.getProfileImage() : DefaultAvatar.key();
        return storageService.resolveUrl(key);
    }

}
