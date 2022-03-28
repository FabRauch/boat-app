package com.openwt.boatapp.service.mapper;

import com.openwt.boatapp.domain.Boat;
import com.openwt.boatapp.service.dto.BoatDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Boat} and its DTO {@link BoatDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface BoatMapper extends EntityMapper<BoatDTO, Boat> {}
