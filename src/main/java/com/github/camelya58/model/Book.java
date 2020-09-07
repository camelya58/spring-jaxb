package com.github.camelya58.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.time.LocalDate;
import java.util.Date;

/**
 * Class Book is a main entity.
 *
 * @author Kamila Meshcheryakova
 * created 07.09.2020
 */
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
