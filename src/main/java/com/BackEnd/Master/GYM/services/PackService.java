package com.BackEnd.Master.GYM.services;

import com.BackEnd.Master.GYM.entity.Pack;

import java.util.List;
import java.util.Optional;

public interface PackService {
    // get packs
    Pack findById(Long id);

    // listAll
    List<Pack> findAll(Boolean includingSoftDeleted);

    // create pack
    Pack insert(Pack pack);

    // update pack
    Pack update(Pack pack);

    // find pack by name
    Optional<Pack> findByName(String packName);

    // soft delete pack
    Pack softDeleteById(Long id);

    // restore soft delete
    Pack restoreSoftDelete(Long id);

    // delete pack
    Pack deleteById(Long id);
}
