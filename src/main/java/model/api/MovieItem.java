package model.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/* JSON data layer 2 (result set) */
// 只取得需要的資料，沒寫的欄位會自動忽略
@JsonIgnoreProperties(ignoreUnknown = true)

// Record 會自動生成 constructor, getter, toString()
public record MovieItem(
        int id,
        String title,
        double popularity,
        // 和原始資料不同名稱須加上 @JsonProperty
        @JsonProperty("vote_average") 
        double voteAverage) {}

