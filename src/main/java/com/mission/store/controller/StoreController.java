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
            , @Valid @RequestBody StoreRegistration.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(storeService.registerStore(memberId, request));
    }
    
    /** 매장 전체 보기 */
    @GetMapping("/stores")
    public ResponseEntity<?> getStores() {
        List<StoreDto> stores = storeService.getStores();

        Map<String, Object> response = new HashMap<>();
        response.put("stores", stores);
        return ResponseEntity.ok().body(response);
    }
    
    /** 매장 상세 보기 */
    @GetMapping("/stores/{id}")
    public ResponseEntity<?> getStoreById(@PathVariable Long id) {
        return ResponseEntity.ok().body(storeService.getStoreById(id));
    }

    /** 매장 검색 */
    @GetMapping("/stores/search")
    public ResponseEntity<?> searchStores(
            @RequestParam("name") String name) {
        List<StoreSearchResult> storeSearchResults = storeService.searchStoresByName(name);

        Map<String, Object> response = new HashMap<>();
        response.put("storeSearchResults", storeSearchResults);

        return ResponseEntity.ok().body(response);
    }

    /** 점주가 관리하는 매장 조회 */
    @GetMapping("/stores/owner/{id}")
    public ResponseEntity<?> getStoresByOwnerId(@PathVariable Long id) {
        List<StoreDto> stores = storeService.getStoresByOwnerId(id);

        Map<String, Object> response = new HashMap<>();
        response.put("stores", stores);
        return ResponseEntity.ok().body(response);
    }
}
