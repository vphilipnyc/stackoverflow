package ru.valerykorzh.springdemo.domain;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "tags")
//    @JoinTable(
//            name = "question_tag",
//            joinColumns = @JoinColumn(name = "tag_id", unique = false),
//            inverseJoinColumns = @JoinColumn(name = "question_id", unique = false))
    private Set<Question> questions;

    public Tag(String name) {
        this.name = name;
    }

    public void addQuestion(Question question) {
        if (questions == null) {
            questions = new HashSet<>();
        }
        questions.add(question);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return name.equals(tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
