# LevelCI Java Selenium accessibility testing example

This is a sample project which shows use cases of LevelCI's 
Java Selenium Accessibility testing integration. There are two ways 
of using the integration - manual and background runner mode.

To use LevelCI Selenium testing solution you should first of all
install all the required dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>0.0.10</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.userway</groupId>
        <artifactId>a11y-selenium-java</artifactId>
        <version>${userway.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <!-- You can use any other Slf4j provider you like -->
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>${logback.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

1. Manual mode
To run tests in manual mode you should prepare analysis configuration
and execute analysis with it. You can find example of manual run
under the package `org.userway.selenium.manual`.

To run only manual mode tests you should go to pom.xml and configure 
Maven Surefire plugin to exclude Background Runner tests in 
package `org.userway.selenium.runner`:

```xml
<configuration>
    <excludes>
      <exclude>**/org/userway/selenium/runner/*.java</exclude>
    </excludes>
<!--    It should be commented-->
<!--    <includes>-->
<!--        <include>**/BGRTestSuiteRunner.java</include>-->
<!--    </includes>-->
</configuration>
```

And then you can run Manual-mode tests only:
```shell
mvn clean test
```

2. Background Runner mode 
In LevelCI Background Runner mode you use our proxy `WebDriver` instance
that you can acquire by executing the following code:

```java
// Define your WebDriver instance
var myDriver = new ChromeDriver(); // Can also be FirefoxDriver and SafariDriver

// Get global UserWayBackgroundRunner instance
var bgRunner = UserWayBackgroundRunner.getInstance();

// Reassign your driver variable with LevelCI proxy by calling watchDriver
myDriver = bgRunner.watchDriver(driver, "BGRInlineUseCase-test_1"); // Second argument is optional and affects only log messages
```

Now the proxy instance of `WebDriver` can be used in your tests like 
any other `WebDriver` but it will implicitly execute LevelCI analysis
each time you change state of tested page by, for example, calling `myDriver.get('...')` method.

Background Runner should be configured before all tests that use it and 
also should be closed after all these tests completed. If you want to use
background runner in many test-classes, we recommend you use JUnit Suite library
to create test suites. For this you should just add the following dependency in
your pom.xml file:

```xml
<dependencies>
    <dependency>
        <groupId>org.junit.platform</groupId>
        <artifactId>junit-platform-suite</artifactId>
        <version>${junit.suite.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

Now you can create a package and add a class annotated with `@Suite` which
defines this class as a test suite entry point. You should also add 
`@SelectPackages` or/and `@SelectClasses` annotation to define test classes you 
want to be included in this test suite, e.g. `@SelectPackages("org.userway.selenium.runner")`

In this class you can add two methods annotated with `@BeforeSuite` and `@AfterSutie`
that will be executed before and after the test suite respectively.
- In `@BeforeSuite` method you should get global instance of LevelCI Background Runner
and set global audit configuration that will be used for each background scan.
After this you must enable monitoring by calling `bgRunner.enableBackgroundRunner()`.
- In `@AfterSuite` method you should just disable background runner by calling 
`bgRunner.disableBackgroundRunner()`.

To run only background runner mode tests you should go to pom.xml and configure
Maven Surefire plugin to include only Background Runner tests by implicitly
specify that only class annotated with `@Suite` should be executed.

```xml
<configuration>
<!--    <excludes>-->
<!--        <exclude>**/org/userway/selenium/runner/*.java</exclude>-->
<!--    </excludes>-->
    <includes>
        <include>**/BGRTestSuiteRunner.java</include>
    </includes>
</configuration>
```

And then you can run Manual-mode tests only:
```shell
mvn clean test
```

NOTE: We use Logback as implementation of Slf4j in this sample project. 
If you want to change log level for tests, you should update configuration
in `src/test/resources/logback.xml` file.