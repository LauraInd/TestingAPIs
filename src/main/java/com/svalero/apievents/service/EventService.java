package com.svalero.apievents.service;

import com.svalero.apievents.domain.Event;
import com.svalero.apievents.domain.EventCategory;
import com.svalero.apievents.domain.dto.EventOutDto;
import com.svalero.apievents.domain.dto.EventRegistrationDto;
import com.svalero.apievents.exception.EventCategoryNotFoundException;
import com.svalero.apievents.exception.EventNotFoundException;
import com.svalero.apievents.repository.EventCategoryRepository;
import com.svalero.apievents.repository.EventRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class EventService {


    private EventRepository eventRepository;

    private EventCategoryRepository eventCategoryRepository;

    private ModelMapper modelMapper;

   // private final EventRepository eventRepository;
    @Autowired
    public EventService(EventRepository eventRepository, EventCategoryRepository eventCategoryRepository, ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.eventCategoryRepository = eventCategoryRepository;
        this.modelMapper = modelMapper;
    }

    // Obtener todos los eventos
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // Buscar eventos por nombre
    public List<Event> getEventsByName(String name) {
        return eventRepository.findByEventNameContaining(name);
    }

    // Buscar eventos con capacidad máxima
    public List<Event> getEventsByCapacity(int capacity) {
        return eventRepository.findByCapacityLessThanEqual(capacity);
    }

    // Buscar eventos por fecha
    public List<Event> getEventsByDate(LocalDate date) {
        return eventRepository.findByEventDate(date);
    }

    // Buscar eventos entre dos fechas
    public List<Event> getEventsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return eventRepository.findByEventDateBetween(startDate, endDate);
    }

    // Buscar eventos por ubicación
    public List<Event> getEventsByUbication(String ubication) {
        return eventRepository.findByUbicationContaining(ubication);
    }

    // Guardar un nuevo evento
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public EventOutDto add(EventRegistrationDto eventInDto) throws EventCategoryNotFoundException {

        EventCategory eventCategory = eventCategoryRepository.findById(eventInDto.getCategoryId())
                .orElseThrow(() -> new EventCategoryNotFoundException("Category" + eventInDto.getCategoryId() + " not found"));


        Event event = modelMapper.map(eventInDto, Event.class);
        event.setEventDate(LocalDate.now()); // Si quieres que la fecha siempre sea "hoy"
        event.setCategory(eventCategory); // Asignar la categoría al evento

        Event newEvent = eventRepository.save(event);

        return modelMapper.map(newEvent, EventOutDto.class);
    }


    // Obtener un evento por ID
    public Event getEventById(Long id) throws EventNotFoundException {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));
    }

    // Actualizar un evento por ID
    public Event updateEvent(Long id, Event eventDetails) throws EventNotFoundException {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));

        // Actualizar los campos del evento existente con los nuevos valores
        existingEvent.setEventName(eventDetails.getEventName());
        existingEvent.setDescription(eventDetails.getDescription());
        existingEvent.setEventDate(eventDetails.getEventDate());
        existingEvent.setCapacity(eventDetails.getCapacity());
        existingEvent.setUbication(eventDetails.getUbication());
        existingEvent.setCategory(eventDetails.getCategory());

        return eventRepository.save(existingEvent);
    }

    public Event updateEventPartial(Long id, Map<String, Object> updates) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Event.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, event, value);
            }
        });

        return eventRepository.save(event);
    }

    // Eliminar un evento por ID
    public void deleteEvent(Long id) throws EventNotFoundException {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }
}
