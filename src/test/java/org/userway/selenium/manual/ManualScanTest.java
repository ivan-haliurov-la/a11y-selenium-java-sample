package org.userway.selenium.manual;

import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.userway.selenium.AccessibilityAuditor;
import org.userway.selenium.model.AnalysisStatus;
import org.userway.selenium.model.config.AnalysisConfig;
import org.userway.selenium.model.config.AuditConfig;
import org.userway.selenium.model.report.AnalysisLevel;

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
        driver.get("https://www.w3.org/WAI/planning/");

        var analysisConfig = AnalysisConfig.builder()
                .level(AnalysisLevel.AAA)
                .includeBestPractices(true)
                .includeExperimental(true)
                .reportPath(REPORTS_PATH)
                .build();

        var auditConfig = AuditConfig.builder()
                .driver(driver)
                .analysisConfiguration(analysisConfig)
                .saveReport(true)
                .build();

        var result = AccessibilityAuditor.levelAnalyze(auditConfig);

        assertThat(result.getStatus()).isEqualTo(AnalysisStatus.SUCCEEDED);
        assertThat(result.getIssuesFound()).isGreaterThanOrEqualTo(0);

        var scopeReports = new File(REPORTS_PATH, "scope-reports");
        assertThat(scopeReports).exists();
        assertThat(scopeReports).isDirectory();
        assertThat(scopeReports.list()).isNotEmpty();
    }
}
