package guru.springframework.msscbeerservice.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.model.BeerStyleEnum;
import guru.springframework.msscbeerservice.bootstrap.BeerLoader;
import guru.springframework.msscbeerservice.services.BeerService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

@WebMvcTest(BeerController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
class BeerControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  BeerService beerService;

  @Test
  void getBeerById() throws Exception {
    given(beerService.getById(any(), anyBoolean())).willReturn(getValidBeerDto());
    mockMvc.perform(get("/api/v1/beer/{beerId}", UUID.randomUUID().toString()).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("v1/beer-get", pathParameters(
                parameterWithName("beerId")
                    .description("UUID if desired beer to get .")
            ), responseFields(
                fieldWithPath("id").type("UUID").description("id of Beer"),
                fieldWithPath("version").type("String").description("version of api"),
                fieldWithPath("lastModifiedDate").type("OffsetDateTime").description("Beer last update"),
                fieldWithPath("createdDate").type("OffsetDateTime").description("Beer creation date"),
                fieldWithPath("beerName").type("String").description("Name of Beer"),
                fieldWithPath("beerStyle").type("String").description("Style of Beer"),
                fieldWithPath("upc").type("String").description("upc of selected Beer"),
                fieldWithPath("price").type("BigDecimal").description("price of selected Beer"),
                fieldWithPath("quantityOnHand").type("Integer").description("Available quantity"))
        ));
  }

  @Test
  void saveNewBeer() throws Exception {

    BeerDto beerDto = getValidBeerDto();
    String beerDtoJson = objectMapper.writeValueAsString(beerDto);

    given(beerService.saveNewBeer(any())).willReturn(getValidBeerDto());
    ConstrainedFields fields = new ConstrainedFields(BeerDto.class);
    mockMvc.perform(post("/api/v1/beer/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(beerDtoJson))
        .andExpect(status().isCreated()).andDo(
            document("v1/beer-create", requestFields(
                fields.withPath("id").type("UUID").description("id of Beer").ignored(),
                fields.withPath("version").type("String").description("version of api").ignored(),
                fields.withPath("createdDate").type("OffsetDateTime").description("Beer creation date").ignored(),
                fields.withPath("lastModifiedDate").type("OffsetDateTime").description("Beer last update").ignored(),
                fields.withPath("beerName").type("String").description("Name of Beer"),
                fields.withPath("beerStyle").type("String").description("Style of Beer"),
                fields.withPath("upc").type("String").description("upc of selected Beer"),
                fields.withPath("price").type("BigDecimal").description("price of selected Beer"),
                fields.withPath("quantityOnHand").type("Integer").description("Available quantity")
            )));
  }

  @Test
  void updateBeerById() throws Exception {
    given(beerService.updateBeer(any(), any())).willReturn(getValidBeerDto());

    BeerDto beerDto = getValidBeerDto();
    String beerDtoJson = objectMapper.writeValueAsString(beerDto);

    ConstrainedFields fields = new ConstrainedFields(BeerDto.class);

    mockMvc.perform(put("/api/v1/beer/{beerId}", UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(beerDtoJson))

        .andExpect(status().isNoContent()).andDo(
            document("v1/beer-update", pathParameters(
                parameterWithName("beerId")
                    .description("UUID if desired beer to get .")), requestFields(
                fields.withPath("id").type("UUID").description("id of Beer").ignored(),
                fields.withPath("version").type("String").description("version of api").ignored(),
                fields.withPath("createdDate").type("OffsetDateTime").description("Beer creation date").ignored(),
                fields.withPath("lastModifiedDate").type("OffsetDateTime").description("Beer last update").ignored(),
                fields.withPath("beerName").type("String").description("Name of Beer"),
                fields.withPath("beerStyle").type("String").description("Style of Beer"),
                fields.withPath("upc").type("String").description("upc of selected Beer"),
                fields.withPath("price").type("BigDecimal").description("price of selected Beer"),
                fields.withPath("quantityOnHand").type("Integer").description("Available quantity")
            )));
  }

  BeerDto getValidBeerDto() {
    return BeerDto.builder()
        .beerName("My Beer")
        .beerStyle(BeerStyleEnum.ALE)
        .price(new BigDecimal("2.99"))
        .upc(BeerLoader.BEER_1_UPC)
        .build();
  }

  private static class ConstrainedFields {

    private final ConstraintDescriptions constraintDescriptions;

    ConstrainedFields(Class<?> input) {
      this.constraintDescriptions = new ConstraintDescriptions(input);
    }

    private FieldDescriptor withPath(String path) {
      return fieldWithPath(path).attributes(key("constraints").value(StringUtils
          .collectionToDelimitedString(this.constraintDescriptions
              .descriptionsForProperty(path), ". ")));
    }
  }

}