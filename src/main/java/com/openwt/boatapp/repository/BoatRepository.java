package com.openwt.boatapp.repository;

import com.openwt.boatapp.domain.Boat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Boat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BoatRepository extends JpaRepository<Boat, Long>, JpaSpecificationExecutor<Boat> {}
