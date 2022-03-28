package com.openwt.boatapp.service;

import com.openwt.boatapp.service.dto.BoatDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.openwt.boatapp.domain.Boat}.
 */
public interface BoatService {
    /**
     * Save a boat.
     *
     * @param boatDTO the entity to save.
     * @return the persisted entity.
     */
    BoatDTO save(BoatDTO boatDTO);

    /**
     * Partially updates a boat.
     *
     * @param boatDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BoatDTO> partialUpdate(BoatDTO boatDTO);

    /**
     * Get all the boats.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BoatDTO> findAll(Pageable pageable);

    /**
     * Get the "id" boat.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BoatDTO> findOne(Long id);

    /**
     * Delete the "id" boat.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
