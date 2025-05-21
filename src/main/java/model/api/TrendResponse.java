package model.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// JSON data layer 1
@JsonIgnoreProperties(ignoreUnknown = true)
public record TrendResponse(int page, List<MovieItem> results) { }
