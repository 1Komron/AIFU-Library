package aifu.project.commondomain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
public class BaseBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String author;
    private String title;
    private String series;
    private String titleDetails;
    private Integer publicationYear;
    private String publisher;
    private String publicationCity;
    private String isbn;
    private Integer pageCount;
    private String language;
    private Double price;
    private String udc;



}
