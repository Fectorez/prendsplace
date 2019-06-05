package com.esgi.prendsplace2.web.rest;

import com.esgi.prendsplace2.service.InterestService;
import com.esgi.prendsplace2.web.rest.errors.BadRequestAlertException;
import com.esgi.prendsplace2.service.dto.InterestDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.esgi.prendsplace2.domain.Interest}.
 */
@RestController
@RequestMapping("/api")
public class InterestResource {

    private final Logger log = LoggerFactory.getLogger(InterestResource.class);

    private static final String ENTITY_NAME = "interest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InterestService interestService;

    public InterestResource(InterestService interestService) {
        this.interestService = interestService;
    }

    /**
     * {@code POST  /interests} : Create a new interest.
     *
     * @param interestDTO the interestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new interestDTO, or with status {@code 400 (Bad Request)} if the interest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/interests")
    public ResponseEntity<InterestDTO> createInterest(@Valid @RequestBody InterestDTO interestDTO) throws URISyntaxException {
        log.debug("REST request to save Interest : {}", interestDTO);
        if (interestDTO.getId() != null) {
            throw new BadRequestAlertException("A new interest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        InterestDTO result = interestService.save(interestDTO);
        return ResponseEntity.created(new URI("/api/interests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /interests} : Updates an existing interest.
     *
     * @param interestDTO the interestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated interestDTO,
     * or with status {@code 400 (Bad Request)} if the interestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the interestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/interests")
    public ResponseEntity<InterestDTO> updateInterest(@Valid @RequestBody InterestDTO interestDTO) throws URISyntaxException {
        log.debug("REST request to update Interest : {}", interestDTO);
        if (interestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        InterestDTO result = interestService.save(interestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, interestDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /interests} : get all the interests.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of interests in body.
     */
    @GetMapping("/interests")
    public List<InterestDTO> getAllInterests(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Interests");
        return interestService.findAll();
    }

    /**
     * {@code GET  /interests/:id} : get the "id" interest.
     *
     * @param id the id of the interestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the interestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/interests/{id}")
    public ResponseEntity<InterestDTO> getInterest(@PathVariable Long id) {
        log.debug("REST request to get Interest : {}", id);
        Optional<InterestDTO> interestDTO = interestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(interestDTO);
    }

    /**
     * {@code DELETE  /interests/:id} : delete the "id" interest.
     *
     * @param id the id of the interestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/interests/{id}")
    public ResponseEntity<Void> deleteInterest(@PathVariable Long id) {
        log.debug("REST request to delete Interest : {}", id);
        interestService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
