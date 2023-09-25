package guru.springframework.msscbeerservice.web.controller;

import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.model.BeerStyleEnum;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class BaseTest {
  BeerDto createBeer()
  {
    return BeerDto.builder()
        .id(UUID.randomUUID())
        .beerName("randomBeer")
        .beerStyle(BeerStyleEnum.LAGER)
        .price(BigDecimal.valueOf(100))
        .upc("1234456789")
        .quantityOnHand(1)
        .version(1)
        .createdDate(OffsetDateTime.now())
        .build();
  }
}
