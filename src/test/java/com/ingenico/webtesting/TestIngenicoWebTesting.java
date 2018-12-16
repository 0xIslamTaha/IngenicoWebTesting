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
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit ;


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

        URI url = new URI("https://eu.sandbox.api-ingenico.com/v1/3142/hostedcheckouts");

        String authenticationSignature = authenticator.createSimpleAuthenticationSignature("POST",
                url, requestHeaderList);

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
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        WebDriverWait wait = new WebDriverWait(driver,30);
        driver.manage().window().maximize();

        List<WebElement> paymentMethods =   driver.findElement(By.id("paymentoptionslist")).findElements(By.tagName("li"));
        for (WebElement paymentElement : paymentMethods){
            if (paymentElement.getText().equals("MasterCard Debit")){
                System.out.println(paymentElement.getText());
                paymentElement.click();
                break;
            }
        }

        WebElement cardNumber =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cardNumber")));
        cardNumber.sendKeys("5399 9999 9999 9999");

        driver.findElement(By.id("expiryDate")).clear();
        driver.findElement(By.id("expiryDate")).sendKeys("11/21");
        driver.findElement(By.id("cvv")).clear();
        driver.findElement(By.id("cvv")).sendKeys("585");
        driver.findElement(By.id("primaryButton")).click();
    }
}
