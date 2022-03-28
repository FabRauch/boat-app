package com.openwt.boatapp.service;

import com.openwt.boatapp.domain.*; // for static metamodels
import com.openwt.boatapp.domain.Boat;
import com.openwt.boatapp.repository.BoatRepository;
import com.openwt.boatapp.service.criteria.BoatCriteria;
import com.openwt.boatapp.service.dto.BoatDTO;
import com.openwt.boatapp.service.mapper.BoatMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Boat} entities in the database.
 * The main input is a {@link BoatCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BoatDTO} or a {@link Page} of {@link BoatDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BoatQueryService extends QueryService<Boat> {

    private final Logger log = LoggerFactory.getLogger(BoatQueryService.class);

    private final BoatRepository boatRepository;

    private final BoatMapper boatMapper;

    public BoatQueryService(BoatRepository boatRepository, BoatMapper boatMapper) {
        this.boatRepository = boatRepository;
        this.boatMapper = boatMapper;
    }

    /**
     * Return a {@link List} of {@link BoatDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BoatDTO> findByCriteria(BoatCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Boat> specification = createSpecification(criteria);
        return boatMapper.toDto(boatRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BoatDTO} which matches the criteria from the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page     The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BoatDTO> findByCriteria(BoatCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Boat> specification = createSpecification(criteria);
        return boatRepository.findAll(specification, page).map(boatMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BoatCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Boat> specification = createSpecification(criteria);
        return boatRepository.count(specification);
    }

    /**
     * Function to convert {@link BoatCriteria} to a {@link Specification}
     *
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Boat> createSpecification(BoatCriteria criteria) {
        Specification<Boat> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Boat_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Boat_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Boat_.description));
            }
        }
        return specification;
    }
}
