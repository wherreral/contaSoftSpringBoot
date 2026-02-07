package com.hp.contaSoft.hibernate.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "afp_factor_nickname", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"afp_factor_id", "family_id"})
})
@Getter
@Setter
public class AfpFactorNickname {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "afp_factor_id", nullable = false)
    private long afpFactorId;

    @Column(name = "family_id", nullable = false)
    private String familyId;

    @Column(name = "nickname")
    private String nickname;

    public AfpFactorNickname() {
    }

    public AfpFactorNickname(long afpFactorId, String familyId, String nickname) {
        this.afpFactorId = afpFactorId;
        this.familyId = familyId;
        this.nickname = nickname;
    }
}
