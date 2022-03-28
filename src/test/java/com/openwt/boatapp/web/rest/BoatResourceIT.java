package com.openwt.boatapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openwt.boatapp.IntegrationTest;
import com.openwt.boatapp.domain.Boat;
import com.openwt.boatapp.repository.BoatRepository;
import com.openwt.boatapp.service.dto.BoatDTO;
import com.openwt.boatapp.service.mapper.BoatMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link BoatResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BoatResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_PIC = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PIC = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PIC_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PIC_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/boats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BoatRepository boatRepository;

    @Autowired
    private BoatMapper boatMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBoatMockMvc;

    private Boat boat;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Boat createEntity(EntityManager em) {
        Boat boat = new Boat()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .pic(DEFAULT_PIC)
            .picContentType(DEFAULT_PIC_CONTENT_TYPE);
        return boat;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Boat createUpdatedEntity(EntityManager em) {
        Boat boat = new Boat()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .pic(UPDATED_PIC)
            .picContentType(UPDATED_PIC_CONTENT_TYPE);
        return boat;
    }

    @BeforeEach
    public void initTest() {
        boat = createEntity(em);
    }

    @Test
    @Transactional
    void createBoat() throws Exception {
        int databaseSizeBeforeCreate = boatRepository.findAll().size();
        // Create the Boat
        BoatDTO boatDTO = boatMapper.toDto(boat);
        restBoatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(boatDTO)))
            .andExpect(status().isCreated());

        // Validate the Boat in the database
        List<Boat> boatList = boatRepository.findAll();
        assertThat(boatList).hasSize(databaseSizeBeforeCreate + 1);
        Boat testBoat = boatList.get(boatList.size() - 1);
        assertThat(testBoat.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBoat.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBoat.getPic()).isEqualTo(DEFAULT_PIC);
        assertThat(testBoat.getPicContentType()).isEqualTo(DEFAULT_PIC_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void createBoatWithExistingId() throws Exception {
        // Create the Boat with an existing ID
        boat.setId(1L);
        BoatDTO boatDTO = boatMapper.toDto(boat);

        int databaseSizeBeforeCreate = boatRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBoatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(boatDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Boat in the database
        List<Boat> boatList = boatRepository.findAll();
        assertThat(boatList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = boatRepository.findAll().size();
        // set the field null
        boat.setName(null);

        // Create the Boat, which fails.
        BoatDTO boatDTO = boatMapper.toDto(boat);

        restBoatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(boatDTO)))
            .andExpect(status().isBadRequest());

        List<Boat> boatList = boatRepository.findAll();
        assertThat(boatList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = boatRepository.findAll().size();
        // set the field null
        boat.setDescription(null);

        // Create the Boat, which fails.
        BoatDTO boatDTO = boatMapper.toDto(boat);

        restBoatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(boatDTO)))
            .andExpect(status().isBadRequest());

        List<Boat> boatList = boatRepository.findAll();
        assertThat(boatList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBoats() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        // Get all the boatList
        restBoatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(boat.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].picContentType").value(hasItem(DEFAULT_PIC_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].pic").value(hasItem(Base64Utils.encodeToString(DEFAULT_PIC))));
    }

    @Test
    @Transactional
    void getBoat() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        // Get the boat
        restBoatMockMvc
            .perform(get(ENTITY_API_URL_ID, boat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(boat.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.picContentType").value(DEFAULT_PIC_CONTENT_TYPE))
            .andExpect(jsonPath("$.pic").value(Base64Utils.encodeToString(DEFAULT_PIC)));
    }

    @Test
    @Transactional
    void getBoatsByIdFiltering() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        Long id = boat.getId();

        defaultBoatShouldBeFound("id.equals=" + id);
        defaultBoatShouldNotBeFound("id.notEquals=" + id);

        defaultBoatShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultBoatShouldNotBeFound("id.greaterThan=" + id);

        defaultBoatShouldBeFound("id.lessThanOrEqual=" + id);
        defaultBoatShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBoatsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        // Get all the boatList where name equals to DEFAULT_NAME
        defaultBoatShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the boatList where name equals to UPDATED_NAME
        defaultBoatShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBoatsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        // Get all the boatList where name not equals to DEFAULT_NAME
        defaultBoatShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the boatList where name not equals to UPDATED_NAME
        defaultBoatShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBoatsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        // Get all the boatList where name in DEFAULT_NAME or UPDATED_NAME
        defaultBoatShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the boatList where name equals to UPDATED_NAME
        defaultBoatShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBoatsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        // Get all the boatList where name is not null
        defaultBoatShouldBeFound("name.specified=true");

        // Get all the boatList where name is null
        defaultBoatShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllBoatsByNameContainsSomething() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        // Get all the boatList where name contains DEFAULT_NAME
        defaultBoatShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the boatList where name contains UPDATED_NAME
        defaultBoatShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBoatsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        // Get all the boatList where name does not contain DEFAULT_NAME
        defaultBoatShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the boatList where name does not contain UPDATED_NAME
        defaultBoatShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllBoatsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        // Get all the boatList where description equals to DEFAULT_DESCRIPTION
        defaultBoatShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the boatList where description equals to UPDATED_DESCRIPTION
        defaultBoatShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBoatsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        // Get all the boatList where description not equals to DEFAULT_DESCRIPTION
        defaultBoatShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the boatList where description not equals to UPDATED_DESCRIPTION
        defaultBoatShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBoatsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        // Get all the boatList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultBoatShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the boatList where description equals to UPDATED_DESCRIPTION
        defaultBoatShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBoatsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        // Get all the boatList where description is not null
        defaultBoatShouldBeFound("description.specified=true");

        // Get all the boatList where description is null
        defaultBoatShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllBoatsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        // Get all the boatList where description contains DEFAULT_DESCRIPTION
        defaultBoatShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the boatList where description contains UPDATED_DESCRIPTION
        defaultBoatShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllBoatsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        // Get all the boatList where description does not contain DEFAULT_DESCRIPTION
        defaultBoatShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the boatList where description does not contain UPDATED_DESCRIPTION
        defaultBoatShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBoatShouldBeFound(String filter) throws Exception {
        restBoatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(boat.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].picContentType").value(hasItem(DEFAULT_PIC_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].pic").value(hasItem(Base64Utils.encodeToString(DEFAULT_PIC))));

        // Check, that the count call also returns 1
        restBoatMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBoatShouldNotBeFound(String filter) throws Exception {
        restBoatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBoatMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBoat() throws Exception {
        // Get the boat
        restBoatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBoat() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        int databaseSizeBeforeUpdate = boatRepository.findAll().size();

        // Update the boat
        Boat updatedBoat = boatRepository.findById(boat.getId()).get();
        // Disconnect from session so that the updates on updatedBoat are not directly saved in db
        em.detach(updatedBoat);
        updatedBoat.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).pic(UPDATED_PIC).picContentType(UPDATED_PIC_CONTENT_TYPE);
        BoatDTO boatDTO = boatMapper.toDto(updatedBoat);

        restBoatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, boatDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(boatDTO))
            )
            .andExpect(status().isOk());

        // Validate the Boat in the database
        List<Boat> boatList = boatRepository.findAll();
        assertThat(boatList).hasSize(databaseSizeBeforeUpdate);
        Boat testBoat = boatList.get(boatList.size() - 1);
        assertThat(testBoat.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBoat.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBoat.getPic()).isEqualTo(UPDATED_PIC);
        assertThat(testBoat.getPicContentType()).isEqualTo(UPDATED_PIC_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingBoat() throws Exception {
        int databaseSizeBeforeUpdate = boatRepository.findAll().size();
        boat.setId(count.incrementAndGet());

        // Create the Boat
        BoatDTO boatDTO = boatMapper.toDto(boat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, boatDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(boatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boat in the database
        List<Boat> boatList = boatRepository.findAll();
        assertThat(boatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBoat() throws Exception {
        int databaseSizeBeforeUpdate = boatRepository.findAll().size();
        boat.setId(count.incrementAndGet());

        // Create the Boat
        BoatDTO boatDTO = boatMapper.toDto(boat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(boatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boat in the database
        List<Boat> boatList = boatRepository.findAll();
        assertThat(boatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBoat() throws Exception {
        int databaseSizeBeforeUpdate = boatRepository.findAll().size();
        boat.setId(count.incrementAndGet());

        // Create the Boat
        BoatDTO boatDTO = boatMapper.toDto(boat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoatMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(boatDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Boat in the database
        List<Boat> boatList = boatRepository.findAll();
        assertThat(boatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBoatWithPatch() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        int databaseSizeBeforeUpdate = boatRepository.findAll().size();

        // Update the boat using partial update
        Boat partialUpdatedBoat = new Boat();
        partialUpdatedBoat.setId(boat.getId());

        restBoatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBoat.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBoat))
            )
            .andExpect(status().isOk());

        // Validate the Boat in the database
        List<Boat> boatList = boatRepository.findAll();
        assertThat(boatList).hasSize(databaseSizeBeforeUpdate);
        Boat testBoat = boatList.get(boatList.size() - 1);
        assertThat(testBoat.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBoat.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBoat.getPic()).isEqualTo(DEFAULT_PIC);
        assertThat(testBoat.getPicContentType()).isEqualTo(DEFAULT_PIC_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateBoatWithPatch() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        int databaseSizeBeforeUpdate = boatRepository.findAll().size();

        // Update the boat using partial update
        Boat partialUpdatedBoat = new Boat();
        partialUpdatedBoat.setId(boat.getId());

        partialUpdatedBoat.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).pic(UPDATED_PIC).picContentType(UPDATED_PIC_CONTENT_TYPE);

        restBoatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBoat.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBoat))
            )
            .andExpect(status().isOk());

        // Validate the Boat in the database
        List<Boat> boatList = boatRepository.findAll();
        assertThat(boatList).hasSize(databaseSizeBeforeUpdate);
        Boat testBoat = boatList.get(boatList.size() - 1);
        assertThat(testBoat.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBoat.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBoat.getPic()).isEqualTo(UPDATED_PIC);
        assertThat(testBoat.getPicContentType()).isEqualTo(UPDATED_PIC_CONTENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingBoat() throws Exception {
        int databaseSizeBeforeUpdate = boatRepository.findAll().size();
        boat.setId(count.incrementAndGet());

        // Create the Boat
        BoatDTO boatDTO = boatMapper.toDto(boat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, boatDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(boatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boat in the database
        List<Boat> boatList = boatRepository.findAll();
        assertThat(boatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBoat() throws Exception {
        int databaseSizeBeforeUpdate = boatRepository.findAll().size();
        boat.setId(count.incrementAndGet());

        // Create the Boat
        BoatDTO boatDTO = boatMapper.toDto(boat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(boatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boat in the database
        List<Boat> boatList = boatRepository.findAll();
        assertThat(boatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBoat() throws Exception {
        int databaseSizeBeforeUpdate = boatRepository.findAll().size();
        boat.setId(count.incrementAndGet());

        // Create the Boat
        BoatDTO boatDTO = boatMapper.toDto(boat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoatMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(boatDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Boat in the database
        List<Boat> boatList = boatRepository.findAll();
        assertThat(boatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBoat() throws Exception {
        // Initialize the database
        boatRepository.saveAndFlush(boat);

        int databaseSizeBeforeDelete = boatRepository.findAll().size();

        // Delete the boat
        restBoatMockMvc
            .perform(delete(ENTITY_API_URL_ID, boat.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Boat> boatList = boatRepository.findAll();
        assertThat(boatList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
