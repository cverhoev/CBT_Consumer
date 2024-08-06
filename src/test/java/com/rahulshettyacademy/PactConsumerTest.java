package com.rahulshettyacademy;

import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahulshettyacademy.controller.LibraryController;
import com.rahulshettyacademy.controller.ProductsPrices;
import com.rahulshettyacademy.controller.SpecificProduct;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.annotations.Pact;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "CoursesCatalogue")
public class PactConsumerTest {
	
	@Autowired
	private LibraryController libraryController;
	
//	@Pact(consumer="BooksCatalogue")
//	public V4Pact PactallCoursesDetailsConfig(PactDslWithProvider builder)
//	{
//		return builder.given("courses exist")
//		.uponReceiving("getting all courses details")
//		.path("/allCourseDetails")
//		.willRespondWith()
//		.status(200)
//		.body(PactDslJsonArray.arrayMinLike(3)
//				.stringType("course_name")
//				.stringType("id")
//				.integerType("price", 10)
//				.stringType("category").closeObject()).toPact(V4Pact.class);
//					
//		
//	}
	//  PactFlow
	
	// -> PactFlow Server  (contract file to server)

	@Pact(consumer="BooksCatalogue")
	public V4Pact PactallCoursesDetailsPriceCheck(PactDslWithProvider builder)
	{
		return builder.given("courses exist")
		.uponReceiving("getting all courses details")
		.path("/allCourseDetails")
		.willRespondWith()
		.status(200)
		.body(PactDslJsonArray.arrayMinLike(3)
				.integerType("price", 10)
				.closeObject()).toPact(V4Pact.class);
	}
		
	@Pact(consumer = "BooksCatalogue")
	public V4Pact getCourseByName(PactDslWithProvider builder)
	{
		return builder.given("Course Appium exist")
		.uponReceiving("Get the Appium course details")
		.path("/getCourseByName/Appium")
		.willRespondWith()
		.status(200)
		.body(new PactDslJsonBody()
				.integerType("price",10)
				.stringType("category","mobile")).toPact(V4Pact.class);

	}
	@Pact(consumer = "BooksCatalogue")
	public V4Pact getCourseByNameNotExist(PactDslWithProvider builder)
	{
		return builder.given("Course Appium does not exist","name","Appium")
				.uponReceiving("Appium course Does not exist")
				.path("/getCourseByName/Appium")
				.willRespondWith()
				.status(404)
				.toPact(V4Pact.class);
	}

	@Test
	@PactTestFor(pactMethod="PactallCoursesDetailsPriceCheck",port = "9999")
	public void testAllProductsSum(MockServer mockServer) throws JsonMappingException, JsonProcessingException
	{
		String expectedJson ="{\"booksPrice\":250,\"coursesPrice\":30}";
		libraryController.setBaseUrl(mockServer.getUrl());
		
		ProductsPrices productsPrices = libraryController.getProductPrices();
		ObjectMapper obj = new ObjectMapper();
		String jsonActual = obj.writeValueAsString(productsPrices);
		
		assertEquals(expectedJson, jsonActual);
	}
	
	@Test
	@PactTestFor(pactMethod="getCourseByName")
	
	public void testByProductName(MockServer mockServer) throws JsonMappingException, JsonProcessingException
	{
		String expectedJson = "{\"product\":{\"bookName\":\"Appium\",\"id\":\"ttefs36\",\"isbn\":\"ttefs\",\"aisle\":36,\"author\":\"Shetty\"},\"price\":10,\"category\":\"mobile\"}";
		libraryController.setBaseUrl(mockServer.getUrl());

		//calling getproductfulldetails methos with appium value
		SpecificProduct specificProduct = libraryController.getProductFullDetails("Appium");
		
		ObjectMapper obj = new ObjectMapper();
		String jsonActual = obj.writeValueAsString(specificProduct);
		
		assertEquals(expectedJson, jsonActual);
	}

	@Test
	@PactTestFor(pactMethod="getCourseByNameNotExist",port = "9999")
	public void testByProductNameNotExist(MockServer mockServer) throws JsonMappingException, JsonProcessingException
	{
		String expectedJson = "{\"product\":{\"bookName\":\"Appium\",\"id\":\"ttefs36\",\"isbn\":\"ttefs\",\"aisle\":36,\"author\":\"Shetty\"},\"msg\":\"AppiumCategory and price details are not available at this time\"}";
		libraryController.setBaseUrl(mockServer.getUrl());

		SpecificProduct specificProduct =libraryController.getProductFullDetails("Appium");
		
		ObjectMapper obj = new ObjectMapper();
		String jsonActual = obj.writeValueAsString(specificProduct);
		
		assertEquals(expectedJson, jsonActual);
	}

	
	
	
	
	
	
	
	
	
	

	
	
}
