package com.rahulshettyacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.rahulshettyacademy.controller.Library;

import java.util.List;

@Repository
public interface LibraryRepository extends JpaRepository<Library, String> {

    List<Library> findAllByAuthor(String authorName);

    Library findByBookName(String bookName);

}
