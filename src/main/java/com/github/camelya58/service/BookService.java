package com.github.camelya58.service;

import com.github.camelya58.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

/**
 * Class BookService contains methods to map java object to xml and xml to java object.
 *
 * @author Kamila Meshcheryakova
 * created 07.09.2020
 */
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
