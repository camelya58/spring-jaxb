# spring-jaxb

Simple project using Spring Boot, Swagger and JAXB.

## Step 1
Create maven project and add dependencies:
```xml
 <project>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.3.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <groupId>com.github.camelya58</groupId>
    <artifactId>spring-jaxb</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- JAXB -->
        <dependency>
            <groupId>javax.xml.bind</groupId>
            <artifactId>jaxb-api</artifactId>
            <version>2.3.1</version>
        </dependency>
        <dependency>
            <groupId>com.sun.xml.bind</groupId>
            <artifactId>jaxb-impl</artifactId>
            <version>2.3.3</version>
        </dependency>

        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.8.6</version>
        </dependency>
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.6</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <!-- swagger -->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>
        <!-- tests -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>jaxb2-maven-plugin</artifactId>
                <version>2.5.0</version>
                <executions>
                    <execution>
                        <id>xjc</id>
                        <goals>
                            <goal>xjc</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <packageName>com.github.camelya58.common.xml</packageName>
                    <sources>
                        <source>src/main/resources/user.xsd</source>
                    </sources>
                    <encoding>UTF-8</encoding>
                    <arguments>
                        <argument>-XautoNameResolution</argument>
                    </arguments>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## Step 2
Create model Book and add annotations.
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "book")
@XmlType(propOrder = { "id", "name", "author", "date" })
public class Book {
    private Long id;
    private String name;
    private String author;
    private int year;
    private Date date;

    @XmlAttribute
    public void setId(Long id) {
        this.id = id;
    }

    @XmlElement(name = "title")
    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public void setYear(int year) {
        this.year = year;
    }
}
```
***@XmlRootElement***: the name of the root XML element is derived from the class name 
and we can also specify the name of the root element of the XML using its name attribute.

***@XmlType***: define the order in which the fields are written in the XML file.

***@XmlElement***: define the actual XML element name which will be used.

***@XmlAttribute***: define the id field is mapped as an attribute instead of an element.

***@XmlTransient***: annotate fields that we don't want to be included in XML.

## Step 3
Create simple rest-controller which allows to receive java object from xml and xml file from java object.
```java
@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/javaObject")
    public Book createBookFromXml() {
        return bookService.createBook();
    }

    @GetMapping("/xml")
    public String createXml() {
        bookService.createXml();
        return "Xml file has created";
    }
}
```

## Step 4
Add swagger configurations.
```java
@Configuration
@EnableSwagger2
public class SwaggerConfiguration implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui.html");
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.camelya58.controller"))
                .paths(PathSelectors.any())
                .build();

    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Book store")
                .description("Service for creating files from templates.")
                .build();
    }
}
```

## Step 5
Create a service with 2 methods.
```java
@Service
@RequiredArgsConstructor
public class BookService {

    private JAXBContext context;
    private final String filesFolder = "/home/camelya/IdeaProjects/spring-jaxb/src/main/resources/files";
    private final String filename = "Book1.xml";

    @PostConstruct
    void init() throws JAXBException {
        context = JAXBContext.newInstance(Book.class);
    }

    public void createXml() {
        Book book = new Book(1L, "Book1", "Author1", 1950, new Date());

        try {
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            Path dir = Path.of(filesFolder);
            Path pathToFile = dir.resolve(book.getName().concat(".xml"));

            if (Files.notExists(pathToFile)) {
                Files.createDirectories(dir);
                Files.createFile(pathToFile);
            }
            mar.marshal(book, pathToFile.toFile());
        } catch (Exception e) {
            throw new RuntimeException("Can't write the file");
        }
    }

    public Book createBook() {
        try {
            Book book = (Book) context.createUnmarshaller()
                           .unmarshal(Path.of(filesFolder, filename).toFile());
            book.setName("Book2");
            book.setAuthor("Author2");
            return book;
        } catch (JAXBException e) {
            throw new RuntimeException("Can't read the file");
        }
    }
}
```
The first method **createXml()** allows to create xml from our java object *"Book"* and save it by the given path.

The second method **createBook()** allows to create java object *"Book"* from a given template *"Book1.xml"*
which situated by given path. And can change any fields in our object.

*Source: https://www.baeldung.com/jaxb.*
