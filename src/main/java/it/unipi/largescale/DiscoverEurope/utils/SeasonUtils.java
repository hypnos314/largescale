package it.unipi.largescale.DiscoverEurope.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class SeasonUtils {

    public static Instant[] getNextSeasonRange(String season) {
        if (season == null) season = "spring";
        int currentYear = LocalDate.now().getYear();

        Instant[] range = calculateSeason(season, currentYear);
        if (range[1].isBefore(Instant.now())) {
            range = calculateSeason(season, currentYear + 1);
        }
        return range;
    }

    private static Instant[] calculateSeason(String season, int targetYear) {
        LocalDate start;
        LocalDate end;
        switch (season.toLowerCase()) {
            case "summer":
                start = LocalDate.of(targetYear, 6, 1);
                end = LocalDate.of(targetYear, 8, 31);
                break;
            case "autumn":
            case "fall":
                start = LocalDate.of(targetYear, 9, 1);
                end = LocalDate.of(targetYear, 11, 30);
                break;
            case "winter":
                start = LocalDate.of(targetYear, 12, 1);
                end = LocalDate.of(targetYear + 1, 2, 28);
                break;
            case "spring":
            default:
                start = LocalDate.of(targetYear, 3, 1);
                end = LocalDate.of(targetYear, 5, 31);
                break;
        }
        return new Instant[]{
                start.atStartOfDay().toInstant(ZoneOffset.UTC),
                end.atTime(23, 59, 59).toInstant(ZoneOffset.UTC)
        };
    }
}