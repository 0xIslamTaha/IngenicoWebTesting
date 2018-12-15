package com.ingenico.webtesting;
import com.ingenico.connect.gateway.sdk.java.defaultimpl.AuthorizationType;
import com.ingenico.connect.gateway.sdk.java.defaultimpl.DefaultAuthenticator;
import com.ingenico.connect.gateway.sdk.java.RequestHeader;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Test;
import com.google.gson.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;



public class TestIngenicoWebTesting {
    @Test
    public void OpenRedirectURL() throws URISyntaxException{
        DefaultAuthenticator authenticator = new DefaultAuthenticator(AuthorizationType.V1HMAC, "6ec1c4dc3e4df732",
                "MdFdQFUkZ0r+ww0DdKliXVodFypuQ47Cgg5cOeAYRVo=");

        List<RequestHeader> requestHeaderList = new ArrayList<RequestHeader>();
        requestHeaderList.add(new RequestHeader("Content-Type", "application/json; charset=UTF-8"));
        requestHeaderList.add(new RequestHeader("Date", "Fri, 14 Dec 2018 01:00:00 GMT"));
        requestHeaderList.add(new RequestHeader("X-GCS-MessageId", "6480071e-039d-4dca-a966-4ce3c1bc201b"));
        requestHeaderList.add(new RequestHeader("X-GCS-RequestId", "1cc6daff-a305-4d7b-94b0-c580fd5ba6b4"));

        String myUrl = "https://eu.sandbox.api-ingenico.com/v1/3142/hostedcheckouts";
        URI uriss = new URI(myUrl);

        String authenticationSignature = authenticator.createSimpleAuthenticationSignature("POST",
                uriss, requestHeaderList);

        RestAssured.baseURI ="https://eu.sandbox.api-ingenico.com/v1/3142";
        RequestSpecification request = RestAssured.given();
        //request.log().all();

        request.header("Content-Type", "application/json")
                .header("Date", "Fri, 14 Dec 2018 01:00:00 GMT")
                .header("X-GCS-MessageId", "6480071e-039d-4dca-a966-4ce3c1bc201b")
                .header("X-GCS-RequestId", "1cc6daff-a305-4d7b-94b0-c580fd5ba6b4")
                .header("Authorization", authenticationSignature);

        JsonObject amountOfMoney = new JsonObject();
        amountOfMoney.addProperty("currencyCode", "USD");
        amountOfMoney.addProperty("amount", 2345);


        JsonObject billingAddress = new JsonObject();
        billingAddress.addProperty("countryCode", "US");

        JsonObject customer = new JsonObject();
        customer.add("billingAddress", billingAddress);
        customer.addProperty("merchantCustomerId", "3142");

        JsonObject order = new JsonObject();
        order.add("amountOfMoney", amountOfMoney);
        order.add("customer", customer);

        JsonObject data = new JsonObject();
        data.add("order", order);

        request.body(data);

        Response response = (Response) request.post("/hostedcheckouts");

        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, 201);

        JsonParser parser = new JsonParser();
        JsonObject response_body = (JsonObject)parser.parse(response.asString());

        String URL = "https://payment." + response_body.get("partialRedirectUrl").getAsString();

        WebDriver driver=new ChromeDriver();
        driver.get(URL);
    }
}
