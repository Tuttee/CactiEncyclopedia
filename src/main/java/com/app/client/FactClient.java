package com.app.client;

import com.app.domain.binding.AddFactDto;
import com.app.domain.binding.FactDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "facts-svc", url = "${facts-svc.base-url}")
//@FeignClient(name = "facts-svc", url = "http://localhost:8081/api/v1/facts")
public interface FactClient {

    @GetMapping("/random")
    ResponseEntity<FactDto> randomFact();

    @PostMapping
    ResponseEntity<Void> addFact(@RequestBody AddFactDto addFactDto);

}
