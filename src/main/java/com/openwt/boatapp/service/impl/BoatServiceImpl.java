package com.openwt.boatapp.service.impl;

import com.openwt.boatapp.domain.Boat;
import com.openwt.boatapp.repository.BoatRepository;
import com.openwt.boatapp.service.BoatService;
import com.openwt.boatapp.service.dto.BoatDTO;
import com.openwt.boatapp.service.mapper.BoatMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Boat}.
 */
@Service
@Transactional
public class BoatServiceImpl implements BoatService {

    private final Logger log = LoggerFactory.getLogger(BoatServiceImpl.class);

    private final BoatRepository boatRepository;

    private final BoatMapper boatMapper;

    public BoatServiceImpl(BoatRepository boatRepository, BoatMapper boatMapper) {
        this.boatRepository = boatRepository;
        this.boatMapper = boatMapper;
    }

    @Override
    public BoatDTO save(BoatDTO boatDTO) {
        log.debug("Request to save Boat : {}", boatDTO);
        Boat boat = boatMapper.toEntity(boatDTO);
        boat = boatRepository.save(boat);
        return boatMapper.toDto(boat);
    }

    @Override
    public Optional<BoatDTO> partialUpdate(BoatDTO boatDTO) {
        log.debug("Request to partially update Boat : {}", boatDTO);

        return boatRepository
            .findById(boatDTO.getId())
            .map(
                existingBoat -> {
                    boatMapper.partialUpdate(existingBoat, boatDTO);

                    return existingBoat;
                }
            )
            .map(boatRepository::save)
            .map(boatMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoatDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Boats");
        return boatRepository.findAll(pageable).map(boatMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BoatDTO> findOne(Long id) {
        log.debug("Request to get Boat : {}", id);
        return boatRepository.findById(id).map(boatMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Boat : {}", id);
        boatRepository.deleteById(id);
    }
}
