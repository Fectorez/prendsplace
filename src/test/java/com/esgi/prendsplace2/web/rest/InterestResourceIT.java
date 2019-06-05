package com.esgi.prendsplace2.web.rest;

import com.esgi.prendsplace2.Prendsplace2App;
import com.esgi.prendsplace2.domain.Interest;
import com.esgi.prendsplace2.repository.InterestRepository;
import com.esgi.prendsplace2.service.InterestService;
import com.esgi.prendsplace2.service.dto.InterestDTO;
import com.esgi.prendsplace2.service.mapper.InterestMapper;
import com.esgi.prendsplace2.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.esgi.prendsplace2.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link InterestResource} REST controller.
 */
@SpringBootTest(classes = Prendsplace2App.class)
public class InterestResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private InterestRepository interestRepository;

    @Mock
    private InterestRepository interestRepositoryMock;

    @Autowired
    private InterestMapper interestMapper;

    @Mock
    private InterestService interestServiceMock;

    @Autowired
    private InterestService interestService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restInterestMockMvc;

    private Interest interest;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final InterestResource interestResource = new InterestResource(interestService);
        this.restInterestMockMvc = MockMvcBuilders.standaloneSetup(interestResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Interest createEntity(EntityManager em) {
        Interest interest = new Interest()
            .name(DEFAULT_NAME);
        return interest;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Interest createUpdatedEntity(EntityManager em) {
        Interest interest = new Interest()
            .name(UPDATED_NAME);
        return interest;
    }

    @BeforeEach
    public void initTest() {
        interest = createEntity(em);
    }

    @Test
    @Transactional
    public void createInterest() throws Exception {
        int databaseSizeBeforeCreate = interestRepository.findAll().size();

        // Create the Interest
        InterestDTO interestDTO = interestMapper.toDto(interest);
        restInterestMockMvc.perform(post("/api/interests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(interestDTO)))
            .andExpect(status().isCreated());

        // Validate the Interest in the database
        List<Interest> interestList = interestRepository.findAll();
        assertThat(interestList).hasSize(databaseSizeBeforeCreate + 1);
        Interest testInterest = interestList.get(interestList.size() - 1);
        assertThat(testInterest.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createInterestWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = interestRepository.findAll().size();

        // Create the Interest with an existing ID
        interest.setId(1L);
        InterestDTO interestDTO = interestMapper.toDto(interest);

        // An entity with an existing ID cannot be created, so this API call must fail
        restInterestMockMvc.perform(post("/api/interests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(interestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Interest in the database
        List<Interest> interestList = interestRepository.findAll();
        assertThat(interestList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = interestRepository.findAll().size();
        // set the field null
        interest.setName(null);

        // Create the Interest, which fails.
        InterestDTO interestDTO = interestMapper.toDto(interest);

        restInterestMockMvc.perform(post("/api/interests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(interestDTO)))
            .andExpect(status().isBadRequest());

        List<Interest> interestList = interestRepository.findAll();
        assertThat(interestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllInterests() throws Exception {
        // Initialize the database
        interestRepository.saveAndFlush(interest);

        // Get all the interestList
        restInterestMockMvc.perform(get("/api/interests?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(interest.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllInterestsWithEagerRelationshipsIsEnabled() throws Exception {
        InterestResource interestResource = new InterestResource(interestServiceMock);
        when(interestServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restInterestMockMvc = MockMvcBuilders.standaloneSetup(interestResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restInterestMockMvc.perform(get("/api/interests?eagerload=true"))
        .andExpect(status().isOk());

        verify(interestServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllInterestsWithEagerRelationshipsIsNotEnabled() throws Exception {
        InterestResource interestResource = new InterestResource(interestServiceMock);
            when(interestServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restInterestMockMvc = MockMvcBuilders.standaloneSetup(interestResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restInterestMockMvc.perform(get("/api/interests?eagerload=true"))
        .andExpect(status().isOk());

            verify(interestServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getInterest() throws Exception {
        // Initialize the database
        interestRepository.saveAndFlush(interest);

        // Get the interest
        restInterestMockMvc.perform(get("/api/interests/{id}", interest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(interest.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingInterest() throws Exception {
        // Get the interest
        restInterestMockMvc.perform(get("/api/interests/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateInterest() throws Exception {
        // Initialize the database
        interestRepository.saveAndFlush(interest);

        int databaseSizeBeforeUpdate = interestRepository.findAll().size();

        // Update the interest
        Interest updatedInterest = interestRepository.findById(interest.getId()).get();
        // Disconnect from session so that the updates on updatedInterest are not directly saved in db
        em.detach(updatedInterest);
        updatedInterest
            .name(UPDATED_NAME);
        InterestDTO interestDTO = interestMapper.toDto(updatedInterest);

        restInterestMockMvc.perform(put("/api/interests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(interestDTO)))
            .andExpect(status().isOk());

        // Validate the Interest in the database
        List<Interest> interestList = interestRepository.findAll();
        assertThat(interestList).hasSize(databaseSizeBeforeUpdate);
        Interest testInterest = interestList.get(interestList.size() - 1);
        assertThat(testInterest.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingInterest() throws Exception {
        int databaseSizeBeforeUpdate = interestRepository.findAll().size();

        // Create the Interest
        InterestDTO interestDTO = interestMapper.toDto(interest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInterestMockMvc.perform(put("/api/interests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(interestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Interest in the database
        List<Interest> interestList = interestRepository.findAll();
        assertThat(interestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteInterest() throws Exception {
        // Initialize the database
        interestRepository.saveAndFlush(interest);

        int databaseSizeBeforeDelete = interestRepository.findAll().size();

        // Delete the interest
        restInterestMockMvc.perform(delete("/api/interests/{id}", interest.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<Interest> interestList = interestRepository.findAll();
        assertThat(interestList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Interest.class);
        Interest interest1 = new Interest();
        interest1.setId(1L);
        Interest interest2 = new Interest();
        interest2.setId(interest1.getId());
        assertThat(interest1).isEqualTo(interest2);
        interest2.setId(2L);
        assertThat(interest1).isNotEqualTo(interest2);
        interest1.setId(null);
        assertThat(interest1).isNotEqualTo(interest2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InterestDTO.class);
        InterestDTO interestDTO1 = new InterestDTO();
        interestDTO1.setId(1L);
        InterestDTO interestDTO2 = new InterestDTO();
        assertThat(interestDTO1).isNotEqualTo(interestDTO2);
        interestDTO2.setId(interestDTO1.getId());
        assertThat(interestDTO1).isEqualTo(interestDTO2);
        interestDTO2.setId(2L);
        assertThat(interestDTO1).isNotEqualTo(interestDTO2);
        interestDTO1.setId(null);
        assertThat(interestDTO1).isNotEqualTo(interestDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(interestMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(interestMapper.fromId(null)).isNull();
    }
}
