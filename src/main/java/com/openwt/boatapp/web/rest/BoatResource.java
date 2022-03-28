package com.openwt.boatapp.web.rest;

import com.openwt.boatapp.repository.BoatRepository;
import com.openwt.boatapp.service.BoatQueryService;
import com.openwt.boatapp.service.BoatService;
import com.openwt.boatapp.service.criteria.BoatCriteria;
import com.openwt.boatapp.service.dto.BoatDTO;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

/**
 * REST controller for managing {@link com.openwt.boatapp.domain.Boat}.
 */
@RestController
@RequestMapping("/api")
public class BoatResource extends AbstractController {

    private static final String ENTITY_NAME = "boat";
    private final Logger log = LoggerFactory.getLogger(BoatResource.class);
    private final BoatService boatService;
    private final BoatRepository boatRepository;
    private final BoatQueryService boatQueryService;

    @Value("${boatapp.clientApp.name}")
    private String applicationName;

    public BoatResource(BoatService boatService, BoatRepository boatRepository, BoatQueryService boatQueryService) {
        this.boatService = boatService;
        this.boatRepository = boatRepository;
        this.boatQueryService = boatQueryService;
    }

    /**
     * {@code POST  /boats} : Create a new boat.
     *
     * @param boatDTO the boatDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new boatDTO, or with status {@code 400 (Bad Request)} if the boat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/boats")
    public ResponseEntity<BoatDTO> createBoat(@Valid @RequestBody BoatDTO boatDTO) throws URISyntaxException {
        log.debug("REST request to save Boat : {}", boatDTO);
        if (boatDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(createAlert("A new boat cannot already have an ID")).build();
        }
        BoatDTO result = boatService.save(boatDTO);
        return ResponseEntity.created(new URI("/api/boats/" + result.getId())).headers(createAlert(applicationName)).body(result);
    }

    /**
     * {@code PUT  /boats/:id} : Updates an existing boat.
     *
     * @param id      the id of the boatDTO to save.
     * @param boatDTO the boatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated boatDTO,
     * or with status {@code 400 (Bad Request)} if the boatDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the boatDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/boats/{id}")
    public ResponseEntity<BoatDTO> updateBoat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BoatDTO boatDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Boat : {}, {}", id, boatDTO);
        if (boatDTO.getId() == null) {
            return ResponseEntity.badRequest().headers(createAlert("Invalid id")).build();
        }
        if (!Objects.equals(id, boatDTO.getId())) {
            return ResponseEntity.badRequest().headers(createAlert("Invalid ID")).build();
        }

        if (!boatRepository.existsById(id)) {
            return ResponseEntity.badRequest().headers(createAlert("Entity not found")).build();
        }

        BoatDTO result = boatService.save(boatDTO);
        return ResponseEntity.ok().headers(createAlert(applicationName)).body(result);
    }

    /**
     * {@code PATCH  /boats/:id} : Partial updates given fields of an existing boat, field will ignore if it is null
     *
     * @param id      the id of the boatDTO to save.
     * @param boatDTO the boatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated boatDTO,
     * or with status {@code 400 (Bad Request)} if the boatDTO is not valid,
     * or with status {@code 404 (Not Found)} if the boatDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the boatDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/boats/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<BoatDTO> partialUpdateBoat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BoatDTO boatDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Boat partially : {}, {}", id, boatDTO);
        if (boatDTO.getId() == null) {
            return ResponseEntity.badRequest().headers(createAlert("Invalid id")).build();
        }
        if (!Objects.equals(id, boatDTO.getId())) {
            return ResponseEntity.badRequest().headers(createAlert("Invalid ID")).build();
        }

        if (!boatRepository.existsById(id)) {
            return ResponseEntity.badRequest().headers(createAlert("Entity not found")).build();
        }

        Optional<BoatDTO> result = boatService.partialUpdate(boatDTO);

        return result.map(ResponseEntity::ok).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * {@code GET  /boats} : get all the boats.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of boats in body.
     */
    @GetMapping("/boats")
    public ResponseEntity<List<BoatDTO>> getAllBoats(BoatCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Boats by criteria: {}", criteria);
        Page<BoatDTO> page = boatQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /boats/count} : count all the boats.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/boats/count")
    public ResponseEntity<Long> countBoats(BoatCriteria criteria) {
        log.debug("REST request to count Boats by criteria: {}", criteria);
        return ResponseEntity.ok().body(boatQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /boats/:id} : get the "id" boat.
     *
     * @param id the id of the boatDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the boatDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/boats/{id}")
    public ResponseEntity<BoatDTO> getBoat(@PathVariable Long id) {
        log.debug("REST request to get Boat : {}", id);
        Optional<BoatDTO> boatDTO = boatService.findOne(id);
        return boatDTO.map(ResponseEntity::ok).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * {@code DELETE  /boats/:id} : delete the "id" boat.
     *
     * @param id the id of the boatDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/boats/{id}")
    public ResponseEntity<Void> deleteBoat(@PathVariable Long id) {
        log.debug("REST request to delete Boat : {}", id);
        boatService.delete(id);
        return ResponseEntity.noContent().headers(createAlert(applicationName)).build();
    }

    private HttpHeaders createAlert(String applicationName) {
        return super.createAlert(applicationName, BoatResource.ENTITY_NAME);
    }
}
