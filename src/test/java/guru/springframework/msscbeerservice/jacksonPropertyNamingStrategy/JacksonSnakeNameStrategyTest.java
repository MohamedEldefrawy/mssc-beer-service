package guru.springframework.msscbeerservice.jacksonPropertyNamingStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.model.BeerDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ActiveProfiles;

@JsonTest
@ActiveProfiles("snake")
@Slf4j
class JacksonSnakeNameStrategyTest extends BaseTest {
  @Autowired
  ObjectMapper mapper;

  @Test
  @SneakyThrows
  void testSerializeBeerDto() {
    BeerDto beer = this.createBeer();
    String serializedBeer = mapper.writeValueAsString(beer);
    log.info(serializedBeer);
  }
}
