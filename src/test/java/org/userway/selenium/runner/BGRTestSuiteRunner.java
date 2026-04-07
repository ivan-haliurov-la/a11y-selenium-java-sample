package org.userway.selenium.runner;

import org.junit.platform.suite.api.AfterSuite;
import org.junit.platform.suite.api.BeforeSuite;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.userway.selenium.model.config.AnalysisConfig;
import org.userway.selenium.model.config.AuditConfig;
import org.userway.selenium.model.config.Rule;

import java.time.Duration;
import java.util.Set;

@Suite
@SelectPackages("org.userway.selenium.runner")
public class BGRTestSuiteRunner {

    @BeforeSuite
    static void setup() {
        var backgroundRunner = UserWayBackgroundRunner.getInstance();
        backgroundRunner.setGlobalAuditConfig(
                AuditConfig.builder()
                        .strict(false)
                        .auditTimeout(Duration.ofMinutes(20))
                        .analysisConfiguration(
                                AnalysisConfig.builder()
                                        .includeRules(Set.of(Rule.SELECT_NAME))
                                        .reportPath("./ca11y/uw-a11y-reports")
                                        .build()
                        )
                        .saveReport(true)
                        .build()
        );
        backgroundRunner.enableBackgroundRunner();

        // For clean logs
//        System.setErr(new PrintStream(OutputStream.nullOutputStream()));
    }

    @AfterSuite
    static void teardown() {
        UserWayBackgroundRunner.getInstance().disableBackgroundRunner();
    }
}
