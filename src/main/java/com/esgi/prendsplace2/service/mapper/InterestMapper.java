package com.esgi.prendsplace2.service.mapper;

import com.esgi.prendsplace2.domain.*;
import com.esgi.prendsplace2.service.dto.InterestDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Interest} and its DTO {@link InterestDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface InterestMapper extends EntityMapper<InterestDTO, Interest> {


    @Mapping(target = "events", ignore = true)
    Interest toEntity(InterestDTO interestDTO);

    default Interest fromId(Long id) {
        if (id == null) {
            return null;
        }
        Interest interest = new Interest();
        interest.setId(id);
        return interest;
    }
}
