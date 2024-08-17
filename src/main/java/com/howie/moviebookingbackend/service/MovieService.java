package com.howie.moviebookingbackend.service;

import com.howie.moviebookingbackend.entity.Movie;
import java.util.List;

public interface MovieService {
    List<Movie> getAllMovies();
    Movie getMovieById(Long id);
    Movie saveMovie(Movie movie);
    void deleteMovie(Long id);
    // 可以添加其他方法声明
}