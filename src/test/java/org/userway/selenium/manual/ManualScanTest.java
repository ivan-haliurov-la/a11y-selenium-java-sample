package org.userway.selenium.manual;

import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.levelci.selenium.AccessibilityAuditor;
import org.levelci.selenium.model.config.AnalysisConfig;
import org.levelci.selenium.model.config.AuditConfig;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class ManualScanTest {

    private static WebDriver driver;

    private static final String REPORTS_PATH = "." + File.separator + "level-ci" + File.separator + "level-ci-reports";

    @BeforeAll
    public static void setup() {
        var options = new ChromeOptions();
        options.addArguments(
                "--headless=new",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    @SneakyThrows
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Should scan page and save Level CI scope report")
    void shouldScanPageAndSaveReport() {
        driver.get("https://www.w3.org/WAI/test-evaluate/preliminary/#images");

        var analysisConfig = AnalysisConfig.builder()
                .reportPath(REPORTS_PATH)
                .build();

        var auditConfig = AuditConfig.builder()
                .driver(driver)
                .analysisConfiguration(analysisConfig)
                .saveReport(true)
                .build();

        var result = AccessibilityAuditor.levelAnalyze(auditConfig);

        assertThat(result.getError()).isNull();
        assertThat(result.getReport()).isNotNull();
        assertThat(result.getIssuesFound()).isGreaterThanOrEqualTo(0);

        var scopeReports = new File(REPORTS_PATH, "scope-reports");
        assertThat(scopeReports).exists();
        assertThat(scopeReports).isDirectory();
        assertThat(scopeReports.list()).isNotEmpty();
    }
}
