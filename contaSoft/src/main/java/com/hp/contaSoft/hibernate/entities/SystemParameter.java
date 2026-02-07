package com.hp.contaSoft.hibernate.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "system_parameter")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SystemParameter extends Base {

    @Column(name = "name", nullable = false, length = 200, unique = true)
    private String name;

    @Column(name = "value", columnDefinition = "text")
    private String value;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    public SystemParameter(String name, String value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
    }
}
