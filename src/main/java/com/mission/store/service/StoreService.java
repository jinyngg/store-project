package com.mission.store.service;

import com.mission.store.dto.StoreDto;
import com.mission.store.dto.StoreRegistration;
import com.mission.store.dto.StoreSearchResult;

import java.util.List;

public interface StoreService {
    StoreRegistration.Response registerStore(Long memberId, StoreRegistration.Request request);

    List<StoreDto> getStores();

    StoreDto getStoreById(Long storeId);

    List<StoreSearchResult> searchStoresByName(String name);

    List<StoreDto> getStoresByOwnerId(Long memberId);
}