package com.BackEnd.Master.GYM.services.Impl;

import com.BackEnd.Master.GYM.Exceptions.EntityNotFoundException;
import com.BackEnd.Master.GYM.Mapper.PackMapper;
import com.BackEnd.Master.GYM.entity.Pack;
import com.BackEnd.Master.GYM.repository.PackRepo;
import com.BackEnd.Master.GYM.services.PackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PackServiceImpl implements PackService {

    private final PackRepo packRepo;

    @Override
    public Pack findById(Long id) {
        return packRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Pack not found with ID: " + id));
    }

    @Override
    public List<Pack> findAll(Boolean includingSoftDeleted) {
        return includingSoftDeleted ? packRepo.findAll(): packRepo.findByDeletedAtIsNull();
    }

    @Override
    public Pack insert(Pack pack) {
        return packRepo.save(pack);
    }

    @Override
    public Pack update(Pack pack) {
        var currentPackState = packRepo.findById(pack.getId()).orElseThrow(() -> new EntityNotFoundException("Pack not found with ID: " + pack.getId()));
        currentPackState.setDescription(pack.getDescription());
        currentPackState.setName(pack.getName());
        currentPackState.setNMonth(pack.getNMonth());
        currentPackState.setPrice(pack.getPrice());
        return packRepo.save(currentPackState);
    }

    @Override
    public Optional<Pack> findByName(String packName) {
        return packRepo.findByName(packName);
    }

    @Override
    public Pack softDeleteById(Long id) {
        var currentPackState = packRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Pack not found with ID: " + id));
        currentPackState.setDeletedAt(new Date());
        return packRepo.save(currentPackState);
    }
    @Override
    public Pack restoreSoftDelete(Long id) {
        var currentPackState = packRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Pack not found with ID: " + id));
        currentPackState.setDeletedAt(null);
        return packRepo.save(currentPackState);
    }

    @Override
    public Pack deleteById(Long id) {
        var currentPackState = packRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Pack not found with ID: " + id));
        packRepo.delete(currentPackState);
        return currentPackState;
    }
}
