package com.CactiEncyclopedia.client;

import com.CactiEncyclopedia.domain.binding.AddFactDto;
import com.CactiEncyclopedia.domain.binding.FactDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "facts-svc", url = "${facts-svc.base-url}")
public interface FactClient {

    @GetMapping("/random")
    ResponseEntity<FactDto> randomFact();

    @PostMapping
    ResponseEntity<Void> addFact(@RequestBody AddFactDto addFactDto);

}
