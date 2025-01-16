package com.rahulshettyacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rahulshettyacademy.controller.Book;

import java.util.List;

@Repository
public interface LibraryRepository extends JpaRepository<Book, String> {

    List<Book> findAllByAuthor(String authorName);

    Book findByBookName(String bookName);

}
