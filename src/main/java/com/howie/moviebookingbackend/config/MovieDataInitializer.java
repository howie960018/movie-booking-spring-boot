package com.howie.moviebookingbackend.config;

import com.howie.moviebookingbackend.entity.Movie;
import com.howie.moviebookingbackend.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Order(10)
public class MovieDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MovieDataInitializer.class);

    private final MovieRepository movieRepository;

    public MovieDataInitializer(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public void run(String... args) {
        // 既有標題集合，用於避免重複新增
        Set<String> existingTitles = movieRepository.findAll()
                .stream()
                .map(m -> m.getTitle() == null ? "" : m.getTitle().trim())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        List<Movie> candidates = new ArrayList<>();

        candidates.add(createMovie("Inception", "一名潛入他人夢境竊取機密的專家，接受一項將想法植入對方腦中的高難度任務。", LocalDate.of(2010, 7, 16), 148));
        candidates.add(createMovie("Interstellar", "面臨全球環境崩壞，太空探險隊穿越蟲洞尋找人類新家園。", LocalDate.of(2014, 11, 7), 169));
        candidates.add(createMovie("The Dark Knight", "蝙蝠俠對決瘋狂小丑，挑戰正義與秩序的極限。", LocalDate.of(2008, 7, 18), 152));
        candidates.add(createMovie("The Matrix", "一名駭客發現真實世界只是虛擬構造，踏上覺醒之路。", LocalDate.of(1999, 3, 31), 136));
        candidates.add(createMovie("Avatar", "人類前往外星潘朵拉星球，面臨文明衝突與抉擇。", LocalDate.of(2009, 12, 18), 162));
        candidates.add(createMovie("Titanic", "豪華郵輪上的動人愛情與災難命運。", LocalDate.of(1997, 12, 19), 195));
        candidates.add(createMovie("Gladiator", "被陷害的羅馬將軍成為角鬥士，展開復仇之路。", LocalDate.of(2000, 5, 5), 155));
        candidates.add(createMovie("The Shawshank Redemption", "兩名獄友在絕望中尋找希望與自由。", LocalDate.of(1994, 9, 23), 142));
        candidates.add(createMovie("Forrest Gump", "一名單純男子見證時代變遷的動人旅程。", LocalDate.of(1994, 7, 6), 142));
        candidates.add(createMovie("The Lord of the Rings: The Fellowship of the Ring", "九人遠征隊踏上銷毀魔戒的旅途。", LocalDate.of(2001, 12, 19), 178));
        candidates.add(createMovie("The Lord of the Rings: The Two Towers", "遠征繼續，邪惡勢力步步進逼。", LocalDate.of(2002, 12, 18), 179));
        candidates.add(createMovie("The Lord of the Rings: The Return of the King", "最終決戰，命運抉擇。", LocalDate.of(2003, 12, 17), 201));
        candidates.add(createMovie("The Avengers", "地球最強英雄集結對抗外星威脅。", LocalDate.of(2012, 5, 4), 143));
        candidates.add(createMovie("Avengers: Endgame", "英雄們逆轉時空，展開最後一戰。", LocalDate.of(2019, 4, 26), 181));
        candidates.add(createMovie("Spider-Man: No Way Home", "多重宇宙開啟，蜘蛛人面臨前所未有的挑戰。", LocalDate.of(2021, 12, 17), 148));
        candidates.add(createMovie("Joker", "社會邊緣人逐步走向瘋狂的起源故事。", LocalDate.of(2019, 10, 4), 122));
        candidates.add(createMovie("Parasite", "兩個階層家庭的命運交錯與反轉。", LocalDate.of(2019, 5, 30), 132));
        candidates.add(createMovie("La La Land", "在夢想與愛情之間的抉擇與追尋。", LocalDate.of(2016, 12, 9), 128));
        candidates.add(createMovie("Whiplash", "年輕鼓手與嚴苛導師的極限拉扯。", LocalDate.of(2014, 10, 10), 107));
        candidates.add(createMovie("Mad Max: Fury Road", "末日荒原中的驚險追逐與解放。", LocalDate.of(2015, 5, 15), 120));
        candidates.add(createMovie("Dune", "年輕公爵繼承人捲入沙丘星球的權力鬥爭。", LocalDate.of(2021, 10, 22), 155));
        candidates.add(createMovie("Oppenheimer", "原子彈之父的抉擇與矛盾。", LocalDate.of(2023, 7, 21), 180));
        candidates.add(createMovie("Barbie", "芭比踏入真實世界的奇幻旅程。", LocalDate.of(2023, 7, 21), 114));
        candidates.add(createMovie("Everything Everywhere All at Once", "平凡主婦穿梭多重宇宙與命運對抗。", LocalDate.of(2022, 3, 25), 139));
        candidates.add(createMovie("Top Gun: Maverick", "傳奇飛行員再度起飛，面對新世代挑戰。", LocalDate.of(2022, 5, 27), 130));

        List<Movie> toInsert = candidates.stream()
                .filter(m -> !existingTitles.contains(m.getTitle()))
                .collect(Collectors.toList());

        if (toInsert.isEmpty()) {
            log.info("Movie seed skipped (all preset titles already exist: {}).", existingTitles.size());
            return;
        }

        movieRepository.saveAll(toInsert);
        log.info("Seeded {} movies ({} already existed).", toInsert.size(), existingTitles.size());
    }

    private Movie createMovie(String title, String description, LocalDate releaseDate, int durationMinutes) {
        Movie m = new Movie();
        m.setTitle(title);
        m.setDescription(description);
        m.setReleaseDate(releaseDate);
        m.setDurationMinutes(durationMinutes);
        return m;
    }
}
