package com.BackEnd.Master.GYM.controller;

import com.BackEnd.Master.GYM.Exceptions.ResourceNotFoundException;
import com.BackEnd.Master.GYM.Mapper.PackMapper;
import com.BackEnd.Master.GYM.dto.PackDto;
import com.BackEnd.Master.GYM.entity.Pack;
import com.BackEnd.Master.GYM.services.PackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/packs")
@CrossOrigin("*")
public class PackController {
    private final PackService packService;
    private final PackMapper packMapper;

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @GetMapping("/{id}")
    public ResponseEntity<PackDto> findById(@PathVariable Long id) {
        var currentPack = packService.findById(id);
        return ResponseEntity.ok(packMapper.entityToDto(currentPack));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_Admin', 'ROLE_Coach')")
    @GetMapping
    public ResponseEntity<List<PackDto>> findAll(@RequestParam(defaultValue = "false") boolean includeDeleted) {
        var currentPackList = packService.findAll(includeDeleted);
        return ResponseEntity.ok(currentPackList.stream().map(packMapper::entityToDto).toList());
    }

    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @PostMapping()
    public ResponseEntity<PackDto> insert(@RequestBody PackDto dto) {
        Optional<Pack> packWithSameName = packService.findByName(dto.getName());
        if (packWithSameName.isPresent()) {
            throw new ResourceNotFoundException("Pack already exists with name: " + dto.getName());
        }
        Pack pack = packMapper.dtoToEntity(dto);
        PackDto packDto = packMapper.entityToDto(packService.insert(pack));
        return ResponseEntity.ok(packDto);
    }

    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @PutMapping()
    public ResponseEntity<PackDto> update(@RequestBody PackDto dto) {
        Pack pack = packMapper.dtoToEntity(dto);
        Pack updatedPack = packService.update(pack);
        return ResponseEntity.ok(packMapper.entityToDto(updatedPack));
    }

    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @PatchMapping("/{id}/soft-delete")
    public ResponseEntity<PackDto> softDeleteById(@PathVariable Long id) {
        return ResponseEntity.ok(packMapper.entityToDto(packService.softDeleteById(id)));
    }

    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @PatchMapping("/{id}/restore")
    public ResponseEntity<PackDto> restoreSoftDelete(@PathVariable Long id) {
        return ResponseEntity.ok(packMapper.entityToDto(packService.restoreSoftDelete(id)));
    }

    @PreAuthorize("hasAuthority('ROLE_Admin')")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        packService.deleteById(id);
    }
}
