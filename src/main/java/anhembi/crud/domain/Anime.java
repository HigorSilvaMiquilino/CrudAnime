package anhembi.crud.domain;


import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class    Anime {
    Integer id;
    String name;
    int episodes;
    Producer  producer;
}
