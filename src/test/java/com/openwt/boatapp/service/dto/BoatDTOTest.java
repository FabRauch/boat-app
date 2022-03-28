package com.openwt.boatapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.openwt.boatapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BoatDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BoatDTO.class);
        BoatDTO boatDTO1 = new BoatDTO();
        boatDTO1.setId(1L);
        BoatDTO boatDTO2 = new BoatDTO();
        assertThat(boatDTO1).isNotEqualTo(boatDTO2);
        boatDTO2.setId(boatDTO1.getId());
        assertThat(boatDTO1).isEqualTo(boatDTO2);
        boatDTO2.setId(2L);
        assertThat(boatDTO1).isNotEqualTo(boatDTO2);
        boatDTO1.setId(null);
        assertThat(boatDTO1).isNotEqualTo(boatDTO2);
    }
}
