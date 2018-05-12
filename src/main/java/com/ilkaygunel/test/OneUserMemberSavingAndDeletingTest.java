package com.ilkaygunel.test;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.ilkaygunel.pojo.MemberSavingData;
import com.ilkaygunel.util.LoggingUtil;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import junit.framework.TestCase;

import static io.restassured.RestAssured.given;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OneUserMemberSavingAndDeletingTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		RestAssured.baseURI = "http://localhost:8080/MemberRestAPIProject";
	}

	@Test
	public void testOneUserMemberSavingAndDeleting() {
		String dataAsJson = prepareMemberSavingDataAsJson();

		Response response = given().auth().basic("ilkgunel93@gmail.com", "TEST1234").contentType("application/json")
				.body(dataAsJson).when().post("/memberPostWebServiceEndPoint/saveOneUserMember").then().statusCode(200)
				.extract().response();

		LoggingUtil loggingUtil = new LoggingUtil();
		Logger logger = loggingUtil.getLoggerForMemberSaving(this.getClass());
		logger.log(Level.INFO, response.getBody().asString());

		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(response.getBody().prettyPrint()).getAsJsonObject();
		JsonArray jsonArray = obj.get("memberList").getAsJsonArray();
		String id = "";
		for (JsonElement pa : jsonArray) {
			JsonObject paymentObj = pa.getAsJsonObject();
			id = paymentObj.get("id").getAsString();
		}
		response = given().auth().basic("ilkgunel93@gmail.com", "TEST1234").when()
				.delete("/memberDeleteWebServiceEndPoint/deleteOneUserMember/"+id).then().statusCode(200).extract()
				.response();
		
		logger.log(Level.INFO, response.getBody().asString());

	}

	private String prepareMemberSavingDataAsJson() {
		MemberSavingData memberSavingData = new MemberSavingData();
		memberSavingData.setFirstName("Michael");
		memberSavingData.setLastName("Schumacher");
		memberSavingData.setEmail("ilkay.gunel@kod5.org");
		memberSavingData.setMemberLanguageCode("tr");
		memberSavingData.setPassword("TEST1234");

		Gson gson = new Gson();
		return gson.toJson(memberSavingData);
	}
}
