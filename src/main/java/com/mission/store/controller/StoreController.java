package com.mission.store.controller;

import com.mission.store.dto.StoreDto;
import com.mission.store.dto.StoreRegistration;
import com.mission.store.dto.StoreSearchResult;
import com.mission.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/store/api/v1")
@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    /** 매장 등록 */
    @PostMapping("/store/register/{memberId}")
    public ResponseEntity<?> register(
            @PathVariable Long memberId
            , @Valid @RequestBody StoreRegistration request) {
        // TODO BindingResult 공부
        storeService.registerStore(memberId, request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    /** 매장 검색 */
    @GetMapping("/store/search")
    public ResponseEntity<Map<String, List<StoreSearchResult>>> searchStores(
            @RequestParam("name") String name) {
        List<StoreSearchResult> storeSearchResults = storeService.searchStoresByName(name);

        Map<String, List<StoreSearchResult>> response = new HashMap<>();
        response.put("storeSearchResults", storeSearchResults);

        return ResponseEntity.ok().body(response);
    }
    
    /** 매장 상세 보기 */
    @GetMapping("/store/{id}")
    public ResponseEntity<StoreDto> getStoreById(@PathVariable Long id) {
        return ResponseEntity.ok().body(storeService.getStoreById(id));
    }
}
