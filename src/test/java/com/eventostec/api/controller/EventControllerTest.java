package com.eventostec.api.controller;

import com.eventostec.api.domain.event.Event;
import com.eventostec.api.domain.event.EventResponseDTO;
import com.eventostec.api.domain.service.EventService;
import com.eventostec.api.exception.BadRequestException;
import com.eventostec.api.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@Import(GlobalExceptionHandler.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Test
    void createEvent_returnsOkAndBody() throws Exception {
        UUID id = UUID.randomUUID();
        Event created = new Event();
        created.setId(id);
        created.setTitle("Evento teste");
        created.setDescription("Desc");
        created.setEventUrl("https://example.com");
        created.setImgUrl("https://bucket/key");
        created.setRemote(false);
        created.setDate(new Date(1893456000000L));

        when(eventService.createEvent(any())).thenReturn(created);

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "f.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[]{0x01}
        );

        mockMvc.perform(
                        multipart("/api/event")
                                .file(image)
                                .param("title", "Evento teste")
                                .param("description", "Desc")
                                .param("date", "1893456000000")
                                .param("city", "Sao Paulo")
                                .param("state", "SP")
                                .param("remote", "false")
                                .param("eventUrl", "https://example.com")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Evento teste"))
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void getEvents_returnsOkAndList() throws Exception {
        UUID id = UUID.randomUUID();
        EventResponseDTO dto = new EventResponseDTO(
                id,
                "T",
                "D",
                new Date(),
                "Sao Paulo",
                "SP",
                false,
                "https://e.com",
                "https://img"
        );
        when(eventService.getUpcomingEvents(0, 10)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/event").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("T"))
                .andExpect(jsonPath("$[0].id").value(id.toString()));
    }

    @Test
    void createEvent_whenServiceThrowsBadRequest_returns400() throws Exception {
        when(eventService.createEvent(any())).thenThrow(new BadRequestException("title is required"));

        mockMvc.perform(
                        multipart("/api/event")
                                .param("title", "x")
                                .param("date", "1893456000000")
                                .param("city", "Sao Paulo")
                                .param("state", "SP")
                                .param("remote", "false")
                                .param("eventUrl", "https://example.com")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("title is required"));
    }
}
