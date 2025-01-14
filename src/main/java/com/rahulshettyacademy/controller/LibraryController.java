package com.rahulshettyacademy.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahulshettyacademy.repository.LibraryRepository;
import com.rahulshettyacademy.service.LibraryService;


@RestController
public class LibraryController {

    public LibraryController(@Lazy LibraryRepository repository) {
        super();
        this.repository = repository;

    }
    @Autowired
    LibraryRepository repository;

    @Autowired
    ProductsPrices productPrices;

    @Autowired
    LibraryService libraryService;

    //	@Autowired
//	SpecificProduct specificProduct;


    String baseUrl = "http://localhost:8181";

    private static final Logger logger = LoggerFactory.getLogger(LibraryController.class);

    @PostMapping("/addBook")
    public ResponseEntity addBook(@RequestBody Library library) {
        String id = libraryService.buildId(library.getIsbn(), library.getAisle());//dependenyMock
        AddResponse addResponse = new AddResponse();

        if (!libraryService.checkBookAlreadyExist(id))//mock
        {
            logger.info("Book do not exist so creating one");
            library.setId(id);
            repository.save(library);//mock
            HttpHeaders headers = new HttpHeaders();
            headers.add("unique", id);

            addResponse.setMsg("Success Book is Added");
            addResponse.setId(id);
            //return addResponse;
            return new ResponseEntity<AddResponse>(addResponse, headers, HttpStatus.CREATED);
        } else {
            logger.info("Book  exist so skipping creation");
            addResponse.setMsg("Book already exist");
            addResponse.setId(id);
            return new ResponseEntity<AddResponse>(addResponse, HttpStatus.ACCEPTED);
        }
    }

    @CrossOrigin
    @RequestMapping("/getBooks/{id}")
    public Library getBookById(@PathVariable(value = "id") String id) {
        try {
            Library lib = repository.findById(id).get();
            return lib;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @GetMapping("getBooks/author")
    public List<Library> getBookByAuthorName(@RequestParam(value = "authorname") String authorname) {
        return repository.findAllByAuthor(authorname);
    }

    @PutMapping("/updateBook/{id}")
    public ResponseEntity<Library> updateBook(@PathVariable(value = "id") String id, @RequestBody Library library) {
        //	Library existingBook = repository.findById(id).get();//mock
        Library existingBook = libraryService.getBookById(id);

        existingBook.setAisle(library.getAisle());//mock
        existingBook.setAuthor(library.getAuthor());
        existingBook.setBookName(library.getBookName());
        repository.save(existingBook);//
        //
        return new ResponseEntity<Library>(existingBook, HttpStatus.OK);
    }

    @DeleteMapping("/deleteBook")
    public ResponseEntity<String> deleteBookById(@RequestBody Library library) {
        //	Library libdelete =repository.findById(library.getId()).get();
        Library libdelete = libraryService.getBookById(library.getId());//mock
        repository.delete(libdelete);

        logger.info("Book  is deleted ");
        return new ResponseEntity<>("Book is deleted", HttpStatus.CREATED);

    }

    @GetMapping("/getBooks")
    public Iterable<Library> getBooks() {
        return repository.findAll();
    }


    @GetMapping("/getProductDetails/{name}")
    public SpecificProduct getProductFullDetails(@PathVariable(value = "name") String name) throws JsonMappingException, JsonProcessingException {

        SpecificProduct specificProduct = new SpecificProduct();

        Library lib = repository.findByBookName(name);
        specificProduct.setProduct(lib);

        try {
            //Call Courses API
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/getCourseByName/" + name, String.class);
            ObjectMapper mapper = new ObjectMapper();

            AllCourseDetails allCourseDetails = mapper.readValue(response.getBody(), AllCourseDetails.class);

            //retrieve category and price from courses API
            specificProduct.setCategory(allCourseDetails.getCategory());
            specificProduct.setPrice(allCourseDetails.getPrice());

        } catch (HttpClientErrorException e) {
            System.out.println("client exc: " + e.getMessage());
            if (e.getStatusCode().is4xxClientError()) {
                specificProduct.setMsg(name + "Category and price details are not available at this time");
            }
        }

        return specificProduct;
    }

    @CrossOrigin
    @GetMapping("/getProductPrices")
    public ProductsPrices getProductPrices() throws JsonMappingException, JsonProcessingException {
        productPrices.setBooksPrice(250);

        //Call Courses API to fetch all courses and extract their prices and calculate total sum of prices
        long sum = 0;
        for (int i = 0; i < getAllCoursesDetails().length; i++) {
            sum = sum + getAllCoursesDetails()[i].getPrice();
        }

        productPrices.setCoursesPrice(sum);

        return productPrices;
    }

    public void setBaseUrl(String url) {
        baseUrl = url;
    }

    public AllCourseDetails[] getAllCoursesDetails() throws JsonMappingException, JsonProcessingException {
        //Call Courses API
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/allCourseDetails", String.class);
        ObjectMapper mapper = new ObjectMapper();

        AllCourseDetails[] allCourseDetails = mapper.readValue(response.getBody(), AllCourseDetails[].class);
        return allCourseDetails;


    }

}


















