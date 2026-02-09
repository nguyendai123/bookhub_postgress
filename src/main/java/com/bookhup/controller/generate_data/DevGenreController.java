
package com.bookhup.controller.generate_data;

import com.bookhup.controller.dto.GenerateAuthorsRequest;
import com.bookhup.controller.service.DevAuthorService;
import com.bookhup.controller.service.DevGenreService;
import com.bookhup.model.Genre;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dev/genres")
@RequiredArgsConstructor
public class DevGenreController {

    private final DevGenreService devGenreService;

    @PostMapping("/generate")
    public ResponseEntity<List<Genre>> generateGenres(
            @RequestParam(defaultValue = "50") int count) {

        return ResponseEntity.ok(devGenreService.generateGenres(count));
    }
}


