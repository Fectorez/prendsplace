package com.esgi.prendsplace2.service.mapper;

import com.esgi.prendsplace2.domain.*;
import com.esgi.prendsplace2.service.dto.EventDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Event} and its DTO {@link EventDTO}.
 */
@Mapper(componentModel = "spring", uses = {InterestMapper.class, UserMapper.class})
public interface EventMapper extends EntityMapper<EventDTO, Event> {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.login", target = "userLogin")
    EventDTO toDto(Event event);

    @Mapping(source = "userId", target = "user")
    Event toEntity(EventDTO eventDTO);

    default Event fromId(Long id) {
        if (id == null) {
            return null;
        }
        Event event = new Event();
        event.setId(id);
        return event;
    }
}
