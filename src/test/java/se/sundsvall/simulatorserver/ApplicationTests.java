package se.sundsvall.simulatorserver;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTests {

	private final ObjectMapper mapper = JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).build();

	@Autowired
	private MockMvc mockMvc;

	@ParameterizedTest
	@EnumSource(value = HttpStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {
		"PAYLOAD_TOO_LARGE", "UNPROCESSABLE_ENTITY"
	})
	void testGetForAllStatuses(HttpStatus status) throws Exception {

		String detail = "Some example detail.";
		String type = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.1";
		String instance = "test.example";

		mockMvc.perform(
			get("/simulations/response")
				.queryParam("status", String.valueOf(status.value()))
				.queryParam("detail", detail)
				.queryParam("type", type)
				.queryParam("instance", instance))
			.andExpect(status().is(status.value()))
			.andExpect(jsonPath("$.title").value(status.getReasonPhrase()))
			.andExpect(jsonPath("$.status").value(status.value()))
			.andExpect(jsonPath("$.detail").value(detail))
			.andExpect(jsonPath("$.type").value(type))
			.andExpect(jsonPath("$.instance").value(instance));
	}

	@ParameterizedTest
	@EnumSource(value = HttpStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {
		"PAYLOAD_TOO_LARGE", "UNPROCESSABLE_ENTITY"
	})
	void testPostForAllStatuses(HttpStatus status) throws Exception {
		TestClass testClassRequest = new TestClass();
		testClassRequest.setField1("Test string");
		testClassRequest.setField2(123);
		testClassRequest.setField3(true);

		String responseBody = mockMvc.perform(
			post("/simulations/response")
				.queryParam("status", String.valueOf(status.value()))
				.content(mapper.writeValueAsString(testClassRequest)).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is(status.value()))
			.andReturn().getResponse().getContentAsString();

		TestClass testClassResponse = JsonMapper.builder().build().readValue(responseBody, TestClass.class);

		assertThat(testClassRequest).isEqualTo(testClassResponse);
	}

	@Test
	void testGetDelay() throws Exception {

		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

		mockMvc.perform(
			get("/simulations/response")
				.queryParam("delay", "1000")
				.queryParam("status", String.valueOf(status.value())))
			.andExpect(status().is(status.value()))
			.andExpect(jsonPath("$.title").value(status.getReasonPhrase()))
			.andExpect(jsonPath("$.status").value(status.value()));
	}

	@Test
	void testPostDelay() throws Exception {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		TestClass testClassRequest = new TestClass();
		testClassRequest.setField1("Test string");
		testClassRequest.setField2(123);
		testClassRequest.setField3(true);

		String responseBody = mockMvc.perform(
			post("/simulations/response")
				.queryParam("delay", "2000")
				.queryParam("status", String.valueOf(status.value()))
				.content(mapper.writeValueAsString(testClassRequest)).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is(status.value()))
			.andReturn().getResponse().getContentAsString();

		TestClass testClassResponse = JsonMapper.builder().build().readValue(responseBody, TestClass.class);

		assertThat(testClassRequest).isEqualTo(testClassResponse);
	}

	@Test
	void testPostIllegalArgumentException() throws Exception {

		TestClass testClassRequest = new TestClass();
		testClassRequest.setField1("Test string");
		testClassRequest.setField2(123);
		testClassRequest.setField3(true);

		mockMvc.perform(post("/simulations/response")
			.queryParam("status", "12345")
			.content(mapper.writeValueAsString(testClassRequest)).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
			.andExpect(jsonPath("$.detail").value(Matchers.containsString("Failed to convert 'status' with value: '12345'")));
	}

	@Test
	void testSort() throws Exception {

		HttpStatus status = HttpStatus.OK;

		mockMvc.perform(
			get("/simulations/response")
				.queryParam("sortSize", "2")
				.queryParam("status", String.valueOf(status.value())))
			.andExpect(status().is(status.value()));

	}
}
