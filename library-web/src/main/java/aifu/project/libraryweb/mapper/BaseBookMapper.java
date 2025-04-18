package aifu.project.libraryweb.mapper;

import aifu.project.commondomain.entity.BaseBook;
import aifu.project.libraryweb.dto.BaseBookDTO;


public class BaseBookMapper {

     //Entitydan dan Dtoga uzgartirish
    public static BaseBookDTO toDTO(BaseBook baseBook) {
     return BaseBookDTO.builder()
             .auther(baseBook.getAuthor())
             .title(baseBook.getTitle())
             .isbn(baseBook.getIsbn())
             .price(baseBook.getPrice())
             .language(baseBook.getLanguage())
             .udc(baseBook.getUdc())
             .pageCount(baseBook.getPageCount())
             .publicationCity(baseBook.getPublicationCity())
             .publicationYear(baseBook.getPublicationYear())
             .publisher(baseBook.getPublisher())
             .series(baseBook.getSeries())
             .titleDetails(baseBook.getTitleDetails())
             .build();
    }
    public static BaseBook fromEntity(BaseBookDTO dto) {
             return BaseBook.builder()
                     .author(dto.getAuther())
                     .publicationCity(dto.getPublicationCity())
                     .publicationYear(dto.getPublicationYear())
                     .publisher(dto.getPublisher())
                     .series(dto.getSeries())
                     .title(dto.getTitle())
                     .isbn(dto.getIsbn())
                     .language(dto.getLanguage())
                     .udc(dto.getUdc())
                     .pageCount(dto.getPageCount())
                     .publicationCity(dto.getPublicationCity())
                     .publicationYear(dto.getPublicationYear())
                     .publisher(dto.getPublisher())
                     .series(dto.getSeries())
                     .titleDetails(dto.getTitleDetails())
                     .build();

    }

}
