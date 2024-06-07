package com.woopaca.laboratory.lms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
public class LMSAuthenticationManager {

    private static final String LOGIN_USER_ID_INPUT = "login_user_id";
    private static final String LOGIN_USER_PASSWORD_INPUT = "login_user_password";
    private static final String SESSION_COOKIE_KEY = "_normandy_session";

    private final RestTemplate restTemplate = new RestTemplateBuilder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebDriver driver;

    public LMSAuthenticationManager() {
        WebDriverManager.chromedriver()
                .setup();

        ChromeOptions options = createChromeOptions();
        driver = new ChromeDriver(options);
    }

    @PreDestroy
    void tearDown() {
        driver.quit();
    }

    public String seleniumLogin(String id, String password) {
        driver.get("https://kncu.kongju.ac.kr/xn-sso/login.php?auto_login=&sso_only=&cvs_lgn=true&return_url=https%3A%2F%2Fkncu.kongju.ac.kr%2Fxn-sso%2Fgw-cb.php%3Ffrom%3Dweb_redirect%26login_type%3Dstandalone%26return_url%3Dhttps%253A%252F%252Fknulms.kongju.ac.kr%252Flearningx%252Flogin");

        WebElement idInput = driver.findElement(By.id(LOGIN_USER_ID_INPUT));
        WebElement passwordInput = driver.findElement(By.id(LOGIN_USER_PASSWORD_INPUT));
        idInput.sendKeys(id);
        passwordInput.sendKeys(password);

        WebElement loginButton = driver.findElement(By.cssSelector("div.login_btn a"));
        loginButton.click();

        WebDriver.Options manage = driver.manage();
        Cookie sessionCookie = manage.getCookieNamed(SESSION_COOKIE_KEY);
        return sessionCookie.getValue();
    }

    private ChromeOptions createChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--headless");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("User-Agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");
        options.addArguments("--headless");
        return options;
    }

    public boolean isAuthenticated(String sessionId) {
        return false;
    }

    public String getUserId(String sessionId) throws JsonProcessingException {
        String url = "https://knulms.kongju.ac.kr";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", String.join("", "_normandy_session=", sessionId));
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        String responseBody = responseEntity.getBody();
        String environment = responseBody.split("ENV = ")[1]
                .split(";")[0];

        Map<String, Object> environmentMap = objectMapper.readValue(environment, Map.class);
        return (String) environmentMap.get("current_user_id");
    }
}
