package example.performance.adapter.in;

import org.example.performance.adapter.in.VenueController;
import org.example.performance.adapter.in.request.HallRequest;
import org.example.performance.adapter.in.request.VenueRequest;
import org.example.performance.application.service.HallService;
import org.example.performance.application.service.VenueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(VenueController.class)
class VenueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Service는 Mock으로 대체
    @MockBean
    private VenueService venueService;

    @MockBean
    private HallService hallService;

}
