package aifu.project.libraryweb.controller;

import aifu.project.commondomain.entity.BookCopy;
import aifu.project.libraryweb.dto.BookCopyDTO;

import aifu.project.libraryweb.exsiption.ResponseMessage;
import aifu.project.libraryweb.service.BaseBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BaseBookController {

    private final BaseBookService service;

    @PostMapping
    public ResponseEntity<BookCopyDTO> create(@RequestBody BookCopyDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {

        List<BookCopy> bookCopies = service.getAll();

        if (bookCopies == null) {
            ResponseMessage response = new ResponseMessage(
                    "Hech qanday kitob nusxasi topilmadi",
                    HttpStatus.NOT_FOUND.value()
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(bookCopies);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        Optional<BookCopyDTO> optionalBookCopy = Optional.ofNullable(service.getById(id));

        if (optionalBookCopy.isPresent()) {
            return ResponseEntity.ok(optionalBookCopy.get());
        } else {
            ResponseMessage response = new ResponseMessage(
                    "Berilgan IDga mos kitob nusxasi topilmadi: " + id,
                    HttpStatus.NOT_FOUND.value()
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<BookCopyDTO> update(@PathVariable Integer id, @RequestBody BookCopyDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }



}

