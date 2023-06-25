package com.mission.store.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDto {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpireDate;

    /** milliseconds 변환 */
    public static String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        String duration = String.format("%d일 %02d:%02d:%02d",
                days,
                hours % 24,
                minutes % 60,
                seconds % 60);

        return duration;
    }
}
