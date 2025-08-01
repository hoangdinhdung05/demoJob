package com.demoJob.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "tbl_skill")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Skill extends AbstractEntity<Long> {

    @NotBlank(message = "Tên skill không được bỏ trống")
    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "skills")
    @JsonIgnore
    private List<Job> jobs;

}
