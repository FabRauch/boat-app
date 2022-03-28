package com.openwt.boatapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.openwt.boatapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BoatTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Boat.class);
        Boat boat1 = new Boat();
        boat1.setId(1L);
        Boat boat2 = new Boat();
        boat2.setId(boat1.getId());
        assertThat(boat1).isEqualTo(boat2);
        boat2.setId(2L);
        assertThat(boat1).isNotEqualTo(boat2);
        boat1.setId(null);
        assertThat(boat1).isNotEqualTo(boat2);
    }
}
