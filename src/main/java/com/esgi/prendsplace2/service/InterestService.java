package com.esgi.prendsplace2.service;

import com.esgi.prendsplace2.domain.Interest;
import com.esgi.prendsplace2.repository.InterestRepository;
import com.esgi.prendsplace2.service.dto.InterestDTO;
import com.esgi.prendsplace2.service.mapper.InterestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Interest}.
 */
@Service
@Transactional
public class InterestService {

    private final Logger log = LoggerFactory.getLogger(InterestService.class);

    private final InterestRepository interestRepository;

    private final InterestMapper interestMapper;

    public InterestService(InterestRepository interestRepository, InterestMapper interestMapper) {
        this.interestRepository = interestRepository;
        this.interestMapper = interestMapper;
    }

    /**
     * Save a interest.
     *
     * @param interestDTO the entity to save.
     * @return the persisted entity.
     */
    public InterestDTO save(InterestDTO interestDTO) {
        log.debug("Request to save Interest : {}", interestDTO);
        Interest interest = interestMapper.toEntity(interestDTO);
        interest = interestRepository.save(interest);
        return interestMapper.toDto(interest);
    }

    /**
     * Get all the interests.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<InterestDTO> findAll() {
        log.debug("Request to get all Interests");
        return interestRepository.findAllWithEagerRelationships().stream()
            .map(interestMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the interests with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<InterestDTO> findAllWithEagerRelationships(Pageable pageable) {
        return interestRepository.findAllWithEagerRelationships(pageable).map(interestMapper::toDto);
    }
    

    /**
     * Get one interest by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InterestDTO> findOne(Long id) {
        log.debug("Request to get Interest : {}", id);
        return interestRepository.findOneWithEagerRelationships(id)
            .map(interestMapper::toDto);
    }

    /**
     * Delete the interest by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Interest : {}", id);
        interestRepository.deleteById(id);
    }
}
